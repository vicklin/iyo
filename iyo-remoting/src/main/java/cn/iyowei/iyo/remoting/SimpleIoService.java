package cn.iyowei.iyo.remoting;

import cn.iyowei.iyo.remoting.channel.ChannelManager;
import cn.iyowei.iyo.remoting.channel.MultiplexingChannelManager;
import cn.iyowei.iyo.remoting.channel.PoolingChannelManager;
import cn.iyowei.iyo.remoting.codec.IoDecoder;
import cn.iyowei.iyo.remoting.codec.IoEncoder;
import cn.iyowei.iyo.remoting.session.IoSession;
import cn.iyowei.iyo.remoting.session.SessionManager;
import cn.iyowei.iyo.remoting.timeout.TimeoutManager;
import cn.iyowei.iyo.remoting.util.KeyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vick on 2016/8/8.
 */
public class SimpleIoService implements IoService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleIoService.class);

    private AtomicInteger idSeq = new AtomicInteger(0);

    private int nextIdSeq() {
        return idSeq.incrementAndGet() & 0xFFFFFFF;
    }

    private TimeoutManager timeoutManager;

    private ChannelManager channelManager;

    private SessionManager sessionManager;

    private SimpleIoService(Builder builder) {
        this.timeoutManager = builder.timeoutManager;
        this.channelManager = builder.channelManager;
        this.sessionManager = builder.sessionManager;
    }


    /**
     * 同步发送
     *
     * @param req
     * @return
     */
    @Override
    public IoResponse sendBlocking(final IoRequest req, long timeoutMills) throws Exception {
        final SynHolder<IoResponse> resultSynHolder = new SynHolder<IoResponse>(1);

        send(req, new IoCallback() {
            private AtomicBoolean isDone = new AtomicBoolean(false);

            @Override
            public void onSuccess(IoResponse response) {
                if (isDone.compareAndSet(false, true)) {
                    resultSynHolder.setResult(response);
                    resultSynHolder.countDown();
                }
            }

            @Override
            public void onFail(IoRequest request, Throwable ex) {
                if (isDone.compareAndSet(false, true)) {
//                    resultSynHolder.setResult(req);
                    resultSynHolder.countDown();
                }
            }
        }, timeoutMills);

        resultSynHolder.await();
        return resultSynHolder.getResult();
    }


    /**
     * 异步发送
     *
     * @param req
     * @param callback
     * @param timeoutMills
     * @return
     */
    @Override
    public Future send(final IoRequest req, final IoCallback callback, final long timeoutMills) throws Exception {
        req.setSeq(nextIdSeq());
        final Channel channel = channelManager.getChannel(req.getRouterAddr());
        IoSession session = new IoSession(timeoutMills, true, req, callback);
        final String key = KeyUtil.key(channel, req.getIoSeq());
        IoSession is = sessionManager.putIfAbsent(key, session);
        if (is != null) {
            throw new RuntimeException("Session conflict, channel:{} and seq:{} already exist in sessionMap.");
        }
        Future<?> timeoutTask = timeoutManager.onTimeout(new Runnable() {
            @Override
            public void run() {
                IoSession session = sessionManager.remove(key);
                if (null != session) {
                    session.onFail(new TimeoutException());
                }
            }
        }, timeoutMills);
        session.setTimeoutTask(timeoutTask);
        channel.writeAndFlush(req).addListener(session);
        return timeoutTask;
    }

    private static class SynHolder<T> {

        private CountDownLatch latch;

        private T result;

        SynHolder(int latchCount) {
            this.latch = new CountDownLatch(latchCount);
        }

        void countDown() {
            latch.countDown();
        }

        void await() throws InterruptedException {
            latch.await();
        }

        void setResult(T packet) {
            this.result = packet;
        }

        T getResult() {
            return result;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Builder() {
        }

        private int connectTimeout = 500;

        private Class<? extends ChannelManager> channelManagerClz;

        private TimeoutManager timeoutManager;

        private ChannelManager channelManager;

        private SessionManager sessionManager;

        private IoDecoder decoder;

        private IoEncoder encoder;

        public IoService build() {
            final SimpleChannelInboundHandler<IoResponse> handler = new IoResultHandler(sessionManager);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class)
                    .group(new NioEventLoopGroup())
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline p = channel.pipeline();
                            p.addLast("e", encoder).addLast("d", decoder).addLast("h", handler);
                        }
                    }).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);

            if (channelManagerClz == MultiplexingChannelManager.class) {
                this.channelManager = new MultiplexingChannelManager(bootstrap);
            } else {
                this.channelManager = new PoolingChannelManager(bootstrap);
            }
            return new SimpleIoService(this);
        }

        public Builder setTimeoutManager(TimeoutManager timeoutManager) {
            this.timeoutManager = timeoutManager;
            return this;
        }

        public Builder setChannelManager(Class<? extends ChannelManager> channelManagerClass) {
            this.channelManagerClz = channelManagerClass;
            return this;
        }

        public Builder setSessionManager(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
            return this;
        }

        public Builder setDecoder(IoDecoder decoder) {
            this.decoder = decoder;
            return this;
        }

        public Builder setEncoder(IoEncoder encoder) {
            this.encoder = encoder;
            return this;
        }
    }

}


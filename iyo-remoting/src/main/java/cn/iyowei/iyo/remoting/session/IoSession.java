package cn.iyowei.iyo.remoting.session;

import cn.iyowei.iyo.remoting.IoCallback;
import cn.iyowei.iyo.remoting.IoRequest;
import cn.iyowei.iyo.remoting.IoResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by vick on 2016/8/8.
 */
public class IoSession implements ChannelFutureListener {

    private long timeout;

    private boolean async;

    private IoCallback callback;

    private IoRequest request;

    private Future<?> timeoutTask;

    private AtomicBoolean isValid;  // 标记是否已被执行，理论上不需要，因为ConcurrentHashMap#remove已经保证，但是担心用别的容器，或者被遍历的方式remove

    public IoSession(long timeout, boolean async, IoRequest request, IoCallback callback) {
        this.timeout = timeout;
        this.async = async;
        this.callback = callback;
        this.request = request;
        this.isValid = new AtomicBoolean(true);
    }

    public void setTimeoutTask(Future<?> timeoutTask) {
        this.timeoutTask = timeoutTask;
    }

    public void onSuccess(IoResponse response) {
        if (isValid.compareAndSet(true, false)) {
            clearTimeout();
            callback.onSuccess(response);
        }
    }

    public void onFail(Throwable ex) {
        if (isValid.compareAndSet(true, false)) {
            clearTimeout();
            callback.onFail(request, ex);
        }
    }

    private void clearTimeout() {
        if (null != timeoutTask && !(timeoutTask.isCancelled() || timeoutTask.isDone())) {
            timeoutTask.cancel(false);
        }
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            // send fail, cancel timeoutTask and call onFail
            onFail(channelFuture.cause());
        }
    }
}

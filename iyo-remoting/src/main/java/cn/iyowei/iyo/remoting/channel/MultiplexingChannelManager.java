package cn.iyowei.iyo.remoting.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * channel复用
 * Created by vick on 2016/8/8.
 */
public class MultiplexingChannelManager implements ChannelManager {

    //  如果不支持多路复用，则需要用channelPool的方式实现
    private ConcurrentHashMap<String, ChannelHolder> holders = new ConcurrentHashMap<String, ChannelHolder>();

    private Bootstrap bootstrap;

    public MultiplexingChannelManager(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public Channel getChannel(String ip, int port) {
        InetSocketAddress isa = new InetSocketAddress(ip, port);
        return getChannel(isa);
    }


    public Channel getChannel(InetSocketAddress isa) {
        String ip = isa.getHostName();
        int port = isa.getPort();
        String key = ip + ":" + port;
        ChannelHolder holder = holders.get(key);
        if (null != holder) {
            return holder.channel;
        }
        holder = createChannel(isa);
        ChannelHolder old = holders.putIfAbsent(key, holder);
        if (old != null) {  // another thread had created one
            destroyChannel(holder);
            holder = old;
        }
        return holder.channel;
    }


    private ChannelHolder createChannel(InetSocketAddress isa) {
        long CONNECTION_TIMEOUT = 1000;
        ChannelFuture future = bootstrap.connect(isa);
        try {
            future.await(CONNECTION_TIMEOUT);
        } catch (InterruptedException e) {
            throw new RuntimeException("Channel connect fail.");
        }
        if (future.isSuccess()) {
            return new ChannelHolder(future.channel(), isa.getHostName(), isa.getPort());
        } else {
            throw new RuntimeException("Channel connect fail.");
        }
    }


    private void destroyChannel(ChannelHolder holder) {
        holder.channel.close();
    }

    private static class ChannelHolder {
        String ip;
        int port;
        Channel channel;

        ChannelHolder(Channel channel, String ip, int port) {
            this.channel = channel;
            this.ip = ip;
            this.port = port;
        }
    }

}

package cn.iyowei.iyo.remoting.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * 池化Channel，每个ip，port对应多个channel，channel在回包前不可复用
 * Created by vick on 2016/8/16.
 */
public class PoolingChannelManager implements ChannelManager {


    private Bootstrap bootstrap;

    public PoolingChannelManager(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public Channel getChannel(String ip, int port) {
        return null;
    }

    @Override
    public Channel getChannel(InetSocketAddress isa) {
        return null;
    }
}

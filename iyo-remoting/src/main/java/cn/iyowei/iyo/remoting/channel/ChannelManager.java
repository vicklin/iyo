package cn.iyowei.iyo.remoting.channel;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created by vick on 2016/8/15.
 * @see MultiplexingChannelManager
 */
public interface ChannelManager {


    Channel getChannel(String ip, int port);


    Channel getChannel(InetSocketAddress isa);

}

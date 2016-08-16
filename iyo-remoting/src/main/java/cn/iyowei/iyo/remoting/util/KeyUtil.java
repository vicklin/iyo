package cn.iyowei.iyo.remoting.util;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created by liuguanglin on 16/8/17.
 */
public class KeyUtil {

    public static String key(Channel channel, Object seq) {
        InetSocketAddress isa = ((InetSocketAddress) channel.remoteAddress());
        return isa.getHostName() + ":" + isa.getPort() + "#" + seq;
    }


}

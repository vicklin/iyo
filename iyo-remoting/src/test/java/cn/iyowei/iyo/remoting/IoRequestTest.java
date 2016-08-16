package cn.iyowei.iyo.remoting;

import java.net.InetSocketAddress;

/**
 * Created by liuguanglin on 16/8/17.
 */
public class IoRequestTest implements IoRequest {
    @Override
    public Object getIoSeq() {
        return 1;
    }

    @Override
    public Object getIoCmd() {
        return 1;
    }

    @Override
    public long getCreateTime() {
        return System.currentTimeMillis();
    }

    @Override
    public IoPacket newResponsePacket(IoPacket reqPacket, int ec, String message, Object body) {
        return null;
    }

    @Override
    public Object getRouterId() {
        return null;
    }

    @Override
    public InetSocketAddress getRouterAddr() {
        int port = 8080;
        return new InetSocketAddress("127.0.0.1", port);
    }

    @Override
    public void setRouterAddr(InetSocketAddress addr) {

    }

    @Override
    public int getEstimateSize() {
        return 0;
    }
}

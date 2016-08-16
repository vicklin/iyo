package cn.iyowei.iyo.remoting;

import cn.iyowei.iyo.remoting.channel.MultiplexingChannelManager;
import cn.iyowei.iyo.remoting.codec.IoFrameDecoder;
import cn.iyowei.iyo.remoting.codec.IoFrameEncoder;
import cn.iyowei.iyo.remoting.session.SessionManager;
import cn.iyowei.iyo.remoting.timeout.DefaultTimeoutManager;

/**
 * Created by liuguanglin on 16/8/16.
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        SimpleIoService.Builder builder = SimpleIoService.newBuilder();
        IoService service = builder
                .setChannelManager(MultiplexingChannelManager.class)
                .setDecoder(new IoFrameDecoder())
                .setEncoder(new IoFrameEncoder())
                .setSessionManager(new SessionManager())
                .setTimeoutManager(new DefaultTimeoutManager())
                .build();

        IoRequest req = new IoRequestTest();
        IoCallback callback = new IoCallbackTest();
        long timeoutMills = 1000;
        service.send(req, callback, timeoutMills);
    }
}

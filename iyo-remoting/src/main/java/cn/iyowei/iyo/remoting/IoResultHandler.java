package cn.iyowei.iyo.remoting;

import cn.iyowei.iyo.remoting.session.IoSession;
import cn.iyowei.iyo.remoting.session.SessionManager;
import cn.iyowei.iyo.remoting.util.KeyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by liuguanglin on 16/8/17.
 */
public class IoResultHandler extends SimpleChannelInboundHandler<IoResponse> {

    private SessionManager sessionManager;

    public IoResultHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IoResponse req) throws Exception {
        String key = KeyUtil.key(ctx.channel(), req.getIoSeq());
        IoSession session = sessionManager.remove(key);
        if (null != session) {
            session.onSuccess(req);
        }
    }

}

package cn.iyowei.iyo.remoting;

import java.util.concurrent.Future;

/**
 * Created by vick on 2016/8/8.
 */
public interface IoService {

    IoResponse sendBlocking(IoRequest req, long timeoutMills) throws Exception;

    Future send(IoRequest req, IoCallback callback, long timeoutMills) throws Exception;

}
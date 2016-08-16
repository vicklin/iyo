package cn.iyowei.iyo.remoting;

/**
 * Created by vick on 2016/8/8.
 */
public interface IoCallback {

    void onSuccess(IoResponse response);

    void onFail(IoRequest request, Throwable ex);

}

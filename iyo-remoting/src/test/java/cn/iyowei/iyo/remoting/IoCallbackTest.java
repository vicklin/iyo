package cn.iyowei.iyo.remoting;

/**
 * Created by liuguanglin on 16/8/17.
 */
public class IoCallbackTest implements IoCallback {

    @Override
    public void onSuccess(IoResponse response) {
        System.out.println("Receive success, response:" + response);
    }

    @Override
    public void onFail(IoRequest request, Throwable ex) {
        System.err.println("Fail.....");
        ex.printStackTrace();
    }
}

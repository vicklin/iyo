package cn.iyowei.iyo.remoting;

/**
 * Created by vick on 2016/8/16.
 */
public abstract class IoRequest implements IoPacket {

    private int seq;

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public Object getIoSeq() {
        return this.seq;
    }
}

package com.xeehoo.rpc.async;

/**
 * Created by wangzunhui on 2017/2/15.
 */
public class Message {
    private long seq;
    private long time;
    private int type;
    private Object data;

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

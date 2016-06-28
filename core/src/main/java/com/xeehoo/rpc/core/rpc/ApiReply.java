package com.xeehoo.rpc.core.rpc;

/**
 * Created by wangzunhui on 2016/6/28.
 */
public class ApiReply {
    private long lsn;
    private int type;
    private Object obj;

    public long getLsn() {
        return lsn;
    }

    public void setLsn(long lsn) {
        this.lsn = lsn;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

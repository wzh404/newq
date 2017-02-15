package com.xeehoo.rpc.async;

/**
 * Created by wangzunhui on 2017/2/15.
 */
public abstract class Event {
    public final static int REQUEST_TYPE = 1;
    public final static int RESPONSE_TYPE = 2;

    private Message message;

    public Message get(){
        return message;
    }
    public void set(Message message){
        this.message = message;
    }

    public boolean isRequest(){
        if (get() == null) return false;

        return get().getType() == REQUEST_TYPE;
    }

    public boolean isResponse(){
        if (get() == null) return false;

        return get().getType() == RESPONSE_TYPE;
    }

    public abstract void call(Event e);
}

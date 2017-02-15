package com.xeehoo.rpc.async;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wangzunhui on 2017/2/15.
 */
public class EventLoop implements Runnable{
    private final Logger logger =  LogManager.getLogger();

    private static LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue(100);
    private Map<Long,Event> eventMap = new HashMap<Long, Event>();
    private volatile boolean running = true;

    public void submit(Event event) {
        Message message = event.get();
        if (message == null){
            return;
        }
        logger.info("------------loop-submit------------" + event.get().getSeq());
        if (event.get().getSeq() == 0L) {
            long seq = System.currentTimeMillis();
            message.setSeq(seq);
        }
        message.setTime(System.currentTimeMillis());

        try {
            queue.put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(running){
            Event event = null;
            try{
                if (queue.isEmpty()){
                    Thread.sleep(100);
                }

                event = queue.take();
                if (event == null){
                    continue;
                }
            } catch(InterruptedException e){
                continue;
            }

            if (event.isRequest()){
                eventMap.put(event.get().getSeq(), event);

                logger.info("------------loop-request------------");
                /* auto response */
                Event e = new Event(){
                    @Override
                    public void call(Event e) {
                        logger.info("-----Auto------RESPONSE--------" + e.get().getSeq());
                    }
                };
                Message m = new Message();
                m.setSeq(event.get().getSeq());
                m.setType(Event.RESPONSE_TYPE);
                e.set(m);
                submit(e);
            } else if (event.isResponse()){
                logger.info("------------loop-response------------");
                Event e = eventMap.get(event.get().getSeq());
                if (e != null){
                    e.call(event);
                    event.call(e);
                }
            }
        }
    }

    public void start(){
        new Thread(this).start();
    }

    public void close(){
        running = false;
    }
}

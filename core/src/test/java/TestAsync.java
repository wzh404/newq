import com.xeehoo.rpc.async.Event;
import com.xeehoo.rpc.async.EventLoop;
import com.xeehoo.rpc.async.Message;
import org.junit.Test;

/**
 * Created by wangzunhui on 2017/2/15.
 */
public class TestAsync {
    @Test
    public void test(){
        System.out.println("----------Start test----------");
        EventLoop loop = new EventLoop();
        loop.start();

        Event event = new Event(){
            @Override
            public void call(Event e) {
                System.out.println("-----Test------RESPONSE--------" + e.get().getSeq());
            }
        };
        Message m = new Message();
        m.setType(Event.REQUEST_TYPE);
        event.set(m);
        loop.submit(event);

        try {
            Thread.sleep(1 * 60 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

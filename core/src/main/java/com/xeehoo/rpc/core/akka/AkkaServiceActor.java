package com.xeehoo.rpc.core.akka;

import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import com.xeehoo.rpc.core.rpc.ApiReply;
import com.xeehoo.rpc.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;
import scala.concurrent.duration.Duration;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.function.Function;

/**
 * Created by wangzunhui on 2016/6/28.
 */
public class AkkaServiceActor<T> extends UntypedActor {
    private final Logger logger =  LogManager.getLogger(AkkaServiceActor.class.getName());
    private final DeferredResult<ModelAndView> _deferred;
    private final Function<T, ModelAndView> _fn;

    public AkkaServiceActor(DeferredResult<ModelAndView> deferred, Function<T, ModelAndView> fn){
        this._deferred = deferred;
        this._fn = fn;
        getContext().setReceiveTimeout(Duration.create("5 seconds"));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ReceiveTimeout){
            getContext().stop(getSelf());
            _deferred.setResult(new ModelAndView("message", "message", "TimeOut"));
        }
        else if (message instanceof ApiReply){
            getContext().stop(getSelf());
            _deferred.setResult(_fn.apply((T) ((ApiReply) message).getObj()));
        }
        else {
            unhandled(message);
        }
    }
}

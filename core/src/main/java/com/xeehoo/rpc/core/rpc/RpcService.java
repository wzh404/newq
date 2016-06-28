package com.xeehoo.rpc.core.rpc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.xeehoo.rpc.core.akka.AkkaServiceActor;
import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RpcService<U, T> {
    private final Logger logger =  LogManager.getLogger();

	private Class<U> _api;
//    private CompletableFuture<T> _future;
//    private static Map<Class<?>, HessianRpcProxy> cache = new ConcurrentHashMap<Class<?>, HessianRpcProxy>();
//	private static ThreadLocal<CompletableFuture> futureThreadLocal = new ThreadLocal<CompletableFuture>();

    public RpcService(Class<U> api){
        this._api = api;
	}

    public CompletableFuture<ModelAndView> applyAsync(Servicer<U> servicer, Function<T, ModelAndView> fn){
        long stime = System.currentTimeMillis();

//        futureThreadLocal.remove();
//        CompletableFuture<T> future = new CompletableFuture<T>();
//        futureThreadLocal.set(future);

//        if (cache.get(api) == null) {
//            cache.put(api, new HessianRpcProxy(api, "future"));
//        }
//        HessianRpcProxy handler = cache.get(api);
        CompletableFuture<T> future = new CompletableFuture<T>();;
        HessianRpcProxy handler = new HessianRpcProxy(_api, future);
        U u = (U)create(handler);
        logger.info("ref is ____________" + (System.currentTimeMillis() - stime) + "ms");

        servicer.apply(u);

        return future.thenApply(fn);
    }
	
//	public CompletableFuture<ModelAndView> thenApply(Function<T, ModelAndView> fn){
//        logger.info(Thread.currentThread().getName() + "___________thenApply");
////        CompletableFuture<T> future = futureThreadLocal.get();
//		return _future.thenApply(fn);
//	}

//    public static CompletableFuture getThreadLocalFuture(){
//        return futureThreadLocal.get();
//    }

    public DeferredResult<ModelAndView> applyAsync(ActorSystem system, Servicer<U> servicer, Function<T, ModelAndView> fn){
        long lsn = HessianCoder.lsn();
        long startTime = System.nanoTime();

        DeferredResult<ModelAndView> deferredResult = new DeferredResult<ModelAndView>();
        system.actorOf(Props.create(AkkaServiceActor.class, deferredResult, fn), "serviceActor$" + lsn);
        logger.info("actorRef_________" + (System.nanoTime() - startTime) * 0.000001);

        HessianAkkaProxy handler = new HessianAkkaProxy(_api, lsn);
        logger.info("handler________" + (System.nanoTime() - startTime) * 0.000001);

        U proxy = (U)Proxy.newProxyInstance(_api.getClassLoader(), new Class[]{_api}, handler);
        logger.info("proxy________" + (System.nanoTime() - startTime) * 0.000001);

        servicer.apply(proxy);
        return deferredResult;
    }
	
	public Object create(HessianRpcProxy handler){
	    if (_api == null){
	    	throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
	    }

	    return Proxy.newProxyInstance(_api.getClassLoader(), new Class[] { _api }, handler);
	}
}

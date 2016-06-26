package com.xeehoo.rpc.core.rpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class RpcService<U, T> {
    private final Logger logger =  LogManager.getLogger();

	private Class<U> api;
    private static Map<Class<?>, HessianRpcProxy> cache = new ConcurrentHashMap<Class<?>, HessianRpcProxy>();
	private static ThreadLocal<CompletableFuture> futureThreadLocal = new ThreadLocal<CompletableFuture>();

    public RpcService(Class<U> api){
        this.api = api;
	}

    public RpcService<U, T> applyAsync(Servicer<U> servicer){
        long stime = System.currentTimeMillis();

        futureThreadLocal.remove();
        CompletableFuture<T> future = new CompletableFuture<T>();
        futureThreadLocal.set(future);

        if (cache.get(api) == null) {
            cache.put(api, new HessianRpcProxy(api));
        }
        HessianRpcProxy handler = cache.get(api);

        U u = (U)create(handler);
        logger.info("ref is ____________" + (System.currentTimeMillis() - stime) + "ms");

        servicer.apply(u);

        return this;
    }
	
	public CompletableFuture<ModelAndView> thenApply(Function<T, ModelAndView> fn){
        logger.info(Thread.currentThread().getName() + "___________thenApply");
        CompletableFuture<T> future = futureThreadLocal.get();
		return future.thenApply(fn);
	}

    public static CompletableFuture getThreadLocalFuture(){
        return futureThreadLocal.get();
    }
	
	public Object create(HessianRpcProxy handler){
	    if (api == null){
	    	throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
	    }

	    return Proxy.newProxyInstance(api.getClassLoader(), new Class[] { api }, handler);
	}
}

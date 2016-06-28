package com.xeehoo.rpc.core.rpc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.xeehoo.rpc.core.akka.AkkaServiceActor;
import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.core.netty.client.NettyClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;


public class HessianAkkaProxy<T> implements InvocationHandler, Serializable{
	private final Logger logger =  LogManager.getLogger();
	private static final long serialVersionUID = -5392660471766899934L;

	private Class<?> _type;
//	private DeferredResult<String> _deferredResult;
	private long _lsn;

	public HessianAkkaProxy(Class<?> type, long lsn){
		this._type = type;
//		this._deferredResult = deferredResult;
		this._lsn = lsn;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if ("toString".equals(methodName)){
			return "Proxy [" + this + "]";
		}

		byte[] data = HessianCoder.serialization(this._type, method.getName(), args, _lsn, 2);
		ByteBuf buf = Unpooled.wrappedBuffer(data);
		NettyClient.getInstance().getChannel().writeAndFlush(buf).sync();
    	
    	return null;
	}
}

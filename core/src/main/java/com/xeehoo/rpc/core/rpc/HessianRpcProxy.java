package com.xeehoo.rpc.core.rpc;

import com.xeehoo.rpc.core.hessian.HessianCoder;
import com.xeehoo.rpc.core.netty.client.NettyClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;


public class HessianRpcProxy<T> implements InvocationHandler, Serializable{
	private final Logger logger =  LogManager.getLogger();

	private static final long serialVersionUID = -5392660471766899934L;
	private static AtomicLong atmoicLong = new AtomicLong();
	
	private Class<?> _type;
	private String _rpcType;

	public HessianRpcProxy(Class<?> type, String rpcType){
		this._type = type;
		if (rpcType == null){
			this._rpcType = "future";
		}
		else{
			this._rpcType = rpcType;
		}
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if ("toString".equals(methodName)){
			return "Proxy [" + this + "]";
		}
		long lsn = HessianRpcProxy.atmoicLong.incrementAndGet();

		if ("future".equalsIgnoreCase(_rpcType)) {
			logger.info(Thread.currentThread().getName() + " invoke - put lsn is " + lsn);
			HessianCoder.put(lsn, RpcService.getThreadLocalFuture());
		}

		byte[] data = HessianCoder.serialization(this._type, method.getName(), args, lsn);
		ByteBuf buf = Unpooled.wrappedBuffer(data);
		NettyClient.getInstance().getChannel().writeAndFlush(buf).sync();
    	
    	return null;
	}
}

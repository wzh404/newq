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
import java.util.concurrent.CompletableFuture;


public class HessianRpcProxy<T> implements InvocationHandler, Serializable {
    private final Logger logger = LogManager.getLogger();
    private static final long serialVersionUID = -5392660471766899934L;

    private Class<?> _type;
    private CompletableFuture<T> _future;

    public HessianRpcProxy(Class<?> type, CompletableFuture<T> future) {
        this._type = type;
        this._future = future;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        String methodName = method.getName();
        if ("toString".equals(methodName)) {
            return "Proxy [" + this + "]";
        }
        long lsn = HessianCoder.lsn();

        logger.info(Thread.currentThread().getName() + " invoke - put lsn is " + lsn);
        HessianCoder.put(lsn, (CompletableFuture) _future);

        byte[] data = HessianCoder.serialization(this._type, method.getName(), args, lsn, 1);
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        NettyClient.getInstance().getChannel().writeAndFlush(buf).sync();

        return null;
    }
}

package com.xeehoo.rpc.core.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.xeehoo.rpc.core.rpc.ApiBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HessianCoder {
	private static final Logger logger =  LogManager.getLogger();
	private static Map<Long, CompletableFuture<Object>> map = new ConcurrentHashMap<Long, CompletableFuture<Object>>();
	
	public static byte[] serialization(Class<?> cls, String methodName, Object[] paras, long lsn){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Hessian2Output out = new Hessian2Output(bos);	

		try {
			out.startMessage();
			out.writeLong(lsn);
			out.writeString(cls.getName());
			out.writeString(methodName);
			out.writeInt(paras.length);
			for (Object para : paras){
				out.writeObject(para);
			}
			out.completeMessage();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		byte[] data = bos.toByteArray();		
		return data;
	}
	
	public static ApiBean deserialization(byte[] data){
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		Hessian2Input in = new Hessian2Input(bin);
		
		ApiBean api = new ApiBean();
		try {
			in.startMessage();
			long lsn = in.readLong();
			
			String className = in.readString();
			String methodName = in.readString();
			int len = in.readInt();
			Object[] paras = new Object[len];
			Class<?>[] types = new Class<?>[len];
			for (int i = 0; i < len; i++){
				paras[i] = in.readObject();
				types[i] = paras[i].getClass();
			}
			in.completeMessage();
			Method method = Class.forName(className).getMethod(methodName, types);
			
			api.setApi(className);
			api.setMethod(method);
			api.setParas(paras);
			api.setLsn(lsn);
			
			return api;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return null;
	}
	
	public static byte[] reply(Object object, long lsn){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Hessian2Output out = new Hessian2Output(bos);	

		try {
			out.startReply();
			out.writeObject(object);
			out.writeLong(lsn);
			out.completeReply();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		byte[] data = bos.toByteArray();		
		return data;
	}
	
	public static Object getReply(byte[] data, Class<?> returnType){
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		Hessian2Input in = new Hessian2Input(bin);
		
		Object obj = null;
		try {
			int code = in.read();
			int major = in.read();
			int minor = in.read();
		
			obj = in.readReply(returnType);
			long lsn = in.readLong();
			in.completeReply();
			
			logger.info(Thread.currentThread().getName() + " get lsn - " + lsn);
			emit(lsn, obj);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return obj;
	}
	
	public static void emit(long lsn, Object obj){
		CompletableFuture<Object> f = HessianCoder.map.get(lsn);
		if (f != null) {
			f.complete(obj);
			HessianCoder.map.remove(lsn);
		}
	}
	
	public static void put(long lsn, CompletableFuture<Object> f){
		HessianCoder.map.put(lsn, f);
	}
}
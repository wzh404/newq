package com.xeehoo.rpc.core.rpc;

import java.lang.reflect.Method;

public class ApiBean {
	private long lsn;
	private String api;
	private Method method;
	private Object[] paras;
	private Class<?>[] types;
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object[] getParas() {
		return paras;
	}
	public void setParas(Object[] paras) {
		this.paras = paras;
	}
	public Class<?>[] getTypes() {
		return types;
	}
	public void setTypes(Class<?>[] types) {
		this.types = types;
	}
	public long getLsn() {
		return lsn;
	}
	public void setLsn(long lsn) {
		this.lsn = lsn;
	}
	
	
}

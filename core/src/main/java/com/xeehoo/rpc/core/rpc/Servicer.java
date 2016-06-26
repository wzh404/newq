package com.xeehoo.rpc.core.rpc;

/**
 * Created by WIN10 on 2016/6/25.
 */
@FunctionalInterface
public interface Servicer<T> {
    void apply(T var0);
}

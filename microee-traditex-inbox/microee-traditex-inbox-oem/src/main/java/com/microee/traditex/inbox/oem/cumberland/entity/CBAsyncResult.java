package com.microee.traditex.inbox.oem.cumberland.entity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CBAsyncResult<T> {

    private CountDownLatch latch = new CountDownLatch(1);
    private T result;

    public CBAsyncResult() {

    }

    public CBAsyncResult(T t) {
        this.result = t;
    }

    public T getResult() {
        return result;
    }

    @SuppressWarnings("unchecked")
    public CBAsyncResult<?> setResult(Object result) {
        this.result = (T)result;
        return this;
    }
    
    public CBAsyncResult<?> success() {
        this.latch.countDown();
        return this;
    }
    
    public boolean await(long l) throws InterruptedException {
        return this.latch.await(l, TimeUnit.SECONDS);
    }

}

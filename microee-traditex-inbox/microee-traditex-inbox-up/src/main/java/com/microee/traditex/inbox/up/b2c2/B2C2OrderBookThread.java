package com.microee.traditex.inbox.up.b2c2;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class B2C2OrderBookThread implements Runnable, UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(B2C2OrderBookThread.class);
    
    private static final String THREAD_NAME = "b2c2-orderbook-thread-";
    private final B2C2Factory factory;
    private AtomicInteger threadId = new AtomicInteger(0);
    private Thread currentThread;
    
    public static B2C2OrderBookThread create(B2C2Factory factory) {
        return new B2C2OrderBookThread(factory);
    }
    
    public B2C2OrderBookThread(B2C2Factory factory) {
        this.factory = factory;
    }
    
    @Override
    public void run() {
        this.factory.createOrderBookStream();
    }
    
    public Thread thread() {
        this.currentThread = new Thread(this, THREAD_NAME + this.threadId.incrementAndGet());
        this.currentThread.setUncaughtExceptionHandler(this);
        return this.currentThread;
    }
    
    public void start() {
        this.thread().start();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.info("异步线程异常: factory={}, threadId={}, threadName={}, errorMessage={}", 
                this.factory.config(), t.getId(), t.getName(), e.getMessage(), e);
        this.start();
    }

    public void shutdown() {
        if (this.currentThread != null) {
            this.currentThread.interrupt();
        }
    }
    
}

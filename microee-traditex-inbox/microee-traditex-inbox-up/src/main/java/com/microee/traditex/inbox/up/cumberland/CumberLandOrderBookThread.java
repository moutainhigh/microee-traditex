package com.microee.traditex.inbox.up.cumberland;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.traditex.inbox.up.hbitex.HBiTexOrderBookThread;

public class CumberLandOrderBookThread implements Runnable, UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HBiTexOrderBookThread.class);
    
    private static final String THREAD_NAME = "cumberland-orderbook-thread-";
    private final CumberLandFactory factory;
    private AtomicInteger threadId = new AtomicInteger(0);
    private Thread currentThread;
    
    public static CumberLandOrderBookThread create(CumberLandFactory factory) {
        return new CumberLandOrderBookThread(factory);
    }
    
    public CumberLandOrderBookThread(CumberLandFactory factory) {
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

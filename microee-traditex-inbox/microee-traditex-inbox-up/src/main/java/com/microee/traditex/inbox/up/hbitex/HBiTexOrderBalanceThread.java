package com.microee.traditex.inbox.up.hbitex;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBiTexOrderBalanceThread implements Runnable, UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(HBiTexOrderBalanceThread.class);
    
    private static final String THREAD_NAME = "hbitex-orderbalance-thread-";
    private final HBiTexTradFactory factory;
    private AtomicInteger threadId = new AtomicInteger(0);
    private Thread currentThread;
    
    public static HBiTexOrderBookThread create(HBiTexTradFactory factory) {
        return new HBiTexOrderBookThread(factory);
    }
    
    public HBiTexOrderBalanceThread(HBiTexTradFactory factory) {
        this.factory = factory;
        this.currentThread = new Thread(this, THREAD_NAME + this.threadId.incrementAndGet());
        this.currentThread.setUncaughtExceptionHandler(this);
    }
    
    @Override
    public void run() {
        this.factory.createWebSocketForOrderBalance();
    }
    
    public void start() {
        this.currentThread.start();
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

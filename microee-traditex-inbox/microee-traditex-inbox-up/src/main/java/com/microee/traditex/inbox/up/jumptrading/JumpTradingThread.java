package com.microee.traditex.inbox.up.jumptrading;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.plugin.thread.ThreadSupport;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;

public class JumpTradingThread implements Runnable, UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(JumpTradingThread.class);
    
    private static final String THREAD_NAME = "jumptrading-stream-thread-";
    private final JumpTradingFactory factory;
    private AtomicInteger threadId = new AtomicInteger(0);
    private Thread currentThread;
    
    public static JumpTradingThread create(JumpTradingFactory factory) {
        return new JumpTradingThread(factory);
    }
    
    public JumpTradingThread(JumpTradingFactory factory) {
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
        logger.info("异步线程异常1: factory={}, threadId={}, threadName={}, connStatus={}, errorMessage={}", 
                this.factory.config(), t.getId(), t.getName(), factory.orderBookStreamHandler().getConnectStatus(), e.getMessage(), e);
        if (!factory.orderBookStreamHandler().getConnectStatus().equals(ConnectStatus.DESTROY)) {
            this.start();
        }
    }

    public void shutdown() {
        if (this.currentThread != null) {
            ThreadSupport.interrupt(this.currentThread);
        }
    }
    
}
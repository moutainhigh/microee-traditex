package com.microee.traditex.inbox.up.oanda;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.microee.plugin.thread.ThreadSupport;
import com.microee.traditex.inbox.oem.constrants.ConnectStatus;

public class OandaStreamThread implements Runnable, UncaughtExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(OandaStreamThread.class);
    
    private static final String THREAD_NAME = "oanda-stream-thread-";
    private final OandaTradFactory factory;
    private AtomicInteger threadId = new AtomicInteger(0);
    private Thread currentThread;
    
    public static OandaStreamThread create(OandaTradFactory factory) {
        return new OandaStreamThread(factory);
    }
    
    public OandaStreamThread(OandaTradFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public void run() {
        this.factory.createPricingStream();
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
        logger.info("异步线程异常: factory={}, threadId={}, threadName={}, connStatus={}, errorMessage={}", 
                this.factory.config(), t.getId(), t.getName(), factory.getOandaStreamHandler().getConnectStatus(), e.getMessage(), e);
        if (!factory.getOandaStreamHandler().getConnectStatus().equals(ConnectStatus.DESTROY)) {
            this.start();
        }
    }

    public void shutdown() {
        if (this.currentThread != null) {
            ThreadSupport.interrupt(this.currentThread);
        }
    }
    
}

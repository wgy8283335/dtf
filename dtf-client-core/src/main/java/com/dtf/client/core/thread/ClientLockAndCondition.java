package com.dtf.client.core.thread;

import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.dtf.client.core.dbconnection.OperationType;
import com.dtf.client.core.nettyclient.protobufclient.NettyService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * The implementation of client lock and condition interface.
 * Wait and send signal between data source and netty client.
 * 
 * @Author: wangguangyuan
 */
public final class ClientLockAndCondition implements ClientLockAndConditionInterface {
    
    /**
     * Lock used in signal and wait.
     */
    private Lock lock;
    
    /**
     * Condition used in signal and wait.
     */
    private Condition condition;
    
    /**
     * Operation type.
     */
    private volatile OperationType state = OperationType.DEFAULT;
    
    public ClientLockAndCondition(final Lock lock, final OperationType state) {
        this.state = state;
        this.lock = lock;
        this.condition = lock.newCondition();
    }
    
    /**
     * Get state.
     * @return operation type.
     */
    @Override
    public OperationType getState() {
        return state;
    }
    
    /**
     * Set state.
     * 
     * @param state operation type
     */
    @Override
    public void setState(final OperationType state) {
        this.state = state;
    }
    
    /**
     * Condition signal.
     */
    @Override
    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Wait until up in time or receive signal.
     * 
     * @param milliseconds milliseconds
     * @param timeUnit time unit
     * @return whether time out
     */
    @Override
    public boolean await(final long milliseconds, final TimeUnit timeUnit) {
        boolean result = wait(milliseconds, timeUnit);
        return result;
    }
    
    /**
     * Wait until receive signal. If haven't receive signal until up in time, will throw exception.
     * 
     * @param nettyService netty service
     * @param serviceInfo base trasaction service information
     * @param msg message
     * @param milliseconds milliseconds
     * @param timeUnit time unit
     */
    @Override
    public void awaitLimitedTime(final NettyService nettyService, final BaseTransactionServiceInfo serviceInfo,
                                 final String msg, final long milliseconds, final TimeUnit timeUnit) throws Exception {
        nettyService.sendMsg(serviceInfo);
        boolean receivedSignal = wait(milliseconds, timeUnit);
        if (!receivedSignal) {
            throw new InterruptedException(msg);
        }
    }
    
    private boolean wait(final long milliseconds, final TimeUnit timeUnit) {
        boolean result = false;
        try {
            lock.lock();
            result = condition.await(milliseconds, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return result;
    }
    
}

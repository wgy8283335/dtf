package com.coconason.dtf.client.core.thread;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:44
 */
public final class ClientLockAndCondition implements ClientLockAndConditionInterface {
    private Lock lock;
    private Condition condition;
    private volatile OperationType state = OperationType.DEFAULT;

    public ClientLockAndCondition(Lock lock, OperationType state) {
        this.state = state;
        this.lock = lock;
        this.condition = lock.newCondition();
    }
    @Override
    public OperationType getState() {
        return state;
    }
    @Override
    public void setState(OperationType state) {
        this.state = state;
    }
    @Override
    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    @Override
    public boolean await(long milliseconds, TimeUnit timeUnit) {
        boolean result = wait(milliseconds, timeUnit);
        return result;
    }
    @Override
    public void awaitLimitedTime(NettyService nettyService, BaseTransactionServiceInfo serviceInfo, String msg, long milliseconds, TimeUnit timeUnit) throws Exception{
        nettyService.sendMsg(serviceInfo);
        boolean receivedSignal = wait(milliseconds, timeUnit);
        if(receivedSignal == false){
            throw new Exception(msg);
        }
    }

    private boolean wait(long milliseconds, TimeUnit timeUnit){
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

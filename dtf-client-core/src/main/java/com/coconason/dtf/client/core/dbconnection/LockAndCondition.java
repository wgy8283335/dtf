package com.coconason.dtf.client.core.dbconnection;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:44
 */
public class LockAndCondition {
    private Lock lock;
    private Condition condition;
    private volatile DbOperationType state = DbOperationType.DEFAULT;

    public LockAndCondition(Lock lock,DbOperationType state) {
        this.state = state;
        this.lock = lock;
        this.condition = lock.newCondition();
    }

    public DbOperationType getState() {
        return state;
    }

    public void setState(DbOperationType state) {
        this.state = state;
    }

    public void await() {
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean await(long milliseconds, TimeUnit timeUnit) {
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

    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}

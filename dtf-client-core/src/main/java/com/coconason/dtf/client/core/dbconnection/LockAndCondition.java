package com.coconason.dtf.client.core.dbconnection;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:44
 */
public class LockAndCondition {
    private Lock lock;
    private Condition condition;
    private volatile ActionType state = ActionType.APPLYFORSUBMIT;

    public LockAndCondition(Lock lock,ActionType state) {
        this.state = state;
        this.lock = lock;
        this.condition = lock.newCondition();
    }

    public ActionType getState() {
        return state;
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

    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}

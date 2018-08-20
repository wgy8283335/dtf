package com.coconason.dtf.client.core.beans;

/**
 * @Author: Jason
 * @date: 2018/8/20-8:41
 */
public class TransactionRollback {
    private final static ThreadLocal<TransactionRollback> currentThreadLocal = new ThreadLocal<TransactionRollback>();

    public TransactionRollback() {
    }

    public static TransactionRollback getCurrentThreadLocal() {
        return currentThreadLocal.get();
    }

    public static void setCurrentThreadLocal(TransactionRollback transactionRollback){
        currentThreadLocal.set(transactionRollback);
    }
}

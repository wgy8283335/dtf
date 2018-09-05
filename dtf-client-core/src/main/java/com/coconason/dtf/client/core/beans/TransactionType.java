package com.coconason.dtf.client.core.beans;

/**
 * @Author: Jason
 * @date: 2018/9/5-15:15
 */
public class TransactionType {
    private final static ThreadLocal<TransactionType> current = new ThreadLocal<>();
    //SYNC_FINAL、SYNC_STRONG、ASYNC_FINAL
    private String transactionType;

    public TransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public static TransactionType getCurrent() {
        return current.get();
    }

    public static void setCurrent(TransactionType transactionType){
        current.set(transactionType);
    }
}

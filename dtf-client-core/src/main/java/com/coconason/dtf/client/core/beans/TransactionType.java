package com.coconason.dtf.client.core.beans;

/**
 * @Author: Jason
 * @date: 2018/9/5-15:15
 */
public enum TransactionType {
    //SYNC_FINAL,SYNC_STRONG,SYNC_STRONG
    SYNC_FINAL("SYNC_FINAL"),SYNC_STRONG("SYNC_STRONG"),ASYNC_FINAL("ASYNC_FINAL");

    private final static ThreadLocal<TransactionType> current = new ThreadLocal<>();

    private String transactionType;

    TransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public static TransactionType newInstance(String type){
        switch (type){
            case "SYNC_FINAL":
                return SYNC_FINAL;
            case "SYNC_STRONG":
                return SYNC_STRONG;
            case "ASYNC_FINAL":
                return ASYNC_FINAL;
            default:
                return null;
        }
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

package com.dtf.client.core.beans.type;

/**
 * Transaction type.
 * 
 * @author wangguangyuan
 */
public enum TransactionType {
    
    /**
     * There is three kinds of transaction type:
     * synchronous final consistency; synchronous strong consistency; asynchronous final consistency.
     */
    SYNC_FINAL("SYNC_FINAL"), SYNC_STRONG("SYNC_STRONG"), ASYNC_FINAL("ASYNC_FINAL");
    
    /**
     * Current is a ThreadLocal variable,each thread has its on TransactionType variable.
     */
    private static final ThreadLocal<TransactionType> CURRENT = new ThreadLocal<>();
    
    /**
     * Transaction type, store type in string.
     */
    private String transactionType;
    
    TransactionType(final String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Create instance by type.
     * 
     * @param type type in string
     * @return transaction type
     */
    public static TransactionType newInstance(final String type) {
        switch (type) {
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
    
    /**
     * Get TransactionType object in current thread.
     *
     * @return base transaction group information
     */
    public static TransactionType getCurrent() {
        return CURRENT.get();
    }
    
    /**
     * Set TransactionType object in current thread.
     *
     * @param transactionType transaction type
     */
    public static void setCurrent(final TransactionType transactionType) {
        CURRENT.set(transactionType);
    }
    
}

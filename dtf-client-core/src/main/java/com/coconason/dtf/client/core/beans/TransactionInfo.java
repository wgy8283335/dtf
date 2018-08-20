package com.coconason.dtf.client.core.beans;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Jason
 * @date: 2018/8/20-10:42
 */
public class TransactionInfo {
    private DtfTransaction transaction;

    private Transactional transactional;

    private TxTransactionLocal txTransactionLocal;

    private String groupId;

    private int maxTimeOut;


    private TxTransactionCompensate compensate;


    private TransactionInvocation invocation;


    public TxTransactionInfo(TxTransaction transaction,Transactional transactional, TxTransactionLocal txTransactionLocal, String txGroupId, int maxTimeOut, TxTransactionCompensate compensate, TransactionInvocation invocation) {
        this.transaction = transaction;
        this.txTransactionLocal = txTransactionLocal;
        this.txGroupId = txGroupId;
        this.maxTimeOut = maxTimeOut;
        this.compensate = compensate;
        this.invocation = invocation;
        this.transactional = transactional;
    }

    public int getMaxTimeOut() {
        return maxTimeOut;
    }



    public TxTransaction getTransaction() {
        return transaction;
    }

    public TxTransactionLocal getTxTransactionLocal() {
        return txTransactionLocal;
    }

    public String getTxGroupId() {
        return txGroupId;
    }

    public TxTransactionCompensate getCompensate() {
        return compensate;
    }

    public TransactionInvocation getInvocation() {
        return invocation;
    }

    public Transactional getTransactional() {
        return transactional;
    }
}

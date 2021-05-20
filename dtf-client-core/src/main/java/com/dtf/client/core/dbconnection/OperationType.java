package com.dtf.client.core.dbconnection;

/**
 * Transaction operation type of distributed transaction.
 * 
 * @Author: wangguangyuan
 */
public enum OperationType {
    COMMIT,
    ROLLBACK,
    DEFAULT,
    WAIT_WHOLE_RESULT,
    WHOLE_SUCCESS,
    WHOLE_FAIL,
    ASYNC_SUCCESS,
    ASYNC_FAIL,
    COMMIT_SUCCESS_ASYNC
}

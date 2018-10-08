package com.coconason.dtf.client.core.dbconnection;

/**
 * @Author: Jason
 * @date: 2018/8/24-10:38
 */
public enum OperationType {
    //
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

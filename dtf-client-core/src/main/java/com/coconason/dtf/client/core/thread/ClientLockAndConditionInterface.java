package com.coconason.dtf.client.core.thread;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Jason
 * @date: 2018/10/2-10:53
 */
public interface ClientLockAndConditionInterface {
    OperationType getState();

    void setState(OperationType state);

    void signal();

    boolean await(long milliseconds, TimeUnit timeUnit);

    void awaitLimitedTime(NettyService nettyService, BaseTransactionServiceInfo serviceInfo, String msg, long milliseconds, TimeUnit timeUnit) throws Exception;

}

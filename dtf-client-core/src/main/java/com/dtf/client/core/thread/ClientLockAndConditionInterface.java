package com.dtf.client.core.thread;

import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.dtf.client.core.dbconnection.OperationType;
import com.dtf.client.core.nettyclient.protobufclient.NettyService;

import java.util.concurrent.TimeUnit;

/**
 * The implementation of client lock and condition interface.
 * Wait and send signal between data source and netty client.
 * 
 * @author wangguangyuan
 */
public interface ClientLockAndConditionInterface {
    
    /**
     * Get state.
     * @return operation type.
     */
    OperationType getState();
    
    /**
     * Set state.
     *
     * @param state operation type
     */
    void setState(OperationType state);
    
    /**
     * Condition signal.
     */
    void signal();

    /**
     * Wait until up in time or receive signal.
     *
     * @param milliseconds milliseconds
     * @param timeUnit time unit
     * @return whether time out
     */
    boolean await(long milliseconds, TimeUnit timeUnit);

    /**
     * Wait until receive signal. If haven't receive signal until up in time, will throw exception.
     *
     * @param nettyService netty service
     * @param serviceInfo base trasaction service information
     * @param msg message
     * @param milliseconds milliseconds
     * @param timeUnit time unit
     * @exception Exception exception
     */
    void awaitLimitedTime(NettyService nettyService, BaseTransactionServiceInfo serviceInfo, String msg, long milliseconds, TimeUnit timeUnit) throws Exception;

}

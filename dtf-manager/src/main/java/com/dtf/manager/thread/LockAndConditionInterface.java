package com.dtf.manager.thread;

import com.dtf.common.protobuf.MessageProto;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.TimeUnit;

/**
 * Interface of lock and condition.
 *
 * @author wangguangyuan
 */
public interface LockAndConditionInterface {
    
    /**
     * Sait for signal.
     */
    void await();
    
    /**
     * Wait for signal or overtime.
     * @param milliseconds number
     * @param timeUnit time unit
     * @return true or false
     */
    boolean await(long milliseconds, TimeUnit timeUnit);
    
    /**
     * Send notification to unblock.
     */
    void signal();
    
    /**
     * Send message to netty channel and wait for signal.
     * If fail, keep trying util success. 
     *
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     * @param msg string
     * @throws Exception exception
     */
    void sendAndWaitForSignal(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws Exception;
    
    /**
     * Send message to netty channel and wait for signal.
     * If fail, throw exception. 
     *
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     * @param msg string
     * @throws Exception exception
     */
    void sendAndWaitForSignalOnce(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws Exception;
    
    /**
     * Send message to netty channel and wait for signal.
     * If fail, send failure message to the channel. 
     *
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     * @param msg string
     * @throws Exception exception
     */
    void sendAndWaitForSignalIfFailSendMessage(String groupId, MessageProto.Message.ActionType action, ChannelHandlerContext ctx, String msg) throws Exception;
    
}

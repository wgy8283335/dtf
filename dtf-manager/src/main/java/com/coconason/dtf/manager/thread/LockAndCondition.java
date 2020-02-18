package com.coconason.dtf.manager.thread;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.protobufserver.NettyServer;
import com.coconason.dtf.manager.utils.MessageSender;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Lock and condition.
 * 
 * @Author: Jason
 */
public final class LockAndCondition implements LockAndConditionInterface {
    
    /**
     * Logger of LockAndCondition class.
     */
    private final Logger logger = LoggerFactory.getLogger(LockAndCondition.class);
    
    /**
     * Lock.
     */
    private Lock lock;
    
    /**
     * Condition.
     */
    private Condition condition;
    
    public LockAndCondition(final Lock lock) {
        this.lock = lock;
        this.condition = lock.newCondition();
    }
    
    /**
     * Sait for signal.
     */
    @Override
    public void await() {
        try {
            lock.lock();
            condition.await();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Wait for signal or overtime.
     * @param milliseconds number
     * @param timeUnit time unit
     * @return
     */
    @Override
    public boolean await(final long milliseconds, final TimeUnit timeUnit) {
        boolean result = false;
        try {
            lock.lock();
            result = condition.await(milliseconds, timeUnit);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * Send notification to unblock.
     */
    @Override
    public void signal() {
        try {
            lock.lock();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Send message to netty channel and wait for signal.
     * If fail, keep trying util success. 
     * 
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     * @param msg string
     * @throws ServerChannelException exception
     */
    @Override
    public void sendAndWaitForSignal(final String groupId, final MessageProto.Message.ActionType action, 
                                     final ChannelHandlerContext ctx, final String msg) throws ServerChannelException {
        MessageSender.sendMsg(groupId, action, ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        while (!receivedSignal) {
            boolean channelIsHealthy = NettyServer.isHealthy();
            if (channelIsHealthy) {
                MessageSender.sendMsg(groupId, action, ctx);
                receivedSignal = await(60000, TimeUnit.MILLISECONDS);
            } else {
                //should write log.
                logger.error(msg + "\n" + "groupId:" + groupId + "\n" + "action:" + action);
                throw new ServerChannelException(msg);
            }
        }
    }
    
    /**
     * Send message to netty channel and wait for signal.
     * If fail, throw exception. 
     *
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     * @param msg string
     * @throws ServerChannelException exception
     */
    @Override
    public void sendAndWaitForSignalOnce(final String groupId, final MessageProto.Message.ActionType action, 
                                         final ChannelHandlerContext ctx, final String msg) throws ServerChannelException {
        MessageSender.sendMsg(groupId, action, ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        if (!receivedSignal) {
            logger.error(msg + "\n" + "groupId:" + groupId + "\n" + "action:" + action);
            throw new ServerChannelException(msg);
        }
    }
    
    /**
     * Send message to netty channel and wait for signal.
     * If fail, send failure message to the channel. 
     *
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     * @param msg string
     * @throws ServerChannelException exception
     */
    @Override
    public void sendAndWaitForSignalIfFailSendMessage(final String groupId, final MessageProto.Message.ActionType action, 
                                                      final ChannelHandlerContext ctx, final String msg) throws ServerChannelException {
        MessageSender.sendMsg(groupId, action, ctx);
        boolean receivedSignal = await(10000, TimeUnit.MILLISECONDS);
        if (!receivedSignal) {
            logger.error(msg + "\n" + "groupId:" + groupId + "\n" + "action:" + action);
            MessageSender.sendMsg(groupId.substring(0, 18), MessageProto.Message.ActionType.WHOLE_FAIL_STRONG, ctx);
            throw new ServerChannelException(msg);
        }
    }
    
}

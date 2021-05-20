package com.dtf.manager.service;

import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.dtf.manager.thread.LockAndCondition;
import com.dtf.manager.thread.ServerChannelException;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Send message.
 * 
 * @Author: wangguangyuan
 */
public final class SendMessageRunnable implements Runnable {
    
    /**
     * Logger of SendMessageRunnable.
     */
    private Logger logger = LoggerFactory.getLogger(SendMessageRunnable.class);
    
    /**
     * Group id.
     */
    private String groupId;
    
    /**
     * Action type.
     */
    private ActionType actionType;
    
    /**
     * Channel handler context.
     */
    private ChannelHandlerContext ctx;
    
    /**
     * Message.
     */
    private String message;
    
    /**
     * Cache for thread lock.
     */
    private Cache serverThreadLockCacheProxy;
    
    public SendMessageRunnable(final String groupId, final ActionType actionType, final ChannelHandlerContext ctx,
                               final String message, final Cache serverThreadLockCacheProxy) {
        this.groupId = groupId;
        this.actionType = actionType;
        this.ctx = ctx;
        this.message = message;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
    }
    
    /**
     * Send message according to action type.
     */
    @Override
    public void run() {
        LockAndCondition lc = new LockAndCondition(new ReentrantLock());
        serverThreadLockCacheProxy.put(groupId, lc);
        try {
            if (actionType == ActionType.APPROVESUBMIT) {
                lc.sendAndWaitForSignal(groupId, actionType, ctx, message);
                return;
            }
            if (actionType == ActionType.APPROVESUBMIT_STRONG) {
                lc.sendAndWaitForSignalIfFailSendMessage(groupId, actionType, ctx, message);
                return;
            } else {
                lc.sendAndWaitForSignalOnce(groupId, actionType, ctx, message);
                return;
            }
        } catch (ServerChannelException e) {
            logger.error(e.getMessage());
        }
    }
    
}

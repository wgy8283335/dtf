package com.coconason.dtf.manager.protobufserver;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.protobufserver.strategy.HandleStrategyContext;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServerProxy;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * Server transaction handler.
 * This part is the core of Manager.
 * 
 * @Author: Jason
 */
public final class ServerTransactionHandler extends ChannelInboundHandlerAdapter {
    
    /**
     * Logger of server transaction handler.
     */
    private Logger logger = LoggerFactory.getLogger(ServerTransactionHandler.class);
    
    /**
     * Normal message cache in synchronous mode.
     */
    private MessageCacheInterface messageSyncCacheProxy;
    
    /**
     * Message cache of submitting in synchronous mode.
     */
    private MessageCacheInterface messageForSubmitSyncCacheProxy;
    
    /**
     * Normal message cache in asynchronous mode.
     */
    private MessageCacheInterface messageAsyncCacheProxy;
    
    /**
     * Message cache of submitting in asynchronous mode.
     */
    private MessageCacheInterface messageForSubmitAsyncCacheProxy;
    
    /**
     * Queue of asynchronous message.
     */
    private Queue messageAsyncQueueProxy;

    /**
     * Channel handler context.
     */
    private ChannelHandlerContext ctx;

    /**
     * Thread pool.
     */
    private ExecutorService threadPoolForServerProxy;

    /**
     * Cache of thread lock.
     */
    private ServerThreadLockCacheProxy serverThreadLockCacheProxy;
    
    public ServerTransactionHandler(final MessageCacheInterface messageSyncCacheProxy, final MessageCacheInterface messageAsyncCacheProxy,
                                    final MessageAsyncQueueProxy messageAsyncQueueProxy, final ThreadPoolForServerProxy threadPoolForServerProxy,
                                    final MessageCacheInterface messageForSubmitSyncCacheProxy, final MessageCacheInterface messageForSubmitAsyncCacheProxy,
                                    final ServerThreadLockCacheProxy serverThreadLockCacheProxy) {
        this.messageSyncCacheProxy = messageSyncCacheProxy;
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
        this.messageAsyncCacheProxy = messageAsyncCacheProxy;
        this.threadPoolForServerProxy = threadPoolForServerProxy;
        this.messageForSubmitSyncCacheProxy = messageForSubmitSyncCacheProxy;
        this.messageForSubmitAsyncCacheProxy = messageForSubmitAsyncCacheProxy;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
    }
    
    /**
     * Override channelActive method.
     * 
     * @param ctx channel handler context
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
    
    /**
     * Override channelReadComplete method.
     * 
     * @param ctx channel handler context
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    /**
     * Receive message from the channel and relevant treatment.
     * 
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        logger.debug(msg.toString());
        MessageProto.Message message = (MessageProto.Message) msg;
        ActionType actionType = message.getAction();
        HandleStrategyContext.getInstance().handleMessageAccordingToAction(actionType, this, ctx, msg);
        ctx.fireChannelRead(msg);
    }
    
    /**
     * Get normal message cache in synchronous mode.
     * 
     * @return cache
     */
    public MessageCacheInterface getMessageSyncCacheProxy() {
        return messageSyncCacheProxy;
    }
    
    /**
     * Get message cache of submitting in synchronous mode.
     *
     * @return cache
     */
    public MessageCacheInterface getMessageForSubmitSyncCacheProxy() {
        return messageForSubmitSyncCacheProxy;
    }
    
    /**
     * Get normal message cache in asynchronous mode.
     *
     * @return cache
     */
    public MessageCacheInterface getMessageAsyncCacheProxy() {
        return messageAsyncCacheProxy;
    }

    /**
     * Get message cache of submitting in asynchronous mode.
     *
     * @return cache
     */
    public MessageCacheInterface getMessageForSubmitAsyncCacheProxy() {
        return messageForSubmitAsyncCacheProxy;
    }
    
    /**
     * Get queue of asynchronous message.
     *
     * @return queue
     */
    public Queue getMessageAsyncQueueProxy() {
        return messageAsyncQueueProxy;
    }
    
    /**
     * Channel handler context.
     *
     * @return ctx
     */
    public ChannelHandlerContext getChannelHandlerContext() {
        return ctx;
    }
    
    /**
     * Thread pool.
     *
     * @return thread pool
     */
    public ExecutorService getThreadPoolForServerProxy() {
        return threadPoolForServerProxy;
    }
    
    /**
     * Cache of thread lock.
     *
     * @return cache
     */
    public ServerThreadLockCacheProxy getServerThreadLockCacheProxy() {
        return serverThreadLockCacheProxy;
    }
}

package com.coconason.dtf.manager.protobufserver.strategy;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.service.CancelRunnable;
import com.coconason.dtf.manager.service.CheckAndSubmitRunnable;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

/**
 * Handle message for cancel action.
 * 
 * @Author: Jason
 */
public class HandleMessageForCancel implements HandleMessageStrategy {
    
    /**
     * Handle message for cancel action.
     * 
     * @param serverTransactionHandler server transaction handler
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        ExecutorService threadPoolForServerProxy = serverTransactionHandler.getThreadPoolForServerProxy();
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        threadPoolForServerProxy.execute(new CancelRunnable(message, MessageProto.Message.ActionType.CANCEL, ctx, 
                messageSyncCacheProxy, serverThreadLockCacheProxy, threadPoolForServerProxy));
    }
    
}

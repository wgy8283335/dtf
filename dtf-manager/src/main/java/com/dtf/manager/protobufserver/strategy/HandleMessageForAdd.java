package com.dtf.manager.protobufserver.strategy;

import com.dtf.common.protobuf.MessageProto;
import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.message.TransactionMessageFactory;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.service.CheckAndSubmitRunnable;
import com.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

/**
 * Handle message for add action.
 * 
 * @Author: wangguangyuan
 */
public class HandleMessageForAdd implements HandleMessageStrategy {
    
    /**
     * Handle message for add action.
     * 
     * @param serverTransactionHandler server transaction handler
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        ExecutorService threadPoolForServerProxy = serverTransactionHandler.getThreadPoolForServerProxy();
        MessageCacheInterface messageForSubmitSyncCacheProxy = serverTransactionHandler.getMessageForSubmitSyncCacheProxy();
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        messageSyncCacheProxy.putDependsOnCondition(TransactionMessageFactory.getMessageGroupInstance(message, ctx));
        threadPoolForServerProxy.execute(new CheckAndSubmitRunnable(message, ActionType.ADD, ctx, messageForSubmitSyncCacheProxy, 
                messageSyncCacheProxy, serverThreadLockCacheProxy, threadPoolForServerProxy));
    }
    
}

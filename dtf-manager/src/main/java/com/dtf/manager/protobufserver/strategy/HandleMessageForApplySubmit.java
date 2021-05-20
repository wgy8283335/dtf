package com.dtf.manager.protobufserver.strategy;

import com.dtf.common.protobuf.MessageProto;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.message.TransactionMessageFactory;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.service.CheckAndSubmitRunnable;
import com.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

/**
 * Handle message for apply submit action.
 * 
 * @Author: wangguangyuan
 */
public class HandleMessageForApplySubmit implements HandleMessageStrategy {

    /**
     * Handle message for apply submit action.
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
        TransactionMessageGroupInterface transactionMessageForSubmit = TransactionMessageFactory.getMessageForSubmitInstance(message);
        messageForSubmitSyncCacheProxy.put(transactionMessageForSubmit.getGroupId(), transactionMessageForSubmit);
        threadPoolForServerProxy.execute(new CheckAndSubmitRunnable(message, MessageProto.Message.ActionType.APPROVESUBMIT, ctx, 
                messageForSubmitSyncCacheProxy, messageSyncCacheProxy, serverThreadLockCacheProxy, threadPoolForServerProxy));
    }
    
}

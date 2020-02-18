package com.coconason.dtf.manager.protobufserver.strategy;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.TransactionMessageFactory;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.service.SendAsyncRequestRunnable;
import com.coconason.dtf.manager.service.SendShortMessageRunnable;
import com.coconason.dtf.manager.utils.SetUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Handle message for add synchronous.
 * 
 * @Author: Jason
 */
public class HandleMessageForAddAsynchronous implements HandleMessageStrategy {

    /**
     * Handle message for add synchronous.
     * 
     * @param serverTransactionHandler server transaction handler
     * @param ctx channel handler context
     * @param msg message
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        Queue messageAsyncQueueProxy = serverTransactionHandler.getMessageAsyncQueueProxy();
        MessageCacheInterface messageAsyncCacheProxy = serverTransactionHandler.getMessageAsyncCacheProxy();
        ExecutorService threadPoolForServerProxy = serverTransactionHandler.getThreadPoolForServerProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        TransactionMessageGroupInterface transactionMessageGroupAsync = null;
        transactionMessageGroupAsync = TransactionMessageFactory.getMessageGroupAsyncInstance(message);
        messageAsyncCacheProxy.putDependsOnCondition(transactionMessageGroupAsync);
        threadPoolForServerProxy.execute(new SendShortMessageRunnable(transactionMessageGroupAsync.getGroupId(), MessageProto.Message.ActionType.ADD_SUCCESS_ASYNC, ctx));
        Set<String> setFromCacheTemp = SetUtil.setTransfer(messageAsyncCacheProxy.get(transactionMessageGroupAsync.getGroupId()).getMemberSet());
        MessageCacheInterface messageForSubmitAsyncCacheProxy = serverTransactionHandler.getMessageForSubmitAsyncCacheProxy();
        TransactionMessageGroupInterface transactionMessageForSubmit1 = messageForSubmitAsyncCacheProxy.get(transactionMessageGroupAsync.getGroupId());
        if (transactionMessageForSubmit1 != null) {
            Set<String> setFromMessageTemp = transactionMessageForSubmit1.getMemberSet();
            setFromMessageTemp.remove("1");
            if (setFromMessageTemp != null && SetUtil.isSetEqual(setFromCacheTemp, setFromMessageTemp)) {
                threadPoolForServerProxy.execute(new SendAsyncRequestRunnable(messageAsyncCacheProxy, transactionMessageForSubmit1, messageAsyncQueueProxy));
            }
        }
    }
    
}

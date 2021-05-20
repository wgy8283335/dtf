package com.dtf.manager.protobufserver.strategy;

import com.dtf.common.protobuf.MessageProto;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.message.TransactionMessageFactory;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.service.SendAsyncRequestRunnable;
import com.dtf.manager.service.SendShortMessageRunnable;
import com.dtf.manager.utils.SetUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Handle message for add synchronous.
 * 
 * @Author: wangguangyuan
 */
public class HandleMessageForAddAsynchronous implements HandleMessageStrategy {
    
    /**
     * Handle message for add synchronous.
     * Add message into the messageAsyncCacheProxy, and send ack message.
     * Check number of member of the submit message whether equals to the number of member of cache.
     * If equal, means that the all of member of the transaction has been received by server, then run the request of each member.
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
        Set<String> setFromCacheTemp = SetUtil.setTransfer(messageAsyncCacheProxy.get(transactionMessageGroupAsync.getGroupId()).getMemberSet());
        MessageCacheInterface messageForSubmitAsyncCacheProxy = serverTransactionHandler.getMessageForSubmitAsyncCacheProxy();
        TransactionMessageGroupInterface transactionMessageForSubmit1 = messageForSubmitAsyncCacheProxy.get(transactionMessageGroupAsync.getGroupId());
        if (transactionMessageForSubmit1 != null) {
            Set<String> setFromMessageTemp = transactionMessageForSubmit1.getMemberSet();
            setFromMessageTemp.remove("1");
            if (setFromMessageTemp != null && SetUtil.isSetEqual(setFromCacheTemp, setFromMessageTemp)) {
                threadPoolForServerProxy.execute(new SendAsyncRequestRunnable(messageAsyncCacheProxy, transactionMessageForSubmit1, messageAsyncQueueProxy,
                        transactionMessageGroupAsync.getGroupId(), MessageProto.Message.ActionType.COMMIT_SUCCESS_ASYNC, ctx));
            }
        }
        threadPoolForServerProxy.execute(new SendShortMessageRunnable(transactionMessageGroupAsync.getGroupId(), MessageProto.Message.ActionType.ADD_SUCCESS_ASYNC, ctx));
    }
    
}

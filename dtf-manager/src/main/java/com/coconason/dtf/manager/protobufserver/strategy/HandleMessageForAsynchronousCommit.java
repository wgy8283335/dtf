package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
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
 * Handle message for asynchronous commit.
 * 
 * @Author: Jason
 */
public class HandleMessageForAsynchronousCommit implements HandleMessageStrategy {

    /**
     * Handle message for asynchronous commit.
     * Add message into the messageForSubmitAsyncCacheProxy, and send ack message.
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
        MessageCacheInterface messageForSubmitAsyncCacheProxy = serverTransactionHandler.getMessageForSubmitAsyncCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        JSONObject map = JSONObject.parseObject(message.getInfo());
        String groupId = map.get("groupId").toString();
        TransactionMessageGroupInterface transactionMessageForSubmitTemp = TransactionMessageFactory.getMessageForSubmitInstance(message);
        messageForSubmitAsyncCacheProxy.put(transactionMessageForSubmitTemp.getGroupId(), transactionMessageForSubmitTemp);
        threadPoolForServerProxy.execute(new SendShortMessageRunnable(groupId, MessageProto.Message.ActionType.COMMIT_SUCCESS_ASYNC, ctx));
        TransactionMessageGroupInterface transactionMessageGroupAsync1 = messageAsyncCacheProxy.get(groupId);
        if (transactionMessageGroupAsync1 != null) {
            Set<String> setFromCache = SetUtil.setTransfer(transactionMessageGroupAsync1.getMemberSet());
            Set<String> setFromMessage = transactionMessageForSubmitTemp.getMemberSet();
            setFromMessage.remove("1");
            if (SetUtil.isSetEqual(setFromCache, setFromMessage)) {
                threadPoolForServerProxy.execute(new SendAsyncRequestRunnable(messageAsyncCacheProxy, transactionMessageForSubmitTemp, messageAsyncQueueProxy));
            }
        }
    }
    
}

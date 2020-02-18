package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.service.SendMessageRunnable;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

/**
 * Handle message for sub failure strong action.
 * 
 * @Author: Jason
 */
public class HandleMessageForSubFailStrong implements HandleMessageStrategy {
    
    /**
     * Handle message for sub failure strong action.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        ExecutorService threadPoolForServerProxy = serverTransactionHandler.getThreadPoolForServerProxy();
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        TransactionMessageGroupInterface groupTemp1 = messageSyncCacheProxy.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
        String groupId1 = groupTemp1.getGroupId();
        threadPoolForServerProxy.execute(new SendMessageRunnable(groupId1, MessageProto.Message.ActionType.WHOLE_FAIL_STRONG, groupTemp1.getCtx(), 
                "send WHOLE_FAIL_STRONG message fail", serverThreadLockCacheProxy));
        messageSyncCacheProxy.invalidate(groupTemp1.getGroupId());
    }
    
}

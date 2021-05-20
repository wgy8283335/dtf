package com.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.service.SendMessageRunnable;
import com.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

/**
 * Handle message for sub failure strong action.
 * 
 * @Author: wangguangyuan
 */
public class HandleMessageForSubFailStrong implements HandleMessageStrategy {
    
    /**
     * Handle message for sub failure strong action.
     * If receive SUB_FAIL_STRONG, then send WHOLE_FAIL_STRONG and invalidate the corresponding element in the cache. 
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        ExecutorService threadPoolForServerProxy = serverTransactionHandler.getThreadPoolForServerProxy();
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        TransactionMessageGroupInterface groupTemp = messageSyncCacheProxy.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
        String groupId1 = groupTemp.getGroupId();
        threadPoolForServerProxy.execute(new SendMessageRunnable(groupId1, MessageProto.Message.ActionType.WHOLE_FAIL_STRONG, groupTemp.getCtx(), 
                "send WHOLE_FAIL_STRONG message fail", serverThreadLockCacheProxy));
        messageSyncCacheProxy.invalidate(groupTemp.getGroupId());
    }
    
}

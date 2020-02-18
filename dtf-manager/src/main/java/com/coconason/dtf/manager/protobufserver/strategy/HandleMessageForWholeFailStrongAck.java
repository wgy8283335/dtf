package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * Handle message for whole fail strong ack action.
 * 
 * @Author: Jason
 */
public class HandleMessageForWholeFailStrongAck implements HandleMessageStrategy {
    
    /**
     * Handle message for whole fail strong ack action.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String tempGroupId2 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        LockAndConditionInterface tempLc2 = serverThreadLockCacheProxy.getIfPresent(tempGroupId2);
        tempLc2.signal();
    }
    
}

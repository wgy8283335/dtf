package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handle message for shole success strong ack action.
 * 
 * @Author: Jason
 */
public class HandleMessageForWholeSuccessStrongAck implements HandleMessageStrategy {
    
    /**
     * Handle message for whole success strong ack action.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String tempGroupId1 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        LockAndConditionInterface tempLc1 = serverThreadLockCacheProxy.getIfPresent(tempGroupId1);
        tempLc1.signal();
    }
}

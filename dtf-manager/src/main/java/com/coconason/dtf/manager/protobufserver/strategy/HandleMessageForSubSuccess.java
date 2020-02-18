package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handle message for sub success.
 * 
 * @Author: Jason
 */
public class HandleMessageForSubSuccess implements HandleMessageStrategy {
    
    /**
     * Handle message for sub success.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String memberId2 = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
        String groupTempId2 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        LockAndConditionInterface lc2 = serverThreadLockCacheProxy.getIfPresent(groupTempId2 + memberId2);
        lc2.signal();
    }
}

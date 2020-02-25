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
     * ？？Why should “lc.signal”
     * 
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
        String groupTempId = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        LockAndConditionInterface lc = serverThreadLockCacheProxy.getIfPresent(groupTempId + memberId);
        lc.signal();
    }
}

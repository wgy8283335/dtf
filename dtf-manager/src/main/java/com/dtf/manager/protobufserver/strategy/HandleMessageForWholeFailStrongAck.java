package com.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.thread.LockAndConditionInterface;
import com.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

/**
 * Handle message for whole fail strong ack action.
 * 
 * @author wangguangyuan
 */
public class HandleMessageForWholeFailStrongAck implements HandleMessageStrategy {
    
    /**
     * Handle message for whole fail strong ack action.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String tempGroupId = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        LockAndConditionInterface lc = serverThreadLockCacheProxy.getIfPresent(tempGroupId);
        lc.signal();
    }
    
}

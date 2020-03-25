package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.log.LogUtilForSyncFinalFailure;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle message for sub failure action.
 * 
 * @Author: Jason
 */
public class HandleMessageForSubFail implements HandleMessageStrategy {
    
    /**
     * Logger of handle message for sub failure.
     */
    private Logger logger = LoggerFactory.getLogger(HandleMessageForSubFail.class);
    
    /**
     * Handle message for sub failure action.
     * If receive SUB_FAIL, then remove corresponding element from the cache.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
        String groupTempId = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        TransactionMessageGroupInterface groupInfoToLog = messageSyncCacheProxy.get(groupTempId);
        logger.error("SUB FAIL :" + groupInfoToLog.toString());
        LogUtilForSyncFinalFailure.getInstance().append(groupInfoToLog.toString());
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        LockAndConditionInterface lc = serverThreadLockCacheProxy.getIfPresent(groupTempId + memberId);
        lc.signal();
    }
    
}

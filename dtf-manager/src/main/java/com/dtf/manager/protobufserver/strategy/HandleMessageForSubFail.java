package com.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.log.LogUtilForSyncFinalFailure;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.thread.LockAndConditionInterface;
import com.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle message for sub failure action.
 * 
 * @author wangguangyuan
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

package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.log.LogUtilForSyncFinalSuccess;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle message for sub success.
 * 
 * @Author: Jason
 */
public class HandleMessageForSubSuccess implements HandleMessageStrategy {

    /**
     * Logger of handle message for sub failure.
     */
    private Logger logger = LoggerFactory.getLogger(HandleMessageForSubSuccess.class);
    
    /**
     * Handle message for sub success.
     * lc signal the SendMessageRunnable with APPROVESUBMIT.
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageProto.Message message = (MessageProto.Message) msg;
        String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
        String groupTempId = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        TransactionMessageGroupInterface groupInfoToLog = messageSyncCacheProxy.get(groupTempId);
        //logger.debug("SUB SUCCESS:" + groupInfoToLog.toString());
        logger.debug("SUB SUCCESS:" + message.getInfo());
        LogUtilForSyncFinalSuccess.getInstance().append(groupInfoToLog.toString());
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        LockAndConditionInterface lc = serverThreadLockCacheProxy.getIfPresent(groupTempId + memberId);
        lc.signal();
    }
}

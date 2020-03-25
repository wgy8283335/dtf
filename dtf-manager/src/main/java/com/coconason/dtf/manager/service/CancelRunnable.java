package com.coconason.dtf.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.TransactionMessageForAdding;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Check and submit runnable.
 * Process action, includes: ADD, ADD_STRONG, APPROVESUBMIT, APPROVESUBMIT_STRONG, CANCEL.
 * 
 * @Author: Jason
 */
public final class CancelRunnable implements Runnable {
    
    private Logger logger = LoggerFactory.getLogger(CancelRunnable.class);
    
    private MessageProto.Message message;

    private ActionType actionType;
    
    private ChannelHandlerContext ctx;
    
    private MessageCacheInterface messageSyncCacheProxy;
    
    private Cache serverThreadLockCacheProxy;
    
    private ExecutorService threadPoolForServerProxy;
    
    public CancelRunnable(final MessageProto.Message message, final ActionType actionType, final ChannelHandlerContext ctx, 
                          final MessageCacheInterface messageSyncCacheProxy, final Cache serverThreadLockCacheProxy, final ExecutorService threadPoolForServerProxy) {
        this.message = message;
        this.actionType = actionType;
        this.ctx = ctx;
        this.messageSyncCacheProxy = messageSyncCacheProxy;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
        this.threadPoolForServerProxy = threadPoolForServerProxy;
    }

    /**
     * Send CANCEL to member in the list.
     */
    @Override
    public void run() {
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        TransactionMessageGroupInterface elementFromCache = messageSyncCacheProxy.get(groupId);
        if (null == elementFromCache) {
            return;
        }
        elementFromCache.setCtxForSubmitting(ctx);
        messageSyncCacheProxy.put(elementFromCache.getGroupId(), elementFromCache);
        List<TransactionMessageForAdding> memberList = elementFromCache.getMemberList();
        goThroughMemberListAndSendMessage(memberList, elementFromCache);
    }
    
    private void goThroughMemberListAndSendMessage(final List<TransactionMessageForAdding> memberList, final TransactionMessageGroupInterface elementFromCache) {
        for (TransactionMessageForAdding messageForAdding : memberList) {
            logger.debug("Send CANCEL message:" + messageForAdding.toString());
            threadPoolForServerProxy.execute(new SendMessageRunnable(elementFromCache.getGroupId() + messageForAdding.getGroupMemberId(),
                    ActionType.CANCEL, messageForAdding.getCtx(), "send CANCEL message fail", serverThreadLockCacheProxy));
        }
    }
    
}

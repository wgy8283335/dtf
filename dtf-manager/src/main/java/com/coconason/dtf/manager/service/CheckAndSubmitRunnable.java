package com.coconason.dtf.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.log.LogUtilForSyncApproveSubmit;
import com.coconason.dtf.manager.message.TransactionMessageFactory;
import com.coconason.dtf.manager.message.TransactionMessageForAdding;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.utils.SetUtil;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Check and submit runnable.
 * Process action, includes: ADD, ADD_STRONG, APPROVESUBMIT, APPROVESUBMIT_STRONG, CANCEL.
 * 
 * @Author: Jason
 */
public final class CheckAndSubmitRunnable implements Runnable {
    
    private Logger logger = LoggerFactory.getLogger(CheckAndSubmitRunnable.class);
    
    private MessageProto.Message message;
    
    private ActionType actionType;
    
    private ChannelHandlerContext ctx;
    
    private MessageCacheInterface messageForSubmitSyncCacheProxy;
    
    private MessageCacheInterface messageSyncCacheProxy;
    
    private Cache serverThreadLockCacheProxy;
    
    private ExecutorService threadPoolForServerProxy;
    
    public CheckAndSubmitRunnable(final MessageProto.Message message, final ActionType actionType, final ChannelHandlerContext ctx, final MessageCacheInterface messageForSubmitSyncCacheProxy,
                                  final MessageCacheInterface messageSyncCacheProxy, final Cache serverThreadLockCacheProxy, final ExecutorService threadPoolForServerProxy) {
        this.message = message;
        this.actionType = actionType;
        this.ctx = ctx;
        this.messageForSubmitSyncCacheProxy = messageForSubmitSyncCacheProxy;
        this.messageSyncCacheProxy = messageSyncCacheProxy;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
        this.threadPoolForServerProxy = threadPoolForServerProxy;
    }

    /**
     * Due to network delay. ADD message may be later than SUBMIT message.
     * So, no matter receive ADD or SUBMIT, should check the cache to decide whether to approve request of the whole group.
     * If action is add or add strong, try to get element from messageForSubmitSyncCacheProxy, then get member set1.
     * If action is approve submit or approve submit strong or cancel,try create tmfs by message ,then get member set2.
     * Then Get member set2 from element of messageSyncCacheProxy.
     * If set1 equals set2, then send approve message. Otherwise, return.
     */
    @Override
    public void run() {
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        Object obj = info.get("groupMemberSet");
        TransactionMessageGroupInterface tmfs = null;
        if (actionType == ActionType.ADD || actionType == ActionType.ADD_STRONG) {
            tmfs = messageForSubmitSyncCacheProxy.get(groupId);
        } else if (actionType == ActionType.APPLYFORSUBMIT || actionType == ActionType.APPLYFORSUBMIT_STRONG || actionType == ActionType.CANCEL) {
            tmfs = TransactionMessageFactory.getMessageForSubmitInstance(message);
        }
        if (tmfs == null || tmfs.getMemberSet().isEmpty() || messageSyncCacheProxy.get(tmfs.getGroupId()) == null) {
            return;
        }
        Set setFromMessage = tmfs.getMemberSet();
        TransactionMessageGroupInterface elementFromCache = messageSyncCacheProxy.get(tmfs.getGroupId());
        elementFromCache.setCtxForSubmitting(ctx);
        messageSyncCacheProxy.put(elementFromCache.getGroupId(), elementFromCache);
        List<TransactionMessageForAdding> memberList = elementFromCache.getMemberList();
        if (setFromMessage.isEmpty()) {
            return;
        }
        Set setFromCache = elementFromCache.getMemberSet();
        if (!SetUtil.isSetEqual(setFromMessage, setFromCache)) {
            return;
        }
        goThroughMemberListAndSendMessage(memberList, elementFromCache, tmfs);
    }
    
    private void goThroughMemberListAndSendMessage(final List<TransactionMessageForAdding> memberList, final TransactionMessageGroupInterface elementFromCache, 
                                                   final TransactionMessageGroupInterface tmfs) {
        for (TransactionMessageForAdding messageForAdding : memberList) {
            if (actionType == ActionType.ADD || actionType == ActionType.APPROVESUBMIT) {
                LogUtilForSyncApproveSubmit.getInstance().append(messageForAdding.toString());
                threadPoolForServerProxy.execute(new SendMessageRunnable(elementFromCache.getGroupId() + messageForAdding.getGroupMemberId(),
                        ActionType.APPROVESUBMIT, messageForAdding.getCtx(), "send APPROVESUBMIT message fail", serverThreadLockCacheProxy));
            } else if (actionType == ActionType.ADD_STRONG || actionType == ActionType.APPROVESUBMIT_STRONG) {
                threadPoolForServerProxy.execute(new SendMessageRunnable(elementFromCache.getGroupId() + messageForAdding.getGroupMemberId(),
                        ActionType.APPROVESUBMIT_STRONG, messageForAdding.getCtx(), "send APPROVESUBMIT_STRONG message fail", serverThreadLockCacheProxy));
            } else if (actionType == ActionType.CANCEL) {
                threadPoolForServerProxy.execute(new SendMessageRunnable(elementFromCache.getGroupId() + messageForAdding.getGroupMemberId(),
                        ActionType.CANCEL, messageForAdding.getCtx(), "send CANCEL message fail", serverThreadLockCacheProxy));
            }
        }
    }
    
}

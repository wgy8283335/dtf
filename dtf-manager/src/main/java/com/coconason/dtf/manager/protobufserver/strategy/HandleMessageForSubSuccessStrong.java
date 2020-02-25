package com.coconason.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.TransactionMessageForAdding;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.protobufserver.ServerTransactionHandler;
import com.coconason.dtf.manager.service.SendMessageRunnable;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Handle message for sub success strong action.
 * 
 * @Author: Jason
 */
public class HandleMessageForSubSuccessStrong implements HandleMessageStrategy {

    /**
     * Handle message for sub success strong action.
     * ？？Why should “lc.signal”
     */
    @Override
    public void handleMessage(final ServerTransactionHandler serverTransactionHandler, final ChannelHandlerContext ctx, final Object msg) {
        MessageCacheInterface messageSyncCacheProxy = serverTransactionHandler.getMessageSyncCacheProxy();
        ExecutorService threadPoolForServerProxy = serverTransactionHandler.getThreadPoolForServerProxy();
        ServerThreadLockCacheProxy serverThreadLockCacheProxy = serverTransactionHandler.getServerThreadLockCacheProxy();
        MessageProto.Message message = (MessageProto.Message) msg;
        String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
        TransactionMessageGroupInterface groupTemp = messageSyncCacheProxy.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
        LockAndConditionInterface lc1 = serverThreadLockCacheProxy.getIfPresent(groupTemp.getGroupId() + memberId);
        lc1.signal();
        //1.check the group.If all of members are success,reply to the creator.
        List<TransactionMessageForAdding> memberList = groupTemp.getMemberList();
        for (TransactionMessageForAdding member:memberList) {
            if (memberId.equals(member.getGroupMemberId())) {
                member.setCommitted(true);
            }
        }
        boolean flag = memberList == null ? false : true;
        for (TransactionMessageForAdding member:memberList) {
            if (!member.isCommitted()) {
                flag = false;
                break;
            }
        }
        if (flag) {
            String groupId = groupTemp.getGroupId();
            threadPoolForServerProxy.execute(new SendMessageRunnable(groupId, MessageProto.Message.ActionType.WHOLE_SUCCESS_STRONG, 
                    groupTemp.getCtx(), "send WHOLE_SUCCESS_STRONG message fail", serverThreadLockCacheProxy));
            messageSyncCacheProxy.invalidate(groupId);
        }
    }
}

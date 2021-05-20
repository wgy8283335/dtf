package com.dtf.manager.protobufserver.strategy;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.message.TransactionMessageForAdding;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.dtf.manager.protobufserver.ServerTransactionHandler;
import com.dtf.manager.service.SendMessageRunnable;
import com.dtf.manager.thread.LockAndConditionInterface;
import com.dtf.manager.thread.ServerThreadLockCacheProxy;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Handle message for sub success strong action.
 * 
 * @Author: wangguangyuan
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

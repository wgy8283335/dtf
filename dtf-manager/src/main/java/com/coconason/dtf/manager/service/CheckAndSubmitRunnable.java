package com.coconason.dtf.manager.service;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.cache.MessageForSubmitSyncCacheProxy;
import com.coconason.dtf.manager.cache.MessageSyncCacheProxy;
import com.coconason.dtf.manager.message.TransactionMessageForAdding;
import com.coconason.dtf.manager.message.TransactionMessageForSubmit;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServerProxy;
import com.coconason.dtf.manager.utils.SetUtil;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @Author: Jason
 * @date: 2018/9/19-10:26
 */
public class CheckAndSubmitRunnable implements Runnable{

    private MessageProto.Message message;

    private ActionType actionType;

    private ChannelHandlerContext ctx;

    private MessageCacheInterface messageForSubmitSyncCacheProxy;

    private MessageCacheInterface messageSyncCacheProxy;

    private Cache serverThreadLockCacheProxy;

    private ExecutorService threadPoolForServerProxy;

    public CheckAndSubmitRunnable(MessageProto.Message message, ActionType actionType, ChannelHandlerContext ctx, MessageCacheInterface messageForSubmitSyncCacheProxy, MessageCacheInterface messageSyncCacheProxy, Cache serverThreadLockCacheProxy, ExecutorService threadPoolForServerProxy) {
        this.message = message;
        this.actionType = actionType;
        this.ctx = ctx;
        this.messageForSubmitSyncCacheProxy = messageForSubmitSyncCacheProxy;
        this.messageSyncCacheProxy = messageSyncCacheProxy;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
        this.threadPoolForServerProxy = threadPoolForServerProxy;
    }

    @Override
    public void run() {
        try{
            JSONObject info = JSONObject.parseObject(message.getInfo());
            String groupId = info.get("groupId").toString();
            Object obj = info.get("groupMemberSet");
            TransactionMessageGroupInterface tmfs = obj == null ? messageForSubmitSyncCacheProxy.get(groupId):new TransactionMessageForSubmit(message);
            if(tmfs == null||tmfs.getMemberSet().isEmpty()|| messageSyncCacheProxy.get(tmfs.getGroupId())==null){
                return;
            }
            Set setFromMessage =tmfs.getMemberSet();
            TransactionMessageGroupInterface elementFromCache = messageSyncCacheProxy.get(tmfs.getGroupId());
            Set setFromCache = elementFromCache.getMemberSet();
            elementFromCache.setCtxForSubmitting(ctx);
            messageSyncCacheProxy.put(elementFromCache.getGroupId(),elementFromCache);
            List<TransactionMessageForAdding> memberList = elementFromCache.getMemberList();
            //check whether the member from message has the same element as the member from cache.
            if(!setFromMessage.isEmpty()){
                if(SetUtil.isSetEqual(setFromMessage,setFromCache)){
                    for (TransactionMessageForAdding messageForAdding: memberList) {
                        if(actionType == ActionType.ADD || actionType == ActionType.APPROVESUBMIT){
                            System.out.println("Send transaction message:\n" + message);
                            threadPoolForServerProxy.execute(new SendMessageRunnable(elementFromCache.getGroupId()+messageForAdding.getGroupMemberId(),ActionType.APPROVESUBMIT,messageForAdding.getCtx(),"send APPROVESUBMIT message fail", serverThreadLockCacheProxy));
                        }else{
                            //success
                            System.out.println("Send transaction message:\n" + message);
                            //int i = 6/0;
                            threadPoolForServerProxy.execute(new SendMessageRunnable(elementFromCache.getGroupId()+messageForAdding.getGroupMemberId(),ActionType.APPROVESUBMIT_STRONG,messageForAdding.getCtx(),"send APPROVESUBMIT_STRONG message fail", serverThreadLockCacheProxy));
                        }
                    }
                    if(actionType == ActionType.ADD || actionType == ActionType.APPROVESUBMIT){
                        messageSyncCacheProxy.invalidate(tmfs.getGroupId());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
package com.coconason.dtf.manager.service;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.thread.LockAndCondition;
import com.google.common.cache.Cache;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/9/26-17:02
 */
public final class SendMessageRunnable implements Runnable {
    private String groupId;
    private ActionType actionType;
    private ChannelHandlerContext ctx;
    private String message;
    private Cache serverThreadLockCacheProxy;

    public SendMessageRunnable(String groupId, ActionType actionType, ChannelHandlerContext ctx, String message,Cache serverThreadLockCacheProxy) {
        this.groupId = groupId;
        this.actionType = actionType;
        this.ctx = ctx;
        this.message = message;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
    }

    @Override
    public void run(){
        LockAndCondition lc = new LockAndCondition(new ReentrantLock());
        serverThreadLockCacheProxy.put(groupId,lc);
        try{
            if(actionType == ActionType.APPROVESUBMIT){
                lc.sendAndWaitForSignal(groupId,actionType,ctx,message);
                //lc.sendAndWaitForSignalOnce(groupId,actionType,ctx,message);
            }else if(actionType == ActionType.APPROVESUBMIT_STRONG){
                lc.sendAndWaitForSignalIfFailSendMessage(groupId,actionType,ctx,message);
            }else{
                lc.sendAndWaitForSignalOnce(groupId,actionType,ctx,message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

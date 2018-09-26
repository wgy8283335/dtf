package com.coconason.dtf.manager.service;

import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.ThreadsInfo;
import com.coconason.dtf.manager.utils.LockAndCondition;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/9/26-17:02
 */
public class SendMessageRunnable implements Runnable {
    private String groupId;
    private ActionType actionType;
    private ChannelHandlerContext ctx;
    private String message;
    private ThreadsInfo threadsInfo;

    public SendMessageRunnable(String groupId, ActionType actionType, ChannelHandlerContext ctx, String message,ThreadsInfo threadsInfo) {
        this.groupId = groupId;
        this.actionType = actionType;
        this.ctx = ctx;
        this.message = message;
        this.threadsInfo = threadsInfo;
    }

    @Override
    public void run(){
        LockAndCondition lc = new LockAndCondition(new ReentrantLock());
        threadsInfo.put(groupId,lc);
        try{
            lc.sendAndWaitForSignal(groupId,actionType,ctx,message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

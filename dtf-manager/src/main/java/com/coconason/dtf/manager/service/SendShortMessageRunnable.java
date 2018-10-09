package com.coconason.dtf.manager.service;

import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.manager.utils.MessageSender;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: Jason
 * @date: 2018/9/27-14:29
 */
public final class SendShortMessageRunnable implements Runnable {
    private String groupId;
    private MessageProto.Message.ActionType actionType;
    private ChannelHandlerContext ctx;
    private Logger logger = LoggerFactory.getLogger(SendShortMessageRunnable.class);

    public SendShortMessageRunnable(String groupId, MessageProto.Message.ActionType actionType, ChannelHandlerContext ctx) {
        this.groupId = groupId;
        this.actionType = actionType;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        try{
            MessageSender.sendMsg(groupId, actionType,ctx);
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}

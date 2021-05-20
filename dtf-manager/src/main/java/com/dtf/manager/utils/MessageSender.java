package com.dtf.manager.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.dtf.common.utils.UuidGenerator;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message sender.
 * 
 * @Author: wangguangyuan
 */
public final class MessageSender {
    
    /**
     * Logger of MessageSender class.
     */
    private static Logger logger = LoggerFactory.getLogger(MessageSender.class);
    
    /**
     * Send message in channel of netty.
     * 
     * @param groupId group id
     * @param action action type
     * @param ctx channel handler context
     */
    public static void sendMsg(final String groupId, final ActionType action, final ChannelHandlerContext ctx) {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        JSONObject info = new JSONObject();
        info.put("groupId", groupId);
        builder.setInfo(info.toJSONString());
        builder.setId(UuidGenerator.generateUuid());
        builder.setAction(action);
        MessageProto.Message message = builder.build();
        logger.debug("Send transaction message:\n" + message);
        ctx.writeAndFlush(message);
    }
    
}

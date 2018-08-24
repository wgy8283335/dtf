package com.coconason.dtf.server.protobufserver;

import com.coconason.dtf.server.cache.MessageCache;
import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.server.message.TransactionMessageGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    @Autowired
    MessageCache messageCache;

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception
    {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        MessageProto.Message message = (MessageProto.Message) msg;
        ActionType actionType = message.getAction();
        System.out.println("Receive transaction message:\n" + message);
        switch (actionType){
            case ADD:
                //store the message in the cache.
                //check whether the group exits in the cache
                TransactionMessageGroup group = new TransactionMessageGroup(message);
                TransactionMessageGroup element = (TransactionMessageGroup)messageCache.get(group.getGroupId());
                if(element==null){
                    messageCache.put(group.getGroupId(),group);
                }else{
                    element.getMemberList().add(group.getMemberList().get(0));
                    element.getMemberSet().add(group.getMemberList().get(0).getGroupMemeberId());
                }
                break;
            case APPROVESUBMIT:
                //check the transaction in the cache.If success,return success message.

                //If fail, return failed message.

                //clear all messages of the transaction in the cache.

                break;
            default:
                ctx.fireChannelRead(msg);
        }
    }
}

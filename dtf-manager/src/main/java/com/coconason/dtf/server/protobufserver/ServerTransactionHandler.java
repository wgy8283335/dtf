package com.coconason.dtf.server.protobufserver;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.coconason.dtf.server.cache.MessageCache;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.server.message.TransactionMessageForSubmit;
import com.coconason.dtf.server.message.TransactionMessageGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Set;
import java.util.UUID;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    @Autowired
    MessageCache messageCache;

    private ChannelHandlerContext ctx;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception
    {
        this.ctx = ctx;
    }

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
            case APPLYFORSUBMIT:
                //check the transaction in the cache.If success,return success message.
                //If fail, return failed message.
                TransactionMessageForSubmit tmfs = new TransactionMessageForSubmit(message);
                Set setFromMessage =tmfs.getMemberSet();
                TransactionMessageGroup elementFromCache = (TransactionMessageGroup)messageCache.get(tmfs.getGroupId());
                Set setFromCache = elementFromCache.getMemberSet();
                //check whether the member from message has the same element as the member from cache.
                setFromMessage.removeAll(setFromCache);
                if(setFromMessage.isEmpty()){
                    //success
                    snedMsg(UuidGenerator.generateUuid(),ActionType.APPROVESUBMIT);
                }else{
                    //fail
                    snedMsg(UuidGenerator.generateUuid(),ActionType.CANCEL);
                }
                //Send response to other members of the group.Clear all messages of the transaction in the cache.

                messageCache.clear(tmfs.getGroupId());
                break;
            default:
                ctx.fireChannelRead(msg);
        }
    }

    private void snedMsg(String id,ActionType action) throws Exception{
        MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
        builder.setId(Integer.valueOf(id));
        builder.setAction(action);
        MessageProto.Message message = builder.build();
        System.out.println("Send transaction message:\n" + message);
        ctx.writeAndFlush(message);
    }
}

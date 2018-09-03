package com.coconason.dtf.demo2.protobufserver;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.coconason.dtf.demo2.cache.MessageCache;
import com.coconason.dtf.demo2.message.TransactionMessageForAdding;
import com.coconason.dtf.demo2.message.TransactionMessageForSubmit;
import com.coconason.dtf.demo2.message.TransactionMessageGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    MessageCache messageCache;

    private ChannelHandlerContext ctx;

    public ServerTransactionHandler(MessageCache messageCache) {
        this.messageCache = messageCache;
    }

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
        switch (actionType){
            case ADD:
                //store the message in the cache.
                //check whether the group exits in the cache
                TransactionMessageGroup group = new TransactionMessageGroup(message,ctx);
                messageCache.putDependsOnCondition(group);
                break;
            case APPLYFORSUBMIT:
                Thread thread = new Thread(new ApplyForRunnable(message));
                thread.start();
                break;
            default:
                ctx.fireChannelRead(msg);
        }
    }

    private void snedMsg(String groupId,ActionType action,ChannelHandlerContext ctx) throws Exception{
        MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
        JSONObject info = new JSONObject();
        info.put("groupId",groupId);
        builder.setInfo(info.toJSONString());
        builder.setId(UuidGenerator.generateUuid());
        builder.setAction(action);
        MessageProto.Message message = builder.build();
        System.out.println("Send transaction message:\n" + message);
        ctx.writeAndFlush(message);
    }

    class ApplyForRunnable implements Runnable{

        private MessageProto.Message message;

        public ApplyForRunnable(MessageProto.Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
                TransactionMessageForSubmit tmfs = new TransactionMessageForSubmit(message);
                Set setFromMessage =tmfs.getMemberSet();
                TransactionMessageGroup elementFromCache = (TransactionMessageGroup)messageCache.get(tmfs.getGroupId());
                Set setFromCache = elementFromCache.getMemberSet();
                List<TransactionMessageForAdding> memberList = elementFromCache.getMemberList();
                //check whether the member from message has the same element as the member from cache.
                setFromMessage.removeAll(setFromCache);
                if(setFromMessage.isEmpty()){
                    for (TransactionMessageForAdding messageForAdding: memberList) {
                        //success
                        snedMsg(elementFromCache.getGroupId(),ActionType.APPROVESUBMIT,messageForAdding.getCtx());
                    }
                }else{
                    for (TransactionMessageForAdding messageForAdding: memberList) {
                        //fail
                        snedMsg(elementFromCache.getGroupId(), ActionType.CANCEL,messageForAdding.getCtx());
                    }
                }
                //Send response to other members of the group.Clear all messages of the transaction in the cache.
                messageCache.clear(tmfs.getGroupId());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

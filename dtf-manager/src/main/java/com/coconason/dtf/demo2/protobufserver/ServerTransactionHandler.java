package com.coconason.dtf.demo2.protobufserver;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.coconason.dtf.demo2.cache.MessageAsyncCache;
import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.cache.MessageSyncCache;
import com.coconason.dtf.demo2.message.TransactionMessageForAdding;
import com.coconason.dtf.demo2.message.TransactionMessageForSubmit;
import com.coconason.dtf.demo2.message.TransactionMessageGroup;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import com.coconason.dtf.demo2.service.SendRunnable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    MessageSyncCache messageSyncCache;

    MessageAsyncCache messageAsyncCache;

    MessageAsyncQueue messageAsyncQueue;

    private ChannelHandlerContext ctx;

    public ServerTransactionHandler(MessageSyncCache messageSyncCache,MessageAsyncCache messageAsyncCache , MessageAsyncQueue messageAsyncQueue) {
        this.messageSyncCache = messageSyncCache;
        this.messageAsyncQueue = messageAsyncQueue;
        this.messageAsyncCache = messageAsyncCache;
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
                messageSyncCache.putDependsOnCondition(new TransactionMessageGroup(message,ctx));
                break;
            case APPLYFORSUBMIT:
                new Thread(new ApplyForRunnable(message,ActionType.APPROVESUBMIT,ctx)).start();
                break;
            case ADD_STRONG:
                //store the message in the cache.
                //check whether the group exits in the cache
                messageSyncCache.putDependsOnCondition(new TransactionMessageGroup(message,ctx));
                break;
            case APPLYFORSUBMIT_STRONG:
                new Thread(new ApplyForRunnable(message,ActionType.APPROVESUBMIT_STRONG,ctx)).start();
                break;
            case CANCEL:
                new Thread(new ApplyForRunnable(message,ActionType.CANCEL,ctx)).start();
            case SUB_SUCCESS_STRONG:
                String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
                TransactionMessageGroup groupTemp = (TransactionMessageGroup) messageSyncCache.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
                //1.check the group.If all of members are success,reply to the creator.
                List<TransactionMessageForAdding> memberList = groupTemp.getMemberList();
                for(TransactionMessageForAdding member:memberList){
                    if(memberId.equals(member.getGroupMemberId())){
                        member.setCommited(true);
                    }
                }
                boolean flag = memberList==null ? false:true;
                for(TransactionMessageForAdding member:memberList){
                    if(!member.isCommited()){
                        flag = false;
                        break;
                    }
                }
                if(flag == true){
                    snedMsg(groupTemp.getGroupId(),ActionType.WHOLE_SUCCESS_STRONG,groupTemp.getCtxForSubmitting());
                    messageSyncCache.clear(groupTemp.getGroupId());
                }
                break;
            case SUB_FAIL_STRONG:
                TransactionMessageGroup groupTemp1 = (TransactionMessageGroup) messageSyncCache.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
                snedMsg(groupTemp1.getGroupId(),ActionType.WHOLE_FAIL_STRONG,groupTemp1.getCtxForSubmitting());
                messageSyncCache.clear(groupTemp1.getGroupId());
                break;
            case ADD_ASYNC:
                TransactionMessageGroupAsync transactionMessageGroupAsync=null;
                try{
                    transactionMessageGroupAsync = TransactionMessageGroupAsync.parse(message);
                    messageAsyncCache.putDependsOnConditionAsync(transactionMessageGroupAsync);
                    snedMsg(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_SUCCESS_ASYNC,ctx);
                }catch (Exception e) {
                    snedMsg(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_FAIL_ASYNC,ctx);
                }
                break;
            case ASYNC_COMMIT:
                JSONObject map = JSONObject.parseObject(message.getInfo());
                String groupId = map.get("groupId").toString();
                Thread thread = new Thread(new SendRunnable(messageAsyncCache,groupId,messageAsyncQueue));
                thread.start();
                break;
            default:
                break;
        }
        ctx.fireChannelRead(msg);
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

        private ActionType actionType;

        private ChannelHandlerContext ctx;

        public ApplyForRunnable(MessageProto.Message message, ActionType actionType, ChannelHandlerContext ctx) {
            this.message = message;
            this.actionType = actionType;
            this.ctx = ctx;
        }

        @Override
        public void run() {
            try{
                Thread.sleep(1000);
                TransactionMessageForSubmit tmfs = new TransactionMessageForSubmit(message);
                Set setFromMessage =tmfs.getMemberSet();
                TransactionMessageGroup elementFromCache = (TransactionMessageGroup) messageSyncCache.get(tmfs.getGroupId());
                Set setFromCache = elementFromCache.getMemberSet();
                elementFromCache.setCtxForSubmitting(ctx);
                messageSyncCache.put(elementFromCache.getGroupId(),elementFromCache);
                List<TransactionMessageForAdding> memberList = elementFromCache.getMemberList();
                //check whether the member from message has the same element as the member from cache.
                if(!setFromMessage.isEmpty()){
                    setFromMessage.removeAll(setFromCache);
                    if(setFromMessage.isEmpty()){
                        for (TransactionMessageForAdding messageForAdding: memberList) {
                            //success
                            snedMsg(elementFromCache.getGroupId()+messageForAdding.getGroupMemberId(),actionType,messageForAdding.getCtx());
                        }
                    }else{
                        for (TransactionMessageForAdding messageForAdding: memberList) {
                            //fail
                            snedMsg(elementFromCache.getGroupId()+messageForAdding.getGroupMemberId(), ActionType.CANCEL,messageForAdding.getCtx());
                        }
                    }
                    if(actionType == ActionType.APPROVESUBMIT){
                        //Send response to other members of the group.Clear all messages of the transaction in the cache.
                        messageSyncCache.clear(tmfs.getGroupId());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

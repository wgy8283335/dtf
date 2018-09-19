package com.coconason.dtf.demo2.protobufserver;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.demo2.cache.MessageAsyncCache;
import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.cache.MessageForSubmitSyncCache;
import com.coconason.dtf.demo2.cache.MessageSyncCache;
import com.coconason.dtf.demo2.message.TransactionMessageForAdding;
import com.coconason.dtf.demo2.message.TransactionMessageForSubmit;
import com.coconason.dtf.demo2.message.TransactionMessageGroup;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import com.coconason.dtf.demo2.service.ApplyForRunnable;
import com.coconason.dtf.demo2.utils.MessageSender;
import com.coconason.dtf.demo2.service.SendRunnable;
import com.coconason.dtf.demo2.threadpools.ThreadPoolForServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    MessageSyncCache messageSyncCache;

    MessageForSubmitSyncCache messageForSubmitSyncCache;

    MessageAsyncCache messageAsyncCache;

    MessageAsyncQueue messageAsyncQueue;

    private ChannelHandlerContext ctx;

    private ThreadPoolForServer threadPoolForServer;

    public ServerTransactionHandler(MessageSyncCache messageSyncCache,MessageAsyncCache messageAsyncCache , MessageAsyncQueue messageAsyncQueue, ThreadPoolForServer threadPoolForServer,MessageForSubmitSyncCache messageForSubmitSyncCache) {
        this.messageSyncCache = messageSyncCache;
        this.messageAsyncQueue = messageAsyncQueue;
        this.messageAsyncCache = messageAsyncCache;
        this.threadPoolForServer = threadPoolForServer;
        this.messageForSubmitSyncCache = messageForSubmitSyncCache;
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
                threadPoolForServer.addTask(new ApplyForRunnable(message,ActionType.ADD,ctx, messageForSubmitSyncCache, messageSyncCache));
                break;
            case APPLYFORSUBMIT:
                messageForSubmitSyncCache.put( new TransactionMessageForSubmit(message));
                threadPoolForServer.addTask(new ApplyForRunnable(message,ActionType.APPROVESUBMIT,ctx, messageForSubmitSyncCache, messageSyncCache));
                break;
            case ADD_STRONG:
                //store the message in the cache.
                //check whether the group exits in the cache
                messageSyncCache.putDependsOnCondition(new TransactionMessageGroup(message,ctx));
                threadPoolForServer.addTask(new ApplyForRunnable(message,ActionType.ADD_STRONG,ctx, messageForSubmitSyncCache, messageSyncCache));
                break;
            case APPLYFORSUBMIT_STRONG:
                messageForSubmitSyncCache.put( new TransactionMessageForSubmit(message));
                threadPoolForServer.addTask(new ApplyForRunnable(message,ActionType.APPROVESUBMIT_STRONG,ctx, messageForSubmitSyncCache, messageSyncCache));
                break;
            case CANCEL:
                threadPoolForServer.addTask(new ApplyForRunnable(message,ActionType.CANCEL,ctx, messageForSubmitSyncCache, messageSyncCache));
            case SUB_SUCCESS_STRONG:
                String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
                TransactionMessageGroup groupTemp = messageSyncCache.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
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
                    MessageSender.snedMsg(groupTemp.getGroupId(),ActionType.WHOLE_SUCCESS_STRONG,groupTemp.getCtxForSubmitting());
                    messageSyncCache.clear(groupTemp.getGroupId());
                }
                break;
            case SUB_FAIL_STRONG:
                TransactionMessageGroup groupTemp1 = messageSyncCache.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
                MessageSender.snedMsg(groupTemp1.getGroupId(),ActionType.WHOLE_FAIL_STRONG,groupTemp1.getCtxForSubmitting());
                messageSyncCache.clear(groupTemp1.getGroupId());
                break;
            case ADD_ASYNC:
                TransactionMessageGroupAsync transactionMessageGroupAsync=null;
                try{
                    transactionMessageGroupAsync = TransactionMessageGroupAsync.parse(message);
                    messageAsyncCache.putDependsOnConditionAsync(transactionMessageGroupAsync);
                    MessageSender.snedMsg(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_SUCCESS_ASYNC,ctx);
                }catch (Exception e) {
                    MessageSender.snedMsg(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_FAIL_ASYNC,ctx);
                }
                break;
            case ASYNC_COMMIT:
                JSONObject map = JSONObject.parseObject(message.getInfo());
                String groupId = map.get("groupId").toString();
                threadPoolForServer.addTask(new SendRunnable(messageAsyncCache,groupId,messageAsyncQueue));
                break;
            default:
                break;
        }
        ctx.fireChannelRead(msg);
    }

//    class ApplyForRunnable implements Runnable{
//
//        private MessageProto.Message message;
//
//        private ActionType actionType;
//
//        private ChannelHandlerContext ctx;
//
//        public ApplyForRunnable(MessageProto.Message message, ActionType actionType, ChannelHandlerContext ctx) {
//            this.message = message;
//            this.actionType = actionType;
//            this.ctx = ctx;
//        }
//
//        @Override
//        public void run() {
//            try{
//                JSONObject info = JSONObject.parseObject(message.getInfo());
//                String groupId = info.get("groupId").toString();
//                Object obj = info.get("groupMemberSet");
//                TransactionMessageForSubmit tmfs = obj == null ? messageForSubmitSyncCache.get(groupId):new TransactionMessageForSubmit(message);
//                if(tmfs == null||tmfs.getMemberSet().isEmpty()||messageSyncCache.get(tmfs.getGroupId())==null){
//                    return;
//                }
//                Set setFromMessage =tmfs.getMemberSet();
//                TransactionMessageGroup elementFromCache = messageSyncCache.get(tmfs.getGroupId());
//                Set setFromCache = elementFromCache.getMemberSet();
//                elementFromCache.setCtxForSubmitting(ctx);
//                messageSyncCache.put(elementFromCache.getGroupId(),elementFromCache);
//                List<TransactionMessageForAdding> memberList = elementFromCache.getMemberList();
//                //check whether the member from message has the same element as the member from cache.
//                if(!setFromMessage.isEmpty()){
//                    if(SetUtil.isSetEqual(setFromMessage,setFromCache)){
//                        for (TransactionMessageForAdding messageForAdding: memberList) {
//                            //success
//                            snedMsg(elementFromCache.getGroupId()+messageForAdding.getGroupMemberId(),ActionType.APPROVESUBMIT_STRONG,messageForAdding.getCtx());
//                        }
//                        if(actionType == ActionType.ADD || actionType == ActionType.APPROVESUBMIT){
//                            messageSyncCache.clear(tmfs.getGroupId());
//                        }
//                    }
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void snedMsg(String groupId,ActionType action,ChannelHandlerContext ctx) throws Exception{
//        MessageProto.Message.Builder builder= MessageProto.Message.newBuilder();
//        JSONObject info = new JSONObject();
//        info.put("groupId",groupId);
//        builder.setInfo(info.toJSONString());
//        builder.setId(UuidGenerator.generateUuid());
//        builder.setAction(action);
//        MessageProto.Message message = builder.build();
//        System.out.println("Send transaction message:\n" + message);
//        ctx.writeAndFlush(message);
//    }
}

package com.coconason.dtf.demo2.protobufserver;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.demo2.cache.*;
import com.coconason.dtf.demo2.message.TransactionMessageForAdding;
import com.coconason.dtf.demo2.message.TransactionMessageForSubmit;
import com.coconason.dtf.demo2.message.TransactionMessageGroup;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import com.coconason.dtf.demo2.service.ApplyForRunnable;
import com.coconason.dtf.demo2.service.SendRunnable;
import com.coconason.dtf.demo2.threadpools.ThreadPoolForServer;
import com.coconason.dtf.demo2.utils.MessageSender;
import com.coconason.dtf.demo2.utils.SetUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    private MessageSyncCache messageSyncCache;

    private MessageForSubmitSyncCache messageForSubmitSyncCache;

    private MessageAsyncCache messageAsyncCache;

    private MessageAsyncQueue messageAsyncQueue;

    private ChannelHandlerContext ctx;

    private ThreadPoolForServer threadPoolForServer;

    private MessageForSubmitAsyncCache messageForSubmitAsyncCache;

    public ServerTransactionHandler(MessageSyncCache messageSyncCache,MessageAsyncCache messageAsyncCache ,
                                    MessageAsyncQueue messageAsyncQueue, ThreadPoolForServer threadPoolForServer,
                                    MessageForSubmitSyncCache messageForSubmitSyncCache,MessageForSubmitAsyncCache messageForSubmitAsyncCache) {
        this.messageSyncCache = messageSyncCache;
        this.messageAsyncQueue = messageAsyncQueue;
        this.messageAsyncCache = messageAsyncCache;
        this.threadPoolForServer = threadPoolForServer;
        this.messageForSubmitSyncCache = messageForSubmitSyncCache;
        this.messageForSubmitAsyncCache = messageForSubmitAsyncCache;
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
                    //add message in messageAsyncCache
                    messageAsyncCache.putDependsOnConditionAsync(transactionMessageGroupAsync);
                    MessageSender.snedMsg(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_SUCCESS_ASYNC,ctx);
                }catch (Exception e) {
                    MessageSender.snedMsg(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_FAIL_ASYNC,ctx);
                }
                Set<String> setFromCacheTemp = SetUtil.setTransfer(messageAsyncCache.get(transactionMessageGroupAsync.getGroupId()).getMemberSet());
                TransactionMessageForSubmit transactionMessageForSubmit1= messageForSubmitAsyncCache.get(transactionMessageGroupAsync.getGroupId());
                if(transactionMessageForSubmit1!=null){
                    Set<String> setFromMessageTemp = transactionMessageForSubmit1.getMemberSet();
                    setFromMessageTemp.remove("1");
                    if(setFromMessageTemp!=null&&SetUtil.isSetEqual(setFromCacheTemp,setFromMessageTemp)){
                        threadPoolForServer.addTask(new SendRunnable(messageAsyncCache,transactionMessageForSubmit1,messageAsyncQueue));
                    }
                }
                break;
            case ASYNC_COMMIT:
                JSONObject map = JSONObject.parseObject(message.getInfo());
                String groupId = map.get("groupId").toString();
                TransactionMessageForSubmit transactionMessageForSubmit = new TransactionMessageForSubmit(message);
                messageForSubmitAsyncCache.put(transactionMessageForSubmit);
                TransactionMessageGroupAsync transactionMessageGroupAsync1 = messageAsyncCache.get(groupId);
                if(transactionMessageGroupAsync1!=null){
                    Set<String> setFromCache = SetUtil.setTransfer(transactionMessageGroupAsync1.getMemberSet());
                    Set<String> setFromMessage = transactionMessageForSubmit.getMemberSet();
                    setFromMessage.remove("1");
                    if(SetUtil.isSetEqual(setFromCache,setFromMessage)){
                        threadPoolForServer.addTask(new SendRunnable(messageAsyncCache,transactionMessageForSubmit,messageAsyncQueue));
                    }
                }
                break;
            default:
                break;
        }
        ctx.fireChannelRead(msg);
    }
}

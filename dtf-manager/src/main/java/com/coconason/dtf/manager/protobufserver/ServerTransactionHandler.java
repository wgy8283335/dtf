package com.coconason.dtf.manager.protobufserver;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.manager.cache.*;
import com.coconason.dtf.manager.message.*;
import com.coconason.dtf.manager.service.CheckAndSubmitRunnable;
import com.coconason.dtf.manager.service.SendAsyncRequestRunnable;
import com.coconason.dtf.manager.service.SendMessageRunnable;
import com.coconason.dtf.manager.service.SendShortMessageRunnable;
import com.coconason.dtf.manager.thread.LockAndConditionInterface;
import com.coconason.dtf.manager.thread.ServerThreadLockCacheProxy;
import com.coconason.dtf.manager.threadpools.ThreadPoolForServer;
import com.coconason.dtf.manager.utils.SetUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/7/30-9:31
 */
public class ServerTransactionHandler extends ChannelInboundHandlerAdapter{

    private MessageSyncCacheProxy messageSyncCacheProxy;

    private MessageForSubmitSyncCacheProxy messageForSubmitSyncCacheProxy;

    private MessageAsyncCacheProxy messageAsyncCacheProxy;

    private MessageAsyncQueueProxy messageAsyncQueueProxy;

    private ChannelHandlerContext ctx;

    private ThreadPoolForServer threadPoolForServer;

    private MessageForSubmitAsyncCacheProxy messageForSubmitAsyncCacheProxy;

    private ServerThreadLockCacheProxy serverThreadLockCacheProxy;

    private static final Logger logger = LoggerFactory.getLogger(ServerTransactionHandler.class);

    public ServerTransactionHandler(MessageSyncCacheProxy messageSyncCacheProxy, MessageAsyncCacheProxy messageAsyncCacheProxy,
                                    MessageAsyncQueueProxy messageAsyncQueueProxy, ThreadPoolForServer threadPoolForServer,
                                    MessageForSubmitSyncCacheProxy messageForSubmitSyncCacheProxy, MessageForSubmitAsyncCacheProxy messageForSubmitAsyncCacheProxy,
                                    ServerThreadLockCacheProxy serverThreadLockCacheProxy) {
        this.messageSyncCacheProxy = messageSyncCacheProxy;
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
        this.messageAsyncCacheProxy = messageAsyncCacheProxy;
        this.threadPoolForServer = threadPoolForServer;
        this.messageForSubmitSyncCacheProxy = messageForSubmitSyncCacheProxy;
        this.messageForSubmitAsyncCacheProxy = messageForSubmitAsyncCacheProxy;
        this.serverThreadLockCacheProxy = serverThreadLockCacheProxy;
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
        System.out.println(msg);
        MessageProto.Message message = (MessageProto.Message) msg;
        ActionType actionType = message.getAction();
        switch (actionType){
            case ADD:
                //store the message in the cache.
                //check whether the group exits in the cache
                messageSyncCacheProxy.putDependsOnCondition(new TransactionMessageGroup(message,ctx));
                threadPoolForServer.execute(new CheckAndSubmitRunnable(message,ActionType.ADD,ctx, messageForSubmitSyncCacheProxy, messageSyncCacheProxy, serverThreadLockCacheProxy,threadPoolForServer));
                break;
            case APPLYFORSUBMIT:
                TransactionMessageForSubmit transactionMessageForSubmit = new TransactionMessageForSubmit(message);
                messageForSubmitSyncCacheProxy.put( transactionMessageForSubmit.getGroupId(),transactionMessageForSubmit);
                threadPoolForServer.execute(new CheckAndSubmitRunnable(message,ActionType.APPROVESUBMIT,ctx, messageForSubmitSyncCacheProxy, messageSyncCacheProxy, serverThreadLockCacheProxy,threadPoolForServer));
                break;
            case ADD_STRONG:
                //store the message in the cache.
                //check whether the group exits in the cache
                messageSyncCacheProxy.putDependsOnCondition(new TransactionMessageGroup(message,ctx));
                threadPoolForServer.execute(new CheckAndSubmitRunnable(message,ActionType.ADD_STRONG,ctx, messageForSubmitSyncCacheProxy, messageSyncCacheProxy, serverThreadLockCacheProxy,threadPoolForServer));
                break;
            case APPLYFORSUBMIT_STRONG:
                TransactionMessageForSubmit transactionMessageForSubmitTemp1 = new TransactionMessageForSubmit(message);
                messageForSubmitSyncCacheProxy.put( transactionMessageForSubmitTemp1.getGroupId(),transactionMessageForSubmitTemp1);
                threadPoolForServer.execute(new CheckAndSubmitRunnable(message,ActionType.APPROVESUBMIT_STRONG,ctx, messageForSubmitSyncCacheProxy, messageSyncCacheProxy, serverThreadLockCacheProxy,threadPoolForServer));
                break;
            case CANCEL:
                threadPoolForServer.execute(new CheckAndSubmitRunnable(message,ActionType.CANCEL,ctx, messageForSubmitSyncCacheProxy, messageSyncCacheProxy, serverThreadLockCacheProxy,threadPoolForServer));
                break;
            case SUB_SUCCESS_STRONG:
                String memberId = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
                TransactionMessageGroupInterface groupTemp = messageSyncCacheProxy.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
                LockAndConditionInterface lc1 = serverThreadLockCacheProxy.getIfPresent(groupTemp.getGroupId()+memberId);
                lc1.signal();
                //1.check the group.If all of members are success,reply to the creator.
                List<TransactionMessageForAdding> memberList = groupTemp.getMemberList();
                for(TransactionMessageForAdding member:memberList){
                    if(memberId.equals(member.getGroupMemberId())){
                        member.setCommitted(true);
                    }
                }
                boolean flag = memberList==null ? false:true;
                for(TransactionMessageForAdding member:memberList){
                    if(!member.isCommitted()){
                        flag = false;
                        break;
                    }
                }
                if(flag == true){
                    String groupId = groupTemp.getGroupId();
                    threadPoolForServer.execute(new SendMessageRunnable(groupId,ActionType.WHOLE_SUCCESS_STRONG,groupTemp.getCtx(),"send WHOLE_SUCCESS_STRONG message fail", serverThreadLockCacheProxy));
                    messageSyncCacheProxy.invalidate(groupId);
                }
                break;
            case SUB_SUCCESS:
                String memberId2 = JSONObject.parseObject(message.getInfo()).get("memberId").toString();
                String groupTempId2 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
                LockAndConditionInterface lc2 = serverThreadLockCacheProxy.getIfPresent(groupTempId2+memberId2);
                lc2.signal();
                break;
            case SUB_FAIL:
                String groupTempId3 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
                TransactionMessageGroupInterface groupInfoToLog = messageSyncCacheProxy.get(groupTempId3);
                logger.error("SUB FAIL :"+groupInfoToLog.toString());
                messageSyncCacheProxy.invalidate(groupTempId3);
                break;
            case WHOLE_SUCCESS_STRONG_ACK:
                String tempGroupId1 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
                LockAndConditionInterface tempLc1 = serverThreadLockCacheProxy.getIfPresent(tempGroupId1);
                tempLc1.signal();
                break;
            case SUB_FAIL_STRONG:
                TransactionMessageGroupInterface groupTemp1 = messageSyncCacheProxy.get(JSONObject.parseObject(message.getInfo()).get("groupId").toString());
                String groupId1 = groupTemp1.getGroupId();
                threadPoolForServer.execute(new SendMessageRunnable(groupId1,ActionType.WHOLE_FAIL_STRONG,groupTemp1.getCtx(),"send WHOLE_FAIL_STRONG message fail", serverThreadLockCacheProxy));
                messageSyncCacheProxy.invalidate(groupTemp1.getGroupId());
                break;
            case WHOLE_FAIL_STRONG_ACK:
                String tempGroupId2 = JSONObject.parseObject(message.getInfo()).get("groupId").toString();
                LockAndConditionInterface tempLc2 = serverThreadLockCacheProxy.getIfPresent(tempGroupId2);
                tempLc2.signal();
                break;
            case ADD_ASYNC:
                TransactionMessageGroupAsync transactionMessageGroupAsync=null;
                try{
                    transactionMessageGroupAsync = TransactionMessageGroupAsync.parse(message);
                    //add message in messageAsyncCacheProxy
                    messageAsyncCacheProxy.putDependsOnCondition(transactionMessageGroupAsync);
                    threadPoolForServer.execute(new SendShortMessageRunnable(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_SUCCESS_ASYNC,ctx));
                }catch (Exception e) {
                    threadPoolForServer.execute(new SendShortMessageRunnable(transactionMessageGroupAsync.getGroupId(),ActionType.ADD_FAIL_ASYNC,ctx));
                }
                Set<String> setFromCacheTemp = SetUtil.setTransfer(messageAsyncCacheProxy.get(transactionMessageGroupAsync.getGroupId()).getMemberSet());
                TransactionMessageGroupInterface transactionMessageForSubmit1= messageForSubmitAsyncCacheProxy.get(transactionMessageGroupAsync.getGroupId());
                if (transactionMessageForSubmit1 != null) {
                    Set<String> setFromMessageTemp = transactionMessageForSubmit1.getMemberSet();
                    setFromMessageTemp.remove("1");
                    if (setFromMessageTemp != null && SetUtil.isSetEqual(setFromCacheTemp, setFromMessageTemp)) {
                        threadPoolForServer.execute(new SendAsyncRequestRunnable(messageAsyncCacheProxy, transactionMessageForSubmit1, messageAsyncQueueProxy));
                    }
                }
                break;
            case ASYNC_COMMIT:
                JSONObject map = JSONObject.parseObject(message.getInfo());
                String groupId = map.get("groupId").toString();
                TransactionMessageForSubmit transactionMessageForSubmitTemp = new TransactionMessageForSubmit(message);
                messageForSubmitAsyncCacheProxy.put(transactionMessageForSubmitTemp.getGroupId(),transactionMessageForSubmitTemp);
                threadPoolForServer.execute(new SendShortMessageRunnable(groupId,ActionType.COMMIT_SUCCESS_ASYNC,ctx));
                TransactionMessageGroupInterface transactionMessageGroupAsync1 = messageAsyncCacheProxy.get(groupId);
                if(transactionMessageGroupAsync1!=null){
                    Set<String> setFromCache = SetUtil.setTransfer(transactionMessageGroupAsync1.getMemberSet());
                    Set<String> setFromMessage = transactionMessageForSubmitTemp.getMemberSet();
                    setFromMessage.remove("1");
                    if(SetUtil.isSetEqual(setFromCache,setFromMessage)){
                        threadPoolForServer.execute(new SendAsyncRequestRunnable(messageAsyncCacheProxy,transactionMessageForSubmitTemp, messageAsyncQueueProxy));
                    }
                }
                break;
            default:
                break;
        }
        ctx.fireChannelRead(msg);
    }
}

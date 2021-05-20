package com.dtf.manager.service;

import com.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.dtf.manager.cache.MessageCacheInterface;
import com.dtf.manager.log.LogUtil;
import com.dtf.manager.message.MessageInfoInterface;
import com.dtf.manager.message.TransactionMessageGroupInterface;
import com.dtf.manager.utils.HttpClientUtil;
import com.dtf.manager.utils.MessageSender;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.Set;

/**
 * Send asynchronous request.
 * 
 * @author wangguangyuan
 */
public final class SendAsyncRequestRunnable implements Runnable {

    /**
     * Logger of SendAsyncRequestRunnable class.
     */
    private final Logger logger = LoggerFactory.getLogger(SendAsyncRequestRunnable.class);
    
    /**
     * Cache for message.
     */
    private MessageCacheInterface messageAsyncCacheProxy;
    
    /**
     * Transaction message for submit.
     */
    private TransactionMessageGroupInterface transactionMessageForSubmit;
    
    /**
     * Queue of asynchronous message.
     */
    private Queue messageAsyncQueueProxy;
    
    /**
     * Group id.
     */
    private String groupId;

    /**
     * Action type.
     */
    private ActionType actionType;

    /**
     * Channel handler context.
     */
    private ChannelHandlerContext ctx;
    
    public SendAsyncRequestRunnable(final MessageCacheInterface messageAsyncCacheProxy, final TransactionMessageGroupInterface transactionMessageForSubmit, 
                                    final Queue messageAsyncQueueProxy, final String groupId, final ActionType actionType, final ChannelHandlerContext ctx) {
        this.messageAsyncCacheProxy = messageAsyncCacheProxy;
        this.transactionMessageForSubmit = transactionMessageForSubmit;
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
        this.groupId = groupId;
        this.actionType = actionType;
        this.ctx = ctx;
    }
    
    /**
     * Get transactionMessageGroupAsync from the messageAsyncCacheProxy, and send request.
     * If request fail, put the request in the queue. There will be a consumer to consume the queue to send request.
     */
    @Override
    public void run() {
        TransactionMessageGroupInterface theMessageGroupAsync = messageAsyncCacheProxy.get(transactionMessageForSubmit.getGroupId());
        Set<MessageInfoInterface> theMemberSet = theMessageGroupAsync.getMemberSet();
        for (MessageInfoInterface messageInfo : theMemberSet) {
            int position = LogUtil.getInstance().append(messageInfo);
            if (position < 0) {
                //???可能需要考虑抛出一个信号终止server的运行。
                logger.error("Record in async-request.bin failure" + messageInfo.toString());
                continue;
            }
            messageInfo.setPosition(position);
            messageInfo.setCommitted(false);
            LogUtil.getInstance().updateCommitStatus(messageInfo);
        }
        MessageSender.sendMsg(groupId, actionType, ctx);
        for (MessageInfoInterface messageInfo : theMemberSet) {
            String url = messageInfo.getUrl();
            String obj = messageInfo.getObj().toString();
            String result = sendByHttpAction(url, obj, null, "post");
            if ("".equals(result)) {
                messageAsyncQueueProxy.add(messageInfo);
            } else {
                messageInfo.setCommitted(true);
                LogUtil.getInstance().updateCommitStatus(messageInfo);
            }
        }
        messageAsyncCacheProxy.invalidate(transactionMessageForSubmit.getGroupId());
    }
    
    private String sendByHttpAction(String url, String request, Object uriVariables, String httpAction) {
        String result = null;
        switch (httpAction) {
            case "post" :
                result = HttpClientUtil.doPostJson(url, request, transactionMessageForSubmit.getGroupId());
                break;
            case "get" :
                result = HttpClientUtil.doGet(url, transactionMessageForSubmit.getGroupId());
                break;
            case "put" :
                result = HttpClientUtil.doPutJson(url, request, transactionMessageForSubmit.getGroupId());
                break;
            case "delete" :
                result = HttpClientUtil.doDelete(url, transactionMessageForSubmit.getGroupId());
                break;
        }
        return  result;
    }
    
}

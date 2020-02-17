package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.utils.HttpClientUtil;

import java.util.Queue;
import java.util.Set;

/**
 * Send asynchronous request.
 * 
 * @Author: Jason
 */
public final class SendAsyncRequestRunnable implements Runnable {
    
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
    
    public SendAsyncRequestRunnable(final MessageCacheInterface messageAsyncCacheProxy, final TransactionMessageGroupInterface transactionMessageForSubmit, final Queue messageAsyncQueueProxy) {
        this.messageAsyncCacheProxy = messageAsyncCacheProxy;
        this.transactionMessageForSubmit = transactionMessageForSubmit;
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
    }
    
    /**
     * Get transactionMessageGroupAsync from the messageAsyncCacheProxy, and send request.
     */
    @Override
    public void run() {
        TransactionMessageGroupInterface theMessageGroupAsync = messageAsyncCacheProxy.get(transactionMessageForSubmit.getGroupId());
        Set<MessageInfoInterface> theMemberSet = theMessageGroupAsync.getMemberSet();
        for (MessageInfoInterface messageInfo : theMemberSet) {
            String url = messageInfo.getUrl();
            String obj = messageInfo.getObj().toString();
            String result = HttpClientUtil.doPostJson(url, obj, transactionMessageForSubmit.getGroupId());
            if ("".equals(result)) {
                messageAsyncQueueProxy.add(messageInfo);
            } else {
                messageInfo.setCommitted(true);
            }
        }
        messageAsyncCacheProxy.invalidate(transactionMessageForSubmit.getGroupId());
    }
}

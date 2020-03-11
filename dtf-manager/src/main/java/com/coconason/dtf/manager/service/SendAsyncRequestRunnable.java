package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.utils.HttpClientUtil;
import com.coconason.dtf.manager.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.Set;

/**
 * Send asynchronous request.
 * 
 * @Author: Jason
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
    
    public SendAsyncRequestRunnable(final MessageCacheInterface messageAsyncCacheProxy, final TransactionMessageGroupInterface transactionMessageForSubmit, final Queue messageAsyncQueueProxy) {
        this.messageAsyncCacheProxy = messageAsyncCacheProxy;
        this.transactionMessageForSubmit = transactionMessageForSubmit;
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
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
                logger.error("Record in async-request.log failure" + messageInfo.toString());
                continue;
            }
            String url = messageInfo.getUrl();
            String obj = messageInfo.getObj().toString();
            messageInfo.setPosition(position);
            String result = HttpClientUtil.doPostJson(url, obj, transactionMessageForSubmit.getGroupId());
            if ("".equals(result)) {
                messageAsyncQueueProxy.add(messageInfo);
            } else {
                messageInfo.setCommitted(true);
                LogUtil.getInstance().updateCommitStatus(messageInfo);
            }
        }
        messageAsyncCacheProxy.invalidate(transactionMessageForSubmit.getGroupId());
    }
    
}

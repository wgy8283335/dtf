package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.utils.HttpClientUtil;
import com.coconason.dtf.manager.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Consume failure asynchronous request.
 * 
 * @Author: Jason
 */
public final class ConsumerFailingAsyncRequestRunnable implements Runnable {
    
    /**
     * Logger of ConsumerFailingAsyncRequestRunnable class.
     */
    private final Logger logger = LoggerFactory.getLogger(ConsumerFailingAsyncRequestRunnable.class);
    
    private Queue messageAsyncQueueProxy;
    
    public ConsumerFailingAsyncRequestRunnable(final MessageAsyncQueueProxy messageAsyncQueueProxy) {
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
    }
    
    /**
     * Consume the queue and send request. 
     */
    @Override
    public void run() {
        MessageInfoInterface messageInfo = null;
        while (true) {
            if (messageAsyncQueueProxy != null) {
                messageInfo = (MessageInfoInterface) messageAsyncQueueProxy.poll();
            }
            if (messageInfo == null) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                continue;
            }
            if (!messageInfo.isCommitted()) {
                String url = messageInfo.getUrl();
                String obj = messageInfo.getObj().toString();
                String result = HttpClientUtil.doPostJson(url, obj, "");
                if ("".equals(result)) {
                    messageInfo.setCommitted(false);
//                    messageAsyncQueueProxy.add(messageInfo);
                    logger.error(messageInfo.toString());
                } else {
                    messageInfo.setCommitted(true);
                    LogUtil.getInstance().updateCommitStatus(messageInfo);
                }
            }
            messageInfo = null;
            continue;
        }
    }
    
}

package com.dtf.manager.service;

import com.dtf.manager.cache.MessageAsyncQueueProxy;
import com.dtf.manager.log.LogUtil;
import com.dtf.manager.message.MessageInfoInterface;
import com.dtf.manager.utils.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Consume failure asynchronous request.
 * 
 * @Author: wangguangyuan
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
                String httpAction = messageInfo.getHttpAction();
                String result = sendByHttpAction(url, obj, "", httpAction);
                if ("".equals(result)) {
                    messageInfo.setCommitted(false);
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
    
    private String sendByHttpAction(String url, String request, String groupId, String httpAction) {
        String result = null;
        switch (httpAction) {
            case "post" :
                result = HttpClientUtil.doPostJson(url, request, groupId);
                break;
            case "get" :
                result = HttpClientUtil.doGet(url, groupId);
                break;
            case "put" :
                result = HttpClientUtil.doPutJson(url, request, groupId);
                break;
            case "delete" :
                result = HttpClientUtil.doDelete(url, groupId);
                break;
        }
        return  result;
    }
    
}

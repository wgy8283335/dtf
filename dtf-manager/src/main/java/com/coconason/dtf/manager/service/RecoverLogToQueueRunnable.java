package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.log.LogUtil;
import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Read log and send the uncommitted message to queue. 
 * 
 * @Author: Jason
 */
public final class RecoverLogToQueueRunnable implements Runnable {
    
    /**
     * Logger of RecoverLogToQueueRunnable class.
     */
    private final Logger logger = LoggerFactory.getLogger(RecoverLogToQueueRunnable.class);
    
    private Queue messageAsyncQueueProxy;
    
    public RecoverLogToQueueRunnable(final MessageAsyncQueueProxy messageAsyncQueueProxy) {
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
    }
    
    /**
     * Consume the queue and send request. 
     */
    @Override
    public void run() {
        int i = 0;
        long length = LogUtil.getInstance().getInitialLength();
        List<MessageInfoInterface> messageInfoList = new ArrayList<>();
        while (i < length) {
            MessageInfoInterface message = LogUtil.getInstance().get(i);
            i = i + 90;
            if (null == message) {
                break;
            }
            if (message.isCommitted()) {
                continue;
            }
            messageInfoList.add(message);
        }
        for (MessageInfoInterface message : messageInfoList) {
            messageAsyncQueueProxy.add(message);
        }
    }
    
}

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
        int position = LogUtil.getInstance().initializeMetadataPosition();
        List<MessageInfoInterface> messageInfoList = LogUtil.getInstance().goThrough(position);
        for (MessageInfoInterface message : messageInfoList) {
            messageAsyncQueueProxy.add(message);
        }
    }
    
}

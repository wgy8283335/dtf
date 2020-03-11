package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        long length = LogUtil.getInstance().getLength();
        while (i < length) {
            MessageInfoInterface message = LogUtil.getInstance().get(i);
            if (null == message) {
                break;
            }
            if (message.isCommitted()) {
                continue;
            }
            messageAsyncQueueProxy.add(message);
            i = i + 92;
            return;
        }
    }
    
}

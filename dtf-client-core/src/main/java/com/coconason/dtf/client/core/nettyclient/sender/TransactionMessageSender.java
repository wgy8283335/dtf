package com.coconason.dtf.client.core.nettyclient.sender;

import com.coconason.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * Transaction message sender.
 * 
 * @Author: Jason
 */

@Component
public final class TransactionMessageSender implements MessageSenderInterface {
    
    /**
     * Logger for TransactionMessageSender class.
     */
    private Logger logger = LoggerFactory.getLogger(TransactionMessageSender.class);
    
    /**
     * Queue of transaction message.
     */
    @Autowired
    @Qualifier("transactionMessageQueueProxy")
    private Queue queue;
    
    /**
     * Netty service.
     */
    @Autowired
    private NettyService service;
    
    /**
     * Thread pool.
     */
    @Autowired
    @Qualifier("threadPoolForClientProxy")
    private ExecutorService threadPoolForClientProxy;
    
    /**
     * Start to pull message from queue and send the message.
     */
    @Override
    public void startSendMessage() throws InterruptedException {
        threadPoolForClientProxy.execute(new SendMessageRunnable());
    }
    
    private void sendMessageInQueue() throws InterruptedException {
        while (true) {
            BaseTransactionServiceInfo info = (BaseTransactionServiceInfo) queue.poll();
            service.sendMsg(info);
        }
    }
    
    private class SendMessageRunnable implements Runnable {
        @Override
        public void run() {
            try {
                sendMessageInQueue();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }
    
}

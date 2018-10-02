package com.coconason.dtf.client.core.nettyclient.sender;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Queue;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:31
 */

@Component
public class TransactionMessageSender implements MessageSenderInterface{

    @Autowired
    @Qualifier("transactionMessageQueueProxy")
    private Queue queue;

    @Autowired
    private NettyService service;

    @Autowired
    private ThreadPoolForClient threadPoolForClient;

    @Override
    public void startSendMessage() throws InterruptedException{
        threadPoolForClient.addTask(new sendMessageRunnable());
    }

    private class sendMessageRunnable implements Runnable {
        @Override
        public void run() {
            try {
                sendMessageInQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessageInQueue() throws InterruptedException{
        while(true){
            TransactionServiceInfo info = (TransactionServiceInfo)queue.poll();
            service.sendMsg(info);
        }
    }

}

package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.threadpools.ThreadPoolForClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:31
 */

@Component
public class TransactionMessageSender {

    @Autowired
    private TransactionMessageQueue queue;

    @Autowired
    private NettyService service;

    @Autowired
    private ThreadPoolForClient threadPoolForClient;

    public void startSendMessage() throws InterruptedException{
        threadPoolForClient.addTask(new sendMessageRunnable());
    }

    private class sendMessageRunnable implements Runnable {
        @Override
        public void run() {
            try {
                sendMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage() throws InterruptedException{
        while(true){
            TransactionServiceInfo info = queue.take();
            service.sendMsg(info);
        }
    }

}

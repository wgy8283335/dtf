package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:31
 */

@Component
public class TransactionMessageSender {

    @Autowired
    TransactionMessageQueue queue;

    @Autowired
    NettyService service;

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    public void startSendMessage() throws InterruptedException{
        executorService.execute(new sendMessageRunnable());
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

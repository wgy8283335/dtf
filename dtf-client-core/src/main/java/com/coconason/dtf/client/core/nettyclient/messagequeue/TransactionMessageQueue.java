package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:07
 */
@Component
public class TransactionMessageQueue {
    private final LinkedBlockingQueue<TransactionServiceInfo> messageQueue = new LinkedBlockingQueue<>();

    public void put(TransactionServiceInfo info) throws InterruptedException{
        messageQueue.put(info);
    }

    public TransactionServiceInfo get() throws InterruptedException{
        return messageQueue.take();
    }

    public boolean isEmpty(){
        return messageQueue.isEmpty();
    }

}

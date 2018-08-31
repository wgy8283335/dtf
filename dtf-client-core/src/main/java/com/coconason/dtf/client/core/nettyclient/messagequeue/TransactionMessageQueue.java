package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:07
 */
@Component
public class TransactionMessageQueue {
    private final LinkedBlockingQueue<TransactionServiceInfo> messageQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    public void put(TransactionServiceInfo info) throws InterruptedException{
        System.out.println("put TransactionServiceInfo is "+info);
        messageQueue.put(info);
    }

    public TransactionServiceInfo take() throws InterruptedException{

        TransactionServiceInfo info = messageQueue.take();
        System.out.println("take TransactionServiceInfo is " + info);
        return info;
    }

    public boolean isEmpty(){
        return messageQueue.isEmpty();
    }

}

package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:07
 */
@Component
public class TransactionMessageQueue {
    private final ConcurrentLinkedQueue<TransactionServiceInfo> messageQueue = new ConcurrentLinkedQueue<>();

    public boolean put(TransactionServiceInfo info){
        return messageQueue.add(info);
    }

    public TransactionServiceInfo get(){
        return messageQueue.poll();
    }

}

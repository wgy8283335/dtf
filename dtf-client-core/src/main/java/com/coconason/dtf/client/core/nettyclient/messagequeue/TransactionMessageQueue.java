package com.coconason.dtf.client.core.nettyclient.messagequeue;

import org.springframework.stereotype.Component;
import java.util.concurrent.LinkedBlockingQueue;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
/**
 * @Author: Jason
 * @date: 2018/8/22-8:07
 */
@Component
public class TransactionMessageQueue {
    private final LinkedBlockingQueue<TransactionServiceInfo> messageQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    public void put(TransactionServiceInfo info) throws InterruptedException{
        messageQueue.put(info);
    }

    public TransactionServiceInfo take() throws InterruptedException{
        TransactionServiceInfo info = messageQueue.take();
        return info;
    }

    public boolean isEmpty(){
        return messageQueue.isEmpty();
    }

}

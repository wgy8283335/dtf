package com.coconason.dtf.demo2.cache;

import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: Jason
 * @date: 2018/9/6-12:15
 */
@Component
public class MessageAsyncQueue {
    private ConcurrentLinkedQueue<TransactionMessageGroupAsync> queue= new ConcurrentLinkedQueue<TransactionMessageGroupAsync>();

    public boolean offer(TransactionMessageGroupAsync groupAsync){
        return queue.offer(groupAsync);
    }

    public TransactionMessageGroupAsync poll(){
        return queue.poll();
    }
}

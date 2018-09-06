package com.coconason.dtf.demo2.cache;

import com.coconason.dtf.demo2.message.MessageInfo;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: Jason
 * @date: 2018/9/6-12:15
 */
@Component
public class MessageAsyncQueue {
    private ConcurrentLinkedQueue<MessageInfo> queue= new ConcurrentLinkedQueue<MessageInfo>();

    public boolean offer(MessageInfo messageInfo){
        return queue.offer(messageInfo);
    }

    public MessageInfo poll(){
        return queue.poll();
    }
}

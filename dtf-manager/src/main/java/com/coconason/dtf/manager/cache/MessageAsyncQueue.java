package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfo;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: Jason
 * @date: 2018/9/6-12:15
 */
public class MessageAsyncQueue {
    private ConcurrentLinkedQueue<MessageInfo> queue= new ConcurrentLinkedQueue<MessageInfo>();

    public boolean offer(MessageInfo messageInfo){
        return queue.offer(messageInfo);
    }

    public MessageInfo poll(){
        return queue.poll();
    }
}

package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfo;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jason
 * @date: 2018/9/6-12:15
 */
public class MessageAsyncQueue {
    private LinkedBlockingQueue<MessageInfo> queue= new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    public boolean offer(MessageInfo messageInfo){
        return queue.offer(messageInfo);
    }

    public MessageInfo poll(){
        return queue.poll();
    }
}

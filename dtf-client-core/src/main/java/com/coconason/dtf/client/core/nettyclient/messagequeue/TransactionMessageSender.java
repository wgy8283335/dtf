package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void sendMessage() throws InterruptedException{
        while(true){
            service.sendMsg(queue.take());
        }
    }
}

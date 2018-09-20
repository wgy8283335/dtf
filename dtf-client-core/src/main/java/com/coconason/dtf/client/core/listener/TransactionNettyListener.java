package com.coconason.dtf.client.core.listener;

import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageSender;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * @Author: Jason
 * @date: 2018/8/22-16:46
 */

@Service
final class TransactionNettyListener implements ApplicationListener<ContextRefreshedEvent>{

    @Autowired
    private NettyService nettyService;

    @Autowired
    private TransactionMessageSender sender;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        nettyService.start();
        try{
            sender.startSendMessage();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

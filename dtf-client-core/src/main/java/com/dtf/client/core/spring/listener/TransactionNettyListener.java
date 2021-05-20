package com.dtf.client.core.spring.listener;

import com.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.dtf.client.core.nettyclient.sender.MessageSenderInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Implementation of application listener interface.
 * 
 * @author wangguangyuan
 */

@Service
final class TransactionNettyListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private NettyService nettyService;

    @Autowired
    private MessageSenderInterface sender;

    /**
     * Start netty service during spring context setup.
     * 
     * @param contextRefreshedEvent context refresh event
     */
    @Override
    public void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {
        nettyService.start();
        try {
            sender.startSendMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.coconason.dtf.client.core.listener;

import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/8/22-16:46
 */
@Component
public class TransactionNettyListener implements ApplicationContextAware{

    @Autowired
    NettyService nettyService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        nettyService.start();
    }
}

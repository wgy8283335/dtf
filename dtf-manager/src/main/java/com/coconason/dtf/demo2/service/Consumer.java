package com.coconason.dtf.demo2.service;

import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.message.MessageInfo;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class Consumer implements Runnable{

    @Autowired
    MessageAsyncQueue messageAsyncQueue;

    @Override
    public void run() {
        TransactionMessageGroupAsync messageGroupAsync;
        while((messageGroupAsync = messageAsyncQueue.poll())!=null) {
            Set<MessageInfo> memberSet = messageGroupAsync.getMemberSet();
            for (MessageInfo messageInfo : memberSet) {
                if (messageInfo.isSubmitted() == false) {
                    RestTemplate restTemplate = new RestTemplate();
                    String result = restTemplate.postForObject(messageInfo.getUrl(), messageInfo.getObj(), String.class);
                    messageInfo.setSubmitted(true);
                }
            }
        }
    }
}

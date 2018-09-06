package com.coconason.dtf.demo2.service;

import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.message.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class Consumer implements Runnable{

    @Autowired
    MessageAsyncQueue messageAsyncQueue;

    @Override
    public void run() {
        MessageInfo messageInfo;
        while(true){
            if((messageInfo = messageAsyncQueue.poll())!=null){
                try{
                    if (messageInfo.isSubmitted() == false) {
                        RestTemplate restTemplate = new RestTemplate();
                        String result = restTemplate.postForObject(messageInfo.getUrl(), messageInfo.getObj(), String.class);
                    }
                }catch (Exception e){
                    messageAsyncQueue.offer(messageInfo);
                }
            }
            try{
                Thread.sleep(30000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

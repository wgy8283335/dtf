package com.coconason.dtf.demo2.service;

import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.message.MessageInfo;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class Consumer implements Runnable{

    MessageAsyncQueue messageAsyncQueue;

    public Consumer(MessageAsyncQueue messageAsyncQueue) {
        this.messageAsyncQueue = messageAsyncQueue;
    }

    @Override
    public void run() {
        MessageInfo messageInfo=null;
        while(true){
            if(messageAsyncQueue!=null){
                messageInfo = messageAsyncQueue.poll();
            }
            System.out.println("Consumer Start----------------------------------------");
            if(messageInfo!=null){
                try{
                    if (messageInfo.isSubmitted() == false) {
                        RestTemplate restTemplate = new RestTemplate();
                        String result = restTemplate.postForObject(messageInfo.getUrl(), messageInfo.getObj(), String.class);
                    }
                }catch (Exception e){
                    messageAsyncQueue.offer(messageInfo);
                }
            }else{
                try{
                    Thread.sleep(30000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

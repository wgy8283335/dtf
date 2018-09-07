package com.coconason.dtf.demo2.service;

import com.alibaba.fastjson.JSONObject;
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
            if(messageInfo==null){
                try{
                    Thread.sleep(10000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                try{
                    if (messageInfo.isSubmitted() == false) {
                        RestTemplate restTemplate = new RestTemplate();
                        String url= messageInfo.getUrl();
                        JSONObject obj = (JSONObject)messageInfo.getObj();
                        //restTemplate.postForObject(url, obj, String.class);
                        restTemplate.postForLocation(url, obj);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //messageAsyncQueue.offer(messageInfo);
                }finally {
                    messageInfo = null;
                }
            }
        }
    }
}

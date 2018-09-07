package com.coconason.dtf.demo2.service;

import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.message.MessageInfo;
import com.coconason.dtf.demo2.utils.HttpClientUtil;

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
            if(messageInfo==null){
                try{
                    Thread.sleep(10000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                try{
                    if (messageInfo.isSubmitted() == false) {
                        String url= messageInfo.getUrl();
                        String obj = messageInfo.getObj().toString();
                        String result = HttpClientUtil.doPostJson(url,obj);
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

package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncQueue;
import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.utils.HttpClientUtil;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class ConsumerRunnable implements Runnable{

    private MessageAsyncQueue messageAsyncQueue;

    public ConsumerRunnable(MessageAsyncQueue messageAsyncQueue) {
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
                        String result = HttpClientUtil.doPostJson(url,obj,"");
                        messageInfo.setSubmitted(true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    messageInfo.setSubmitted(false);
                    messageAsyncQueue.offer(messageInfo);
                }finally {
                    messageInfo = null;
                }
            }
        }
    }
}

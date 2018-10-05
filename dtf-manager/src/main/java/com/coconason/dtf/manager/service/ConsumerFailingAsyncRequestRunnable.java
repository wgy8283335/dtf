package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncQueueProxy;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import com.coconason.dtf.manager.utils.HttpClientUtil;

import java.util.Queue;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public final class ConsumerFailingAsyncRequestRunnable implements Runnable{

    private Queue messageAsyncQueueProxy;

    public ConsumerFailingAsyncRequestRunnable(MessageAsyncQueueProxy messageAsyncQueueProxy) {
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
    }

    @Override
    public void run() {
        MessageInfoInterface messageInfo=null;
        while(true){
            if(messageAsyncQueueProxy !=null){
                messageInfo = (MessageInfoInterface)messageAsyncQueueProxy.poll();
            }
            if(messageInfo==null){
                try{
                    Thread.sleep(10000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                try{
                    if (messageInfo.isCommitted() == false) {
                        String url= messageInfo.getUrl();
                        String obj = messageInfo.getObj().toString();
                        String result = HttpClientUtil.doPostJson(url,obj,"");
                        if("".equals(result)){
                            messageInfo.setCommitted(false);
                            messageAsyncQueueProxy.add(messageInfo);
                        }else{
                            messageInfo.setCommitted(true);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    messageInfo = null;
                }
            }
        }
    }
}

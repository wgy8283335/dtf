package com.coconason.dtf.demo2.service;

import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.cache.MessageCache;
import com.coconason.dtf.demo2.message.MessageInfo;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import com.coconason.dtf.demo2.utils.HttpClientUtil;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class SendRunnable implements Runnable{

    private MessageCache messageCache;

    private String groupId;

    private MessageAsyncQueue messageAsyncQueue;

    public SendRunnable(MessageCache messageCache,String groupId,MessageAsyncQueue messageAsyncQueue) {
        this.messageCache = messageCache;
        this.groupId = groupId;
        this.messageAsyncQueue = messageAsyncQueue;
    }

    @Override
    public void run() {
        try{
            Thread.sleep(5000);
            TransactionMessageGroupAsync theMessageGroupAsync = (TransactionMessageGroupAsync)messageCache.get(groupId);
            Set<MessageInfo> theMemberSet = theMessageGroupAsync.getMemberSet();
            for(MessageInfo messageInfo :theMemberSet){
                String url= messageInfo.getUrl();
                String obj = messageInfo.getObj().toString();
                try{
                    HttpClientUtil.doPostJson(url,obj,groupId);
                    messageInfo.setSubmitted(true);
                }catch (Exception e){
                    //if fail put the info of the service into a cache,and there will be another thread to check and execute.
                    messageAsyncQueue.offer(messageInfo);
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

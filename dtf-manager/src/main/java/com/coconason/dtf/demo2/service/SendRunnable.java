package com.coconason.dtf.demo2.service;

import com.coconason.dtf.demo2.cache.MessageAsyncCache;
import com.coconason.dtf.demo2.cache.MessageAsyncQueue;
import com.coconason.dtf.demo2.message.MessageInfo;
import com.coconason.dtf.demo2.message.TransactionMessageForSubmit;
import com.coconason.dtf.demo2.message.TransactionMessageGroupAsync;
import com.coconason.dtf.demo2.utils.HttpClientUtil;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class SendRunnable implements Runnable{

    private MessageAsyncCache messageAsyncCache;

    private TransactionMessageForSubmit transactionMessageForSubmit;

    private MessageAsyncQueue messageAsyncQueue;

    public SendRunnable(MessageAsyncCache messageAsyncCache, TransactionMessageForSubmit transactionMessageForSubmit, MessageAsyncQueue messageAsyncQueue) {
        this.messageAsyncCache = messageAsyncCache;
        this.transactionMessageForSubmit = transactionMessageForSubmit;
        this.messageAsyncQueue = messageAsyncQueue;
    }

    @Override
    public void run() {
        try{
            //Thread.sleep(5000);
            //get the TransactionMessageGroupAsync from the messageAsyncCache
            TransactionMessageGroupAsync theMessageGroupAsync = messageAsyncCache.get(transactionMessageForSubmit.getGroupId());
            Set<MessageInfo> theMemberSet = theMessageGroupAsync.getMemberSet();
            for(MessageInfo messageInfo :theMemberSet){
                String url= messageInfo.getUrl();
                String obj = messageInfo.getObj().toString();
                try{
                    HttpClientUtil.doPostJson(url,obj,transactionMessageForSubmit.getGroupId());
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

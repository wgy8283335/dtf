package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageAsyncCache;
import com.coconason.dtf.manager.cache.MessageAsyncQueue;
import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.TransactionMessageForSubmit;
import com.coconason.dtf.manager.message.TransactionMessageGroupAsync;
import com.coconason.dtf.manager.utils.HttpClientUtil;

import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class SendAsyncRequestRunnable implements Runnable{

    private MessageAsyncCache messageAsyncCache;

    private TransactionMessageForSubmit transactionMessageForSubmit;

    private MessageAsyncQueue messageAsyncQueue;

    public SendAsyncRequestRunnable(MessageAsyncCache messageAsyncCache, TransactionMessageForSubmit transactionMessageForSubmit, MessageAsyncQueue messageAsyncQueue) {
        this.messageAsyncCache = messageAsyncCache;
        this.transactionMessageForSubmit = transactionMessageForSubmit;
        this.messageAsyncQueue = messageAsyncQueue;
    }

    @Override
    public void run() {
        try{
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
            messageAsyncCache.clear(transactionMessageForSubmit.getGroupId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

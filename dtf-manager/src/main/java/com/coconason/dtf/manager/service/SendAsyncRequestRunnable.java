package com.coconason.dtf.manager.service;

import com.coconason.dtf.manager.cache.MessageCacheInterface;
import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;
import com.coconason.dtf.manager.utils.HttpClientUtil;

import java.util.Queue;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:04
 */
public class SendAsyncRequestRunnable implements Runnable{

    private MessageCacheInterface messageAsyncCacheProxy;

    private TransactionMessageGroupInterface transactionMessageForSubmit;

    private Queue messageAsyncQueueProxy;

    public SendAsyncRequestRunnable(MessageCacheInterface messageAsyncCacheProxy, TransactionMessageGroupInterface transactionMessageForSubmit, Queue messageAsyncQueueProxy) {
        this.messageAsyncCacheProxy = messageAsyncCacheProxy;
        this.transactionMessageForSubmit = transactionMessageForSubmit;
        this.messageAsyncQueueProxy = messageAsyncQueueProxy;
    }

    @Override
    public void run() {
        try{
            //get the TransactionMessageGroupAsync from the messageAsyncCacheProxy
            TransactionMessageGroupInterface theMessageGroupAsync = messageAsyncCacheProxy.get(transactionMessageForSubmit.getGroupId());
            Set<MessageInfo> theMemberSet = theMessageGroupAsync.getMemberSet();
            for(MessageInfo messageInfo :theMemberSet){
                String url= messageInfo.getUrl();
                String obj = messageInfo.getObj().toString();
                try{
                    String result = HttpClientUtil.doPostJson(url,obj,transactionMessageForSubmit.getGroupId());
                    if("".equals(result)){
                        messageAsyncQueueProxy.add(messageInfo);
                    }else{
                        messageInfo.setCommitted(true);
                    }
                }catch (Exception e){
                    //if fail put the info of the service into a cache,and there will be another thread to check and execute.
                    e.printStackTrace();
                }
            }
            messageAsyncCacheProxy.invalidate(transactionMessageForSubmit.getGroupId());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

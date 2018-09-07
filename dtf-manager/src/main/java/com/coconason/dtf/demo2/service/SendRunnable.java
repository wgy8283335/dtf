package com.coconason.dtf.demo2.service;

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

    public SendRunnable(MessageCache messageCache,String groupId) {
        this.messageCache = messageCache;
        this.groupId = groupId;
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
                HttpClientUtil.doPostJson(url,obj,groupId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

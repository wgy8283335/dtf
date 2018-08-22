package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.ClientTransactionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Jason
 * @date: 2018/8/22-8:31
 */
@Component
public class TransactionMessageSender {
    @Autowired
    TransactionMessageQueue queue;

    @Autowired
    ClientTransactionHandler handler;

    public void sendMessage(){
        TransactionServiceInfo transactionServiceInfo;
        while ((transactionServiceInfo = queue.get())!=null){
            handler.sendMsg(transactionServiceInfo);
        }
    }
}

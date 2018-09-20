package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.DBOperationType;
import com.coconason.dtf.client.core.dbconnection.LockAndCondition;
import com.coconason.dtf.client.core.dbconnection.ThreadsInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:24
 */
@Component
public class RestClientAsync {

    @Autowired
    private NettyService nettyService;

    @Autowired
    @Qualifier("threadsInfo")
    private ThreadsInfo thirdThreadsInfo;

    public void sendPost(String url, Object object){
        TransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        LockAndCondition lc = new LockAndCondition(new ReentrantLock(), DBOperationType.DEFAULT);
        thirdThreadsInfo.put(groupInfo.getGroupId(),lc);
        groupInfo.addNewMemeber();
        TransactionGroupInfo.setCurrent(groupInfo);
        TransactionServiceInfo transactionServiceInfo = TransactionServiceInfo.newInstanceForRestful(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.ADD_ASYNC, groupInfo.getGroupId(), groupInfo.getMemberId(), url, object);
        nettyService.sendMsg(transactionServiceInfo);
        lc.await();
        LockAndCondition lc2 = thirdThreadsInfo.get(groupInfo.getGroupId());
        while(lc2.getState()==DBOperationType.ASYNCFAIL){
            try{
                nettyService.sendMsg(transactionServiceInfo);
                lc2.await();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

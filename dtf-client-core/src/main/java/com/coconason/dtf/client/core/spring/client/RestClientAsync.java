package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.DbOperationType;
import com.coconason.dtf.client.core.dbconnection.ClientLockAndCondition;
import com.coconason.dtf.client.core.dbconnection.ThreadLockCacheProxy;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
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
    @Qualifier("threadLockCacheProxy")
    private ThreadLockCacheProxy thirdThreadLockCacheProxy;

    public void sendPost(String url, Object object) throws Exception{
        TransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        ClientLockAndCondition lc = new ClientLockAndCondition(new ReentrantLock(), DbOperationType.DEFAULT);
        thirdThreadLockCacheProxy.put(groupInfo.getGroupId(),lc);
        groupInfo.addNewMemeber();
        TransactionGroupInfo.setCurrent(groupInfo);
        TransactionServiceInfo transactionServiceInfo = TransactionServiceInfo.newInstanceForRestful(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.ADD_ASYNC, groupInfo.getGroupId(), groupInfo.getMemberId(), url, object);
        lc.awaitLimitedTime(nettyService,transactionServiceInfo,"RestClientAsync sendPost fail",10000, TimeUnit.MILLISECONDS);
    }
}

package com.dtf.client.core.spring.client;

import com.dtf.client.core.beans.group.BaseTransactionGroupInfo;
import com.dtf.client.core.beans.service.BaseTransactionServiceInfo;
import com.dtf.client.core.beans.group.TransactionGroupInfo;
import com.dtf.client.core.beans.service.TransactionServiceInfoFactory;
import com.dtf.client.core.dbconnection.OperationType;
import com.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.dtf.client.core.thread.ClientLockAndCondition;
import com.dtf.client.core.thread.ThreadLockCacheProxy;
import com.dtf.common.protobuf.MessageProto;
import com.dtf.common.utils.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Restful request client in asynchronous mode.
 * 
 * @author wangguangyuan
 */
@Component
public final class RestClientAsync {
    
    @Autowired
    private NettyService nettyService;
    
    @Autowired
    @Qualifier("threadLockCacheProxy")
    private ThreadLockCacheProxy thirdThreadLockCacheProxy;
    
    /**
     * Time for waiting.
     */
    private final int waitTime = 10000;
    
    /**
     * Send request in post method.
     *
     * @param url http url
     * @param object parameter
     * @throws Exception when awaitLimitedTime() throw exception
     */
    public void sendPost(final String url, final Object object) throws Exception {
        send(url, object, "post");
    }
    
    /**
     * Send request in put method.
     *
     * @param url http url
     * @param object parameter
     * @throws Exception when awaitLimitedTime() throw exception
     */
    public void sendPut(final String url, final Object object) throws Exception {
        send(url, object, "put");
    }
    
    /**
     * Send request in delete method.
     *
     * @param url http url
     * @param object parameter
     * @throws Exception when awaitLimitedTime() throw exception
     */
    public void sendDelete(final String url, final Object object) throws Exception {
        send(url, object, "delete");
    }
    
    /**
     * Send request in get method.
     *
     * @param url http url
     * @param object parameter
     * @throws Exception when awaitLimitedTime() throw exception
     */
    public void sendGet(final String url, final Object object) throws Exception {
        send(url, object, "get");
    }
    
    private void send(final String url, final Object object, final String httpAction) throws Exception {
        BaseTransactionGroupInfo groupInfo = TransactionGroupInfo.getCurrent();
        ClientLockAndCondition lc = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
        thirdThreadLockCacheProxy.put(groupInfo.getGroupId(), lc);
        groupInfo.addNewMember();
        TransactionGroupInfo.setCurrent(groupInfo);
        BaseTransactionServiceInfo transactionServiceInfo = TransactionServiceInfoFactory.newInstanceForRestful(UuidGenerator.generateUuid(),
                MessageProto.Message.ActionType.ADD_ASYNC, groupInfo.getGroupId(), groupInfo.getMemberId(), url, object, httpAction);
        lc.awaitLimitedTime(nettyService, transactionServiceInfo, "RestClientAsync sendPost fail", waitTime, TimeUnit.MILLISECONDS);
    }
    
}

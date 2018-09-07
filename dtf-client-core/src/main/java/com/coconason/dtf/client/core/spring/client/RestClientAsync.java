package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.dbconnection.DBOperationType;
import com.coconason.dtf.client.core.dbconnection.LockAndCondition;
import com.coconason.dtf.client.core.dbconnection.ThirdThreadsInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:24
 */
@Component
public class RestClientAsync {

    @Autowired
    NettyService nettyService;

    @Autowired
    ThirdThreadsInfo thirdThreadsInfo;

    public void sendPost(String url, Object object){
        AsyncSubmitRunnable asyncSubmitRunnable = new AsyncSubmitRunnable(TransactionGroupInfo.getCurrent(),url,object);
        Thread thread = new Thread(asyncSubmitRunnable);
        thread.start();
    }

    private class AsyncSubmitRunnable implements Runnable{
        private TransactionGroupInfo groupInfo;
        private String url;
        private Object object;

        public AsyncSubmitRunnable(TransactionGroupInfo groupInfo,String url,Object object) {
            this.groupInfo = groupInfo;
            this.url = url;
            this.object = object;
        }

        @Override
        public void run() {
            LockAndCondition lc = new LockAndCondition(new ReentrantLock(), DBOperationType.DEFAULT);
            thirdThreadsInfo.put(groupInfo.getGroupId(),lc);
            TransactionServiceInfo transactionServiceInfo = new TransactionServiceInfo(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.ADD_ASYNC, groupInfo.getGroupId(), groupInfo.getMemberId(), url, object);
            nettyService.sendMsg(transactionServiceInfo);
            lc.await();
            LockAndCondition lc2 = thirdThreadsInfo.get(groupInfo.getGroupId());
            while(lc2.getState()==DBOperationType.ASYNCFAIL){
                nettyService.sendMsg(transactionServiceInfo);
                try{
                    Thread.sleep(30000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}

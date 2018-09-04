package com.coconason.dtf.client.core.transaction;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.constant.Member;
import com.coconason.dtf.client.core.dbconnection.DBOperationType;
import com.coconason.dtf.client.core.dbconnection.LockAndCondition;
import com.coconason.dtf.client.core.dbconnection.SecondThreadsInfo;
import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueue;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.utils.GroupidGenerator;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import com.coconason.dtf.common.utils.UuidGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/8/19-20:38
 */
@Component
public class AspectHandler {

    @Autowired
    TransactionMessageQueue queue;
    @Autowired
    NettyService nettyService;
    @Autowired
    SecondThreadsInfo secondThreadsInfo;

    public Object before(TransactionGroupInfo transactionGroupInfo,ProceedingJoinPoint point) throws Throwable {

        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method currentMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        DtfTransaction transaction = currentMethod.getAnnotation(DtfTransaction.class);
        //transactionType has:SYNC_FINAL、SYNC_STRONG、ASYNC_FINAL
        String transactionType = transaction.type();
        Transactional transactional = currentMethod.getAnnotation(Transactional.class);
        if (transactional == null) {
            transactional = clazz.getAnnotation(Transactional.class);
        }
        //declare one transaction group
        TransactionGroupInfo groupInfo;
        //1.When the service is creator,should create groupId and store in public object.
        //2.Then execute the program.And if the program has transactional operation in database,
        //should use database proxy to send transaction information to the transaction server.
        //3.At the end send submit request to the server, and listen the response from server.
        //If success, submit transaction by database proxy.If fail,cancel transaction by database proxy.
        if(transactionGroupInfo == null) {
            //1.
            String groupIdTemp = GroupidGenerator.getStringId(0, 0);
            groupInfo = new TransactionGroupInfo(groupIdTemp, Member.ORIGNAL_ID);
            TransactionGroupInfo.setCurrent(groupInfo);
            switch (transactionType) {
                case "SYNC_FINAL":
                    TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD, groupInfo.getGroupId(), groupInfo.getMemberId(), method, args));
                    break;
                case "SYNC_STRONG":
                    TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD_STRONG, groupInfo.getGroupId(), groupInfo.getMemberId(), method, args));
                    break;
                default:
                    break;
            }
            //2.
            point.proceed();
            //3.Send confirm message to netty server, in order to commit all transaction in the service
            switch (transactionType) {
                case "SYNC_FINAL":
                    queue.put(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.APPLYFORSUBMIT, TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers()));
                    break;
                case "SYNC_STRONG":
                    //4.wait for confirm from the server and use lock condition to wait for signaling.
                    LockAndCondition secondlc = new LockAndCondition(new ReentrantLock(), DBOperationType.DEFAULT);
                    secondThreadsInfo.put(TransactionGroupInfo.getCurrent().getGroupId(), secondlc);
                    queue.put(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.APPLYFORSUBMIT_STRONG, TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers()));
                    secondlc.await();
                    LockAndCondition secondlc2 = secondThreadsInfo.get(TransactionGroupInfo.getCurrent().getGroupId());
                    if(secondlc2.getState() == DBOperationType.WHOLEFAIL){
                        throw new Exception("Distributed transaction failed");
                    }
                    break;
                default:
                    break;
            }
        }
        //1.When the service is follower,execute the program.And if the program has transactional operation in database,
        //should use database proxy to send transaction information to the transaction server.
        //2.And listen the response from server.
        //If the response is submit,submit transaction by database proxy.If the response is cancel,cancel transaction by database proxy.
        else{
            //if the thread does not have transactionGroupInfo,set current transaction group information
            if(TransactionGroupInfo.getCurrent()==null){
                transactionGroupInfo.addNewMemeber();
                TransactionGroupInfo.setCurrent(transactionGroupInfo);
            }
            //if the thread does not have transactionServiceInfo,set current transaction service information
            if(TransactionServiceInfo.getCurrent()==null){
                switch (transactionType){
                    case "SYNC_FINAL":
                        TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,transactionGroupInfo.getGroupId(),transactionGroupInfo.getMemberId(),method,args));
                        break;
                    case "SYNC_STRONG":
                        TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD_STRONG,transactionGroupInfo.getGroupId(),transactionGroupInfo.getMemberId(),method,args));
                        break;
                    default:
                        break;
                }

            }
            point.proceed();
        }
        return null;
    }
}

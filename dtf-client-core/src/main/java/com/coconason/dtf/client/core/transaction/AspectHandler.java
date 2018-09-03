package com.coconason.dtf.client.core.transaction;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.constant.Member;
import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueue;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.utils.GroupidGenerator;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Method;

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

    public Object before(TransactionGroupInfo transactionGroupInfo,ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method currentMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        DtfTransaction transaction = currentMethod.getAnnotation(DtfTransaction.class);
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
        if(transactionGroupInfo == null){
            //1.
            String groupIdTemp = GroupidGenerator.getStringId(0,0);
            groupInfo = new TransactionGroupInfo(groupIdTemp, Member.ORIGNAL_ID);
            TransactionGroupInfo.setCurrent(groupInfo);
            TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,groupInfo.getGroupId(),groupInfo.getMemberId(),method,args));
            //2.
            point.proceed();
            //3.
            //ClientTransactionHandler clientTransactionHandler = new ClientTransactionHandler();
            //serviceInfo = new TransactionServiceInfo(groupInfo.getGroupId(),groupInfo.getGroupMemberId(),"","","");
            //clientTransactionHandler.sendMsg(serviceInfo);
            //queue.put(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,groupInfo.getGroupId(),1,method,args));
            //4.Send confirm message to netty server, in oreder to commit all transaction in the service
            queue.put(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.APPLYFORSUBMIT,TransactionGroupInfo.getCurrent().getGroupId(),TransactionGroupInfo.getCurrent().getGroupMembers()));
            //nettyService.sendMsg(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.APPLYFORSUBMIT,groupInfo.getGroupId().toString(),groupInfo.getGroupMembers()));
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
                TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,transactionGroupInfo.getGroupId(),transactionGroupInfo.getMemberId(),method,args));
            }
            //1.
            point.proceed();
            //2.ClientTransactionHandler clientTransactionHandler = new ClientTransactionHandler();
            //serviceInfo = new TransactionServiceInfo(groupInfo.getGroupId(),groupInfo.getGroupMemberId(),"","","");
            //clientTransactionHandler.sendMsg(serviceInfo);
           // queue.put(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,groupId,groupMemberId,method,args));
        }
        return null;
    }
}

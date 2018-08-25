package com.coconason.dtf.client.core.transaction;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionServiceInfo;
import com.coconason.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueue;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.utils.GroupidGenerator;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.coconason.dtf.common.protobuf.MessageProto.Message.ActionType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
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

    public Object before(String groupId, Integer groupMemberId,ProceedingJoinPoint point) throws Throwable {
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
        if(groupId == null){
            //1.
            groupInfo = new TransactionGroupInfo(GroupidGenerator.getStringId(0,0));
            groupInfo.addMemeber(1);
            TransactionGroupInfo.setCurrent(groupInfo);
            TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,groupInfo.getGroupId(),1,method,args));
            //2.
            point.proceed();

            //3.
            //ClientTransactionHandler clientTransactionHandler = new ClientTransactionHandler();
            //serviceInfo = new TransactionServiceInfo(groupInfo.getGroupId(),groupInfo.getGroupMemberId(),"","","");
            //clientTransactionHandler.sendMsg(serviceInfo);
            //queue.put(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,groupInfo.getGroupId(),1,method,args));
            //4.Send confirm message to netty server, in oreder to commit all transaction in the service
            nettyService.sendMsg(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.APPLYFORSUBMIT,groupInfo.getGroupId().toString(),groupInfo.getGroupMembers()));

        }
        //1.When the service is follower,execute the program.And if the program has transactional operation in database,
        //should use database proxy to send transaction information to the transaction server.
        //2.And listen the response from server.
        //If the response is submit,submit transaction by database proxy.If the response is cancel,cancel transaction by database proxy.
        else{
            groupInfo = new TransactionGroupInfo(groupId);
            groupInfo.addMemeber(groupMemberId);
            TransactionGroupInfo.setCurrent(groupInfo);
            TransactionServiceInfo.setCurrent(new TransactionServiceInfo(UuidGenerator.generateUuid(), ActionType.ADD,groupId,groupMemberId,method,args));
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

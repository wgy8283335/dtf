package com.coconason.dtf.client.core.transaction;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.beans.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfo;
import com.coconason.dtf.client.core.beans.TransactionGroupInfoFactory;
import com.coconason.dtf.client.core.beans.TransactionServiceInfoFactory;
import com.coconason.dtf.client.core.beans.TransactionType;
import com.coconason.dtf.client.core.constants.Member;
import com.coconason.dtf.client.core.dbconnection.OperationType;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.thread.ClientLockAndCondition;
import com.coconason.dtf.client.core.thread.ClientLockAndConditionInterface;
import com.coconason.dtf.client.core.utils.GroupIdGenerator;
import com.coconason.dtf.common.protobuf.MessageProto;
import com.coconason.dtf.common.utils.UuidGenerator;
import com.google.common.cache.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Aspect before the join point.
 * 
 * @Author: Jason
 */
@Component
public final class AspectHandler implements AspectInterface {
    
    /**
     * Logger of AspectHandler class.
     */
    private Logger logger = LoggerFactory.getLogger(AspectHandler.class);
    
    /**
     * Queue of transaction message.
     */
    @Autowired
    @Qualifier("transactionMessageQueueProxy")
    private Queue queue;
    
    /**
     * Netty service.
     */
    @Autowired
    private NettyService nettyService;
    
    /**
     * Cache of lock and condition.
     */
    @Autowired
    @Qualifier("threadLockCacheProxy")
    private Cache<String, ClientLockAndConditionInterface> finalCommitThreadLockCacheProxy;
    
    /**
     * Aspect before the join point.
     * 
     * @param info group information
     * @param point join point
     * @return result of method of the join point
     * @throws Throwable throwable
     */
    @Override
    public Object before(final String info, final ProceedingJoinPoint point) throws Throwable {
        Method method = getMethod(point);
        Object[] args = getArgs(point);
        TransactionType transactionType = getTransactionType(point, method);
        TransactionType.setCurrent(transactionType);
        Object result = proceedPointBy(transactionType, info, point, method, args);
        return result;
    }
    
    private Method getMethod(final ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method result = signature.getMethod();
        return result;
    }
    
    private Object[] getArgs(final ProceedingJoinPoint point) {
        Class<?> clazz = point.getTarget().getClass();
        Object[] result = point.getArgs();
        return result;
    }
    
    private TransactionType getTransactionType(final ProceedingJoinPoint point, final Method method) throws Exception {
        Class<?> clazz = point.getTarget().getClass();
        Method currentMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        DtfTransaction transaction = currentMethod.getAnnotation(DtfTransaction.class);
        TransactionType result = TransactionType.newInstance(transaction.type());
        return result;
    }
    
    private Object proceedPointBy(final TransactionType transactionType, final String info, final ProceedingJoinPoint point, 
                                  final Method method, final Object[] args) throws Throwable {
        if (TransactionType.ASYNC_FINAL == transactionType) {
            return proceedPointWhenAsyncFinal(transactionType, info, point, method, args);
        }
        if (TransactionType.SYNC_FINAL == transactionType) {
            return proceedPointWhenSyncFinal(transactionType, info, point, method, args);
        }
        if (TransactionType.SYNC_STRONG == transactionType) {
            return proceedPointWhenSyncStrong(transactionType, info, point, method, args);
        }
        return null;
    }
    
    private Object proceedPointWhenAsyncFinal(final TransactionType transactionType, final String info, final ProceedingJoinPoint point, 
                                                         final Method method, final Object[] args) throws Throwable {
        if (null == info) {
            String groupIdTemp = GroupIdGenerator.getStringId(0, 0);
            BaseTransactionGroupInfo groupInfo = TransactionGroupInfoFactory.getInstance(groupIdTemp, Member.ORIGINAL_ID);
            TransactionGroupInfo.setCurrent(groupInfo);
            Object result = point.proceed();
            ClientLockAndConditionInterface asyncFinalCommitLc = new ClientLockAndCondition(new ReentrantLock(), OperationType.DEFAULT);
            finalCommitThreadLockCacheProxy.put(TransactionGroupInfo.getCurrent().getGroupId(), asyncFinalCommitLc);
            BaseTransactionServiceInfo serviceInfo = TransactionServiceInfoFactory.newInstanceForAsyncCommit(UuidGenerator.generateUuid(),
                    MessageProto.Message.ActionType.ASYNC_COMMIT, TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers());
            asyncFinalCommitLc.awaitLimitedTime(nettyService, serviceInfo, "commit async fail", 10000, TimeUnit.MILLISECONDS);
            return result;
        } else {
            Object result = point.proceed();
            return result;
        }

    }
    
    private Object proceedPointWhenSyncFinal(final TransactionType transactionType, final String info, final ProceedingJoinPoint point, 
                                                        final Method method, final Object[] args) throws Throwable {
        if (null == info) {
            Object result = null;
            //1.
            String groupIdTemp = GroupIdGenerator.getStringId(0, 0);
            BaseTransactionGroupInfo groupInfo = TransactionGroupInfoFactory.getInstance(groupIdTemp, Member.ORIGINAL_ID);
            TransactionGroupInfo.setCurrent(groupInfo);
            switchTransactionType(transactionType, groupInfo, method, args);
            //2.
            try {
                result = point.proceed();
            } catch (Exception e) {
                if (Member.ORIGINAL_ID.equals(TransactionGroupInfo.getCurrent().getMemberId())) {
                    queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.CANCEL,
                            TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers()));
                }
                logger.error(e.getMessage());
            }
            return result;
        } else {
            Object result = proceedPointWhenSyncWithInfo(transactionType, info, point, method, args);
            return result;
        }
    }
    
    private Object proceedPointWhenSyncStrong(final TransactionType transactionType, final String info, final ProceedingJoinPoint point, 
                                                         final Method method, final Object[] args) throws Throwable {
        if (null == info) {
            Object result = null;
            //1.
            String groupIdTemp = GroupIdGenerator.getStringId(0, 0);
            BaseTransactionGroupInfo groupInfo = TransactionGroupInfoFactory.getInstance(groupIdTemp, Member.ORIGINAL_ID);
            TransactionGroupInfo.setCurrent(groupInfo);
            switchTransactionType(transactionType, groupInfo, method, args);
            //2.
            try {
                result = point.proceed();
            } catch (Exception e) {
                if (Member.ORIGINAL_ID.equals(TransactionGroupInfo.getCurrent().getMemberId())) {
                    queue.add(TransactionServiceInfoFactory.newInstanceWithGroupIdSet(UuidGenerator.generateUuid(), MessageProto.Message.ActionType.CANCEL,
                            TransactionGroupInfo.getCurrent().getGroupId(), TransactionGroupInfo.getCurrent().getGroupMembers()));
                }
                logger.error(e.getMessage());
            }
            if (Member.ORIGINAL_ID.equals(TransactionGroupInfo.getCurrent().getMemberId())
                    && finalCommitThreadLockCacheProxy.getIfPresent(TransactionGroupInfo.getCurrent().getGroupId()).getState() == OperationType.WHOLE_FAIL) {
                logger.error("system transaction error");
                throw new Exception("system transaction error");
            }
            return result;
        } else {
            Object result = proceedPointWhenSyncWithInfo(transactionType, info, point, method, args);
            return result;
        }
    }
    
    private Object proceedPointWhenSyncWithInfo(final TransactionType transactionType, final String info, final ProceedingJoinPoint point, 
                                                         final Method method, final Object[] args) throws Throwable {
        BaseTransactionGroupInfo transactionGroupInfo = TransactionGroupInfoFactory.getInstanceByParsingString(info);
        //if the thread does not have transactionGroupInfo,set current transaction group information and current transaction service information
        BaseTransactionGroupInfo temp = TransactionGroupInfo.getCurrent();
        if (null == temp) {
            transactionGroupInfo.addNewMember();
            TransactionGroupInfo.setCurrent(transactionGroupInfo);
            switchTransactionType(transactionType, transactionGroupInfo, method, args);
        }
        Object result = point.proceed();
        return result;
    }
    
    private void switchTransactionType(final TransactionType transactionType, final BaseTransactionGroupInfo transactionGroupInfo, final Method method, final Object[] args) {
        switch (transactionType) {
            case SYNC_FINAL:
                BaseTransactionServiceInfo.setCurrent(TransactionServiceInfoFactory.newInstanceForSyncAdd(UuidGenerator.generateUuid(), 
                        MessageProto.Message.ActionType.ADD, transactionGroupInfo.getGroupId(), transactionGroupInfo.getMemberId(), method, args));
                break;
            case SYNC_STRONG:
                BaseTransactionServiceInfo.setCurrent(TransactionServiceInfoFactory.newInstanceForSyncAdd(UuidGenerator.generateUuid(), 
                        MessageProto.Message.ActionType.ADD_STRONG, transactionGroupInfo.getGroupId(), transactionGroupInfo.getMemberId(), method, args));
                break;
            default:
                break;
        }
    }
    
}

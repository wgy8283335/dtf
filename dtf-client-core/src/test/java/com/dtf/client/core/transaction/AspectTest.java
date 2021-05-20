package com.dtf.client.core.transaction;

import com.dtf.client.core.annotation.DtfTransaction;
import com.dtf.client.core.beans.group.TransactionGroupInfoFactory;
import com.dtf.client.core.exception.DtfException;
import com.dtf.client.core.nettyclient.messagequeue.TransactionMessageQueueProxy;
import com.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.dtf.client.core.thread.ThreadLockCacheProxy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AspectTest {
    
    @Test
    public void assertBeforeWithSyncFinal() throws Throwable {
        ProceedingJoinPoint point = createProceedingJoinPoint(new DefaultService(), "testSyncFinal");
        AspectHandler handler = new AspectHandler();
        handler.before(TransactionGroupInfoFactory.getInstance("8898392", 1L).toString(), point);
    }
    
    @Test
    public void assertBeforeWithAsync() throws Throwable {
        ProceedingJoinPoint point = createProceedingJoinPoint(new DefaultService(), "testAsync");
        AspectHandler handler = new AspectHandler();
        handler.before(TransactionGroupInfoFactory.getInstance("8898392", 1L).toString(), point);
    }
    
    @Test
    public void assertBeforeWithSyncStrong() throws Throwable {
        ProceedingJoinPoint point = createProceedingJoinPoint(new DefaultService(), "testSyncStrong");
        AspectHandler handler = new AspectHandler();
        handler.before(TransactionGroupInfoFactory.getInstance("8898392", 1L).toString(), point);
    }
    
    @Test
    public void assertBeforeWithSyncFinalNull() throws Throwable {
        ProceedingJoinPoint point = createProceedingJoinPoint(new DefaultService(), "testSyncFinal");
        AspectHandler handler = createAspectHandler();
        handler.before(null, point);
    }

    @Test(expected = InterruptedException.class)
    public void assertBeforeWithAsyncNull() throws Throwable {
        ProceedingJoinPoint point = createProceedingJoinPoint(new DefaultService(), "testAsync");
        AspectHandler handler = createAspectHandler();
        handler.before(null, point);
    }

    @Test(expected = DtfException.class)
    public void assertBeforeWithSyncStrongNull() throws Throwable {
        ProceedingJoinPoint point = createProceedingJoinPoint(new DefaultService(), "testSyncStrong");
        AspectHandler handler = createAspectHandler();
        handler.before(null, point);
    }

    private AspectHandler createAspectHandler() throws NoSuchFieldException, IllegalAccessException {
        AspectHandler result = new AspectHandler();
        Field queueField = result.getClass().getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(result, new TransactionMessageQueueProxy());
        Field finalCommitThreadLockCacheProxyField = result.getClass().getDeclaredField("finalCommitThreadLockCacheProxy");
        finalCommitThreadLockCacheProxyField.setAccessible(true);
        finalCommitThreadLockCacheProxyField.set(result, new ThreadLockCacheProxy());
        Field threadLockCacheProxyField = result.getClass().getDeclaredField("threadLockCacheProxy");
        threadLockCacheProxyField.setAccessible(true);
        threadLockCacheProxyField.set(result, new ThreadLockCacheProxy());
        Field nettyServiceField = result.getClass().getDeclaredField("nettyService");
        nettyServiceField.setAccessible(true);
        NettyService nettyService = mock(NettyService.class);
        doNothing().when(nettyService).sendMsg(any());
        nettyServiceField.set(result, nettyService);
        return result;
    }
    
    private ProceedingJoinPoint createProceedingJoinPoint(DefaultService service, String methodName) throws NoSuchMethodException {
        ProceedingJoinPoint result = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(result.getSignature()).thenReturn(signature);
        when(result.getTarget()).thenReturn(service);
        when(result.getArgs()).thenReturn(new Object[0]);
        when(signature.getMethod()).thenReturn(DefaultService.class.getDeclaredMethod(methodName));
        return result;
    }
    
    private class DefaultService {
        
        public DefaultService() {
            
        }
        
        @DtfTransaction
        public void testSyncFinal() {
            
        }

        @DtfTransaction(type = "ASYNC_FINAL")
        public void testAsync() {

        }

        @DtfTransaction(type = "SYNC_STRONG")
        public void testSyncStrong() {

        }

    }
    
}

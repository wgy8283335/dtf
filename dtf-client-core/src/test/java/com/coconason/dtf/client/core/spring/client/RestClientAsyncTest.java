package com.coconason.dtf.client.core.spring.client;

import com.coconason.dtf.client.core.beans.group.BaseTransactionGroupInfo;
import com.coconason.dtf.client.core.beans.group.TransactionGroupInfo;
import com.coconason.dtf.client.core.nettyclient.protobufclient.NettyService;
import com.coconason.dtf.client.core.thread.ThreadLockCacheProxy;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RestClientAsyncTest {
    
    @Test(expected = InterruptedException.class)
    public void testSendPost() throws Exception {
        setEnv();
        RestClientAsync restClientAsync = createRestClientAsync();
        restClientAsync.sendPost("http://test-url", new Object());
        BaseTransactionGroupInfo transactionGroupInfo = TransactionGroupInfo.getCurrent();
        Set<Long> actual = transactionGroupInfo.getGroupMembers();
        assertEquals(2, actual.size());
    }

    private void setEnv() {
        Set<Long> setMembers = new HashSet<Long>();
        setMembers.add(1L);
        TransactionGroupInfo transactionGroupInfo = new TransactionGroupInfo("1234", 1L, setMembers);
        TransactionGroupInfo.setCurrent(transactionGroupInfo);
    }

    private RestClientAsync createRestClientAsync() throws NoSuchFieldException, IllegalAccessException {
        RestClientAsync result = new RestClientAsync();
        Field field = result.getClass().getDeclaredField("thirdThreadLockCacheProxy");
        field.setAccessible(true);
        field.set(result, new ThreadLockCacheProxy());
        Field field2 = result.getClass().getDeclaredField("nettyService");
        field2.setAccessible(true);
        NettyService nettyService = mock(NettyService.class);
        field2.set(result, nettyService);
        return result;
    }
    
}

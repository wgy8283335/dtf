package com.dtf.client.core.beans.service;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionServiceInfoTest {
    
    @Test
    public void assertCurrent() {
        BaseTransactionServiceInfo actual = createService();
        BaseTransactionServiceInfo.setCurrent(actual);
        assertEquals("testValue", BaseTransactionServiceInfo.getCurrent().getInfo().get("testKey"));
        assertEquals(MessageProto.Message.ActionType.ADD, BaseTransactionServiceInfo.getCurrent().getAction());
        assertEquals("1585529403579", BaseTransactionServiceInfo.getCurrent().getId());
    }

    @Test
    public void assertGetId() {
        BaseTransactionServiceInfo actual = createService();
        assertEquals("1585529403579", actual.getId());
    }
    
    @Test
    public void assertGetInfo() {
        BaseTransactionServiceInfo actual = createService();
        assertEquals("testValue", actual.getInfo().get("testKey"));
    }

    @Test
    public void assertGetAction() {
        BaseTransactionServiceInfo actual = createService();
        assertEquals(MessageProto.Message.ActionType.ADD, actual.getAction());
    }

    private BaseTransactionServiceInfo createService() {
        JSONObject map = new JSONObject();
        map.put("testKey", "testValue");
        BaseTransactionServiceInfo result = new TransactionServiceInfo("1585529403579", map, MessageProto.Message.ActionType.ADD);
        return result;
    }
    
}

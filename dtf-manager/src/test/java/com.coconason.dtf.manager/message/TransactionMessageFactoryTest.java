package com.dtf.manager.message;

import com.alibaba.fastjson.JSONObject;
import com.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.dtf.common.protobuf.MessageProto.Message.newBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransactionMessageFactoryTest {
    
    @Test
    public void testGetMessageForSubmitInstance() {
        Set set = createSet();
        MessageProto.Message message = createMessageForSubmitInstance(set);
        TransactionMessageGroupInterface actual =  TransactionMessageFactory.getMessageForSubmitInstance(message);
        assertEquals(2, actual.getMemberSet().size());
        assertEquals("123", actual.getGroupId());
    }
    
    @Test
    public void testGetMessageGroupInstance() {
        MessageProto.Message message = createMessageForGroupInstance();
        TransactionMessageGroupInterface actual =  TransactionMessageFactory.getMessageGroupInstance(message, null);
        assertTrue(actual.getMemberSet().contains("1"));
        assertEquals("123", actual.getGroupId());
    }
    
    @Test
    public void testMessageGroupAsyncInstance() {
        MessageProto.Message message = createMessageGroupAsyncInstance();
        TransactionMessageGroupInterface actual =  TransactionMessageFactory.getMessageGroupAsyncInstance(message);
        assertEquals(1, actual.getMemberSet().size());
        assertEquals("123", actual.getGroupId());
    }
    
    private MessageProto.Message createMessageForSubmitInstance(Set set) {
        MessageProto.Message.Builder builder = newBuilder();
        JSONObject map = new JSONObject();
        map.put("groupId", "123");
        map.put("groupMemberSet", set);
        builder.setInfo(map.toJSONString());
        MessageProto.Message result = builder.build();
        return result;
    }
    
    private MessageProto.Message createMessageForGroupInstance() {
        MessageProto.Message.Builder builder = newBuilder();
        JSONObject map = new JSONObject();
        map.put("groupId", "123");
        map.put("groupMemberId", "1");
        map.put("method", "");
        map.put("args", "");
        builder.setInfo(map.toJSONString());
        MessageProto.Message result = builder.build();
        return result;
    }
    
    private MessageProto.Message createMessageGroupAsyncInstance() {
        MessageProto.Message.Builder builder = newBuilder();
        JSONObject map = new JSONObject();
        map.put("groupId", "123");
        map.put("groupMemberId", "1");
        map.put("url", "http://localhost");
        map.put("obj", "");
        map.put("httpAction", "get");
        builder.setInfo(map.toJSONString());
        MessageProto.Message result = builder.build();
        return result;
    }
    
    private Set createSet() {
        Set set = new HashSet();
        set.add(1);
        set.add(1837232);
        return set;
    }
    
}

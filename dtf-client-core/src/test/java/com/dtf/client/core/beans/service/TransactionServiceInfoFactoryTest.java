package com.dtf.client.core.beans.service;

import com.dtf.common.protobuf.MessageProto;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class TransactionServiceInfoFactoryTest {
    
    @Test
    public void assertNewInstanceForRestful() {
        Object obj = new Object();
        BaseTransactionServiceInfo actual = TransactionServiceInfoFactory.newInstanceForRestful("af6c64b293494e54b33ea548ea441900", MessageProto.Message.ActionType.ADD_ASYNC, 
                "6931353512664104961585297947332", 1585529403571L, "http://localhost:8080/test", obj, "post");
        assertThat(actual.getId(), is("af6c64b293494e54b33ea548ea441900"));
        assertThat(actual.getAction(), is(MessageProto.Message.ActionType.ADD_ASYNC));
        assertThat(actual.getInfo().get("groupId"), is("6931353512664104961585297947332"));
        assertThat(actual.getInfo().get("groupMemberId"), is(1585529403571L));
        assertThat(actual.getInfo().get("url"), is("http://localhost:8080/test"));
        assertThat(actual.getInfo().get("obj"), is(obj));
        assertThat(actual.getInfo().get("httpAction"), is("post"));
    }
    
    @Test
    public void assertNewInstanceForSyncAdd() throws ClassNotFoundException, NoSuchMethodException {
        Method method = getMethod();
        Object[] array = {new Object(), new Object()};
        BaseTransactionServiceInfo actual = TransactionServiceInfoFactory.newInstanceForSyncAdd("af6c64b293494e54b33ea548ea441900", MessageProto.Message.ActionType.ADD,
                "6931353512664104961585297947332", 1585529403571L, method, array);
        assertThat(actual.getId(), is("af6c64b293494e54b33ea548ea441900"));
        assertThat(actual.getAction(), is(MessageProto.Message.ActionType.ADD));
        assertThat(actual.getInfo().get("groupId"), is("6931353512664104961585297947332"));
        assertThat(actual.getInfo().get("groupMemberId"), is(1585529403571L));
        assertThat(actual.getInfo().get("method"), is(method));
        assertThat(actual.getInfo().get("args"), is(array));
    }
    
    @Test
    public void assertNewInstanceForAsyncCommit() {
        Set<Long> groupMemberSet = getGroupMemeberSet();
        BaseTransactionServiceInfo actual = TransactionServiceInfoFactory.newInstanceForAsyncCommit("af6c64b293494e54b33ea548ea441900", MessageProto.Message.ActionType.ASYNC_COMMIT,
                "6931353512664104961585297947332", groupMemberSet);
        assertThat(actual.getId(), is("af6c64b293494e54b33ea548ea441900"));
        assertThat(actual.getAction(), is(MessageProto.Message.ActionType.ASYNC_COMMIT));
        assertThat(actual.getInfo().get("groupId"), is("6931353512664104961585297947332"));
        assertThat(actual.getInfo().get("groupMemberSet"), is(groupMemberSet.toString()));
    }
    
    @Test
    public void assertNewInstanceWithGroupIdSet() {
        Set<Long> groupMemberSet = getGroupMemeberSet();
        BaseTransactionServiceInfo actual = TransactionServiceInfoFactory.newInstanceWithGroupIdSet("af6c64b293494e54b33ea548ea441900", MessageProto.Message.ActionType.APPLYFORSUBMIT,
                "6931353512664104961585297947332", groupMemberSet);
        assertThat(actual.getId(), is("af6c64b293494e54b33ea548ea441900"));
        assertThat(actual.getAction(), is(MessageProto.Message.ActionType.APPLYFORSUBMIT));
        assertThat(actual.getInfo().get("groupId"), is("6931353512664104961585297947332"));
        assertThat(actual.getInfo().get("groupMemberSet"), is(groupMemberSet.toString()));
    }
    
    @Test
    public void assertNewInstanceForSub() {
        Set<Long> groupMemberSet = getGroupMemeberSet();
        BaseTransactionServiceInfo actual = TransactionServiceInfoFactory.newInstanceForSub("af6c64b293494e54b33ea548ea441900", MessageProto.Message.ActionType.SUB_SUCCESS,
                "6931353512664104961585297947332", groupMemberSet, 1585529403571L);
        assertThat(actual.getId(), is("af6c64b293494e54b33ea548ea441900"));
        assertThat(actual.getAction(), is(MessageProto.Message.ActionType.SUB_SUCCESS));
        assertThat(actual.getInfo().get("groupId"), is("6931353512664104961585297947332"));
        assertThat(actual.getInfo().get("groupMemberSet"), is(groupMemberSet.toString()));
        assertThat(actual.getInfo().get("memberId"), is("1585529403571"));
    }

    @Test
    public void assertNewInstanceForShortMessage() {
        Set<Long> groupMemberSet = getGroupMemeberSet();
        BaseTransactionServiceInfo actual = TransactionServiceInfoFactory.newInstanceForShortMessage("af6c64b293494e54b33ea548ea441900", MessageProto.Message.ActionType.WHOLE_SUCCESS_STRONG_ACK,
                "6931353512664104961585297947332");
        assertThat(actual.getId(), is("af6c64b293494e54b33ea548ea441900"));
        assertThat(actual.getAction(), is(MessageProto.Message.ActionType.WHOLE_SUCCESS_STRONG_ACK));
        assertThat(actual.getInfo().get("groupId"), is("6931353512664104961585297947332"));
    }

    private Method getMethod() throws ClassNotFoundException, NoSuchMethodException {
        Class methodClass = Class.forName("com.dtf.client.core.beans.service.TransactionServiceInfoFactory");
        Method result = methodClass.getMethod("newInstanceForSyncAdd", String.class, MessageProto.Message.ActionType.class,
                String.class, Long.class, Method.class, Object[].class);
        return result;
    }

    private Set<Long> getGroupMemeberSet() {
        Set<Long> result = new HashSet<>();
        result.add(1L);
        result.add(1585529403572L);
        return result;
    }
    
}

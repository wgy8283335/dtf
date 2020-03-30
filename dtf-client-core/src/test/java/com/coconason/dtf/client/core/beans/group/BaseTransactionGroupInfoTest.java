package com.coconason.dtf.client.core.beans.group;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

public class BaseTransactionGroupInfoTest {
    
    @Test
    public void assertCurrent() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        BaseTransactionGroupInfo.setCurrent(actual);
        assertThat(BaseTransactionGroupInfo.getCurrent().getGroupMembers().size(), is(3));
        assertThat(BaseTransactionGroupInfo.getCurrent().getMemberId(), is(1585529403572L));
        assertThat(BaseTransactionGroupInfo.getCurrent().getGroupId(), is("6931353512664104961585297947332"));
    }
    
    @Test
    public void assertAddNewMember() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        actual.addNewMember();
        assertThat(actual.getGroupMembers().size(), is(4));
    }
    
    @Test
    public void assertAddMembers() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        Set<Long> members = createTwoMembers();
        actual.addMembers(members);
        assertThat(actual.getGroupMembers().size(), is(5));
    }
    
    @Test
    public void assertGetMemberId() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        assertThat(actual.getMemberId(), is(1585529403572L));
    }
    
    @Test
    public void assertGetGroupId() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        assertThat(actual.getGroupId(), is("6931353512664104961585297947332"));
    }
    
    @Test
    public void assertGetMembers() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        assertTrue(actual.getGroupMembers().contains(1L));
        assertTrue(actual.getGroupMembers().contains(1585529403572L));
        assertTrue(actual.getGroupMembers().contains(1585529403573L));
    }
    
    @Test
    public void assertToString() {
        BaseTransactionGroupInfo actual = createTransactionGroup();
        assertEquals("6931353512664104961585297947332-1585529403572-1-1585529403573-1585529403572", actual.toString());
    }
    
    private BaseTransactionGroupInfo createTransactionGroup() {
        Set<Long> groupMembers = new HashSet<>();
        groupMembers.add(1L);
        groupMembers.add(1585529403572L);
        groupMembers.add(1585529403573L);
        BaseTransactionGroupInfo result = new TransactionGroupInfo("6931353512664104961585297947332", 1585529403572L, groupMembers);
        return result;
    }
    
    private Set<Long> createTwoMembers() {
        Set<Long> result = new HashSet<>();
        result.add(1585529403574L);
        result.add(1585529403575L);
        return result;
    }
    
}

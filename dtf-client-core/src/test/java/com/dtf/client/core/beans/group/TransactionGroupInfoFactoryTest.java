package com.dtf.client.core.beans.group;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TransactionGroupInfoFactoryTest {
    
    @Test
    public void assertGetInstance() {
        BaseTransactionGroupInfo actual = TransactionGroupInfoFactory.getInstance("6931353512664104961585297947332", 1585529403571L);
        assertThat(actual.getGroupMembers().size(), is(1));
        assertThat(actual.getGroupId(), is("6931353512664104961585297947332"));
        assertThat(actual.getMemberId(), is(1585529403571L));
    }

    @Test
    public void assertGetInstanceByParsingString() {
        BaseTransactionGroupInfo actual = TransactionGroupInfoFactory.getInstanceByParsingString("6931353512664104961585297947332-1585529403571-1585529403571");
        assertThat(actual.getGroupMembers().size(), is(1));
        assertThat(actual.getGroupId(), is("6931353512664104961585297947332"));
        assertThat(actual.getMemberId(), is(1585529403571L));
    }
    
}

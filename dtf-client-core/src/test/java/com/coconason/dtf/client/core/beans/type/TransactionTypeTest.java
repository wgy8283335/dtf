package com.coconason.dtf.client.core.beans.type;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TransactionTypeTest {
    
    @Test
    public void assertNewInstance() {
        TransactionType type = TransactionType.newInstance("SYNC_FINAL");
        assertThat(type, is(TransactionType.SYNC_FINAL));
    }

    @Test
    public void assertCurrent() {
        TransactionType type = TransactionType.newInstance("SYNC_STRONG");
        TransactionType.setCurrent(type);
        assertThat(type, is(TransactionType.getCurrent().SYNC_STRONG));
    }

}

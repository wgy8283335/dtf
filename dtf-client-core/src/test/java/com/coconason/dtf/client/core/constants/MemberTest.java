package com.coconason.dtf.client.core.constants;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MemberTest {
    
    @Test
    public void assertOriginalId() {
        assertThat(Member.ORIGINAL_ID,is(1L));
    }
}

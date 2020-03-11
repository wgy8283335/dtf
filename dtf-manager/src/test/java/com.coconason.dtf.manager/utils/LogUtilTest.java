package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@FixMethodOrder(MethodSorters.DEFAULT)
public class LogUtilTest {
    
    @Test
    public void assertAppend(){
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = new MessageInfo("1", true, "http://localhost:8080/test", new Object(), System.currentTimeMillis());
        int position1 = logUtil.append(messageInfo1);
        MessageInfoInterface messageInfo2 = new MessageInfo("2", true, "http://localhost:8082/test", new Object(), System.currentTimeMillis());
        int position2 = logUtil.append(messageInfo2);
        assertThat(position1,is(0));
        assertThat(position2,is(92));
        assertThat(logUtil.getLength(),is(184L));
    }
    
    @Test
    public void assertGetInstanceTwice() {
        LogUtil logUtil1 = LogUtil.getInstance();
        LogUtil logUti2 = LogUtil.getInstance();
        assertThat(logUtil1, is(logUti2));
    }
    
    @Test
    public void assertGet() {
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = logUtil.get(0);
        MessageInfoInterface messageInfo2 = logUtil.get(92);
        assertThat(messageInfo1.getUrl(), is("http://localhost:8080/test"));
        assertThat(messageInfo1.getGroupMemberId(), is("1"));
        assertThat(messageInfo2.getUrl(), is("http://localhost:8082/test"));
        assertThat(messageInfo2.getGroupMemberId(), is("2"));
    }
    
    @Test
    public void assertPut(){
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo2 = new MessageInfo("2", false, "http://localhost:8082/test", new Object(), System.currentTimeMillis());
        messageInfo2.setPosition(92);
        logUtil.updateCommitStatus(messageInfo2);
        MessageInfoInterface messageInfo22 = logUtil.get(92);
        assertThat(messageInfo2.isCommitted(), is(messageInfo22.isCommitted()));
    }

    @Test
    public void get() {
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = logUtil.get(0);
        MessageInfoInterface messageInfo2 = logUtil.get(92);
        assertThat(messageInfo1.getUrl(), is("http://localhost:8080/test"));
        assertThat(messageInfo1.getGroupMemberId(), is("1"));
        assertThat(messageInfo2.getUrl(), is("http://localhost:8082/test"));
        assertThat(messageInfo2.getGroupMemberId(), is("2"));
    }
    
    @Test
    public void happend(){
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = new MessageInfo("1", true, "http://localhost:8080/test", new Object(), System.currentTimeMillis());
        int position1 = logUtil.append(messageInfo1);
        MessageInfoInterface messageInfo2 = new MessageInfo("2", true, "http://localhost:8082/test", new Object(), System.currentTimeMillis());
        int position2 = logUtil.append(messageInfo2);
        assertThat(position1,is(0));
        assertThat(position2,is(92));
        assertThat(logUtil.getLength(),is(184L));
    }
    
}

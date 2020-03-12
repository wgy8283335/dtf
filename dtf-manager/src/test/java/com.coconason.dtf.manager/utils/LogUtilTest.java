package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.log.LogUtil;
import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogUtilTest {
    
    @Test
    public void assertAGet() {
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = logUtil.get(0);
        MessageInfoInterface messageInfo2 = logUtil.get(90);
        assertThat(messageInfo1.getUrl(), is("http://localhost:8080/test"));
        assertThat(messageInfo1.getGroupMemberId(), is("1"));
        assertThat(messageInfo2.getUrl(), is("http://localhost:8082/test"));
        assertThat(messageInfo2.getGroupMemberId(), is("2"));
    }
    
    @Test
    public void assertAppend(){
        LogUtil logUtil = LogUtil.getInstance();
        int position = LogUtil.getInstance().getPosition();
        MessageInfoInterface messageInfo1 = new MessageInfo("1", true, "http://localhost:8080/test", new Object(), System.currentTimeMillis());
        int position1 = logUtil.append(messageInfo1);
        MessageInfoInterface messageInfo2 = new MessageInfo("2", true, "http://localhost:8082/test", new Object(), System.currentTimeMillis());
        int position2 = logUtil.append(messageInfo2);
        assertThat(position1,is(position));
        assertThat(position2,is(position+90));
    }
    
    @Test
    public void assertGetInstanceTwice() {
        LogUtil logUtil1 = LogUtil.getInstance();
        LogUtil logUtil2 = LogUtil.getInstance();
        assertThat(logUtil1, is(logUtil2));
    }
    
    @Test
    public void assertPut(){
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo2 = new MessageInfo("2", false, "http://localhost:8082/test", new Object(), System.currentTimeMillis());
        messageInfo2.setPosition(90);
        logUtil.updateCommitStatus(messageInfo2);
        MessageInfoInterface messageInfo22 = logUtil.get(90);
        assertThat(messageInfo2.isCommitted(), is(messageInfo22.isCommitted()));
        assertThat(messageInfo2.getGroupMemberId(), is(messageInfo22.getGroupMemberId()));
    }
    
    @Test
    public void assertQGet() {
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = logUtil.get(0);
        MessageInfoInterface messageInfo2 = logUtil.get(90);
        assertThat(messageInfo1.getUrl(), is("http://localhost:8080/test"));
        assertThat(messageInfo1.getGroupMemberId(), is("1"));
        assertThat(messageInfo2.getUrl(), is("http://localhost:8082/test"));
        assertThat(messageInfo2.getGroupMemberId(), is("2"));
        assertThat(messageInfo2.isCommitted(), is(false));
    }
    
}

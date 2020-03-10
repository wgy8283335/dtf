package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogUtilTest {
    
    @Test
    public void append(){
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo = new MessageInfo("1", true, "http://localhost:8080/test", new Object(), System.currentTimeMillis());
        int position = logUtil.append(messageInfo);
        MessageInfoInterface messageInfo2 = new MessageInfo("2", true, "http://localhost:8082/test", new Object(), System.currentTimeMillis());
        int position2 = logUtil.append(messageInfo2);
        //assertThat(position2,is(2048));
    }

    @Test
    public void assertGetInstanceTwice() {
        LogUtil.getInstance();
    }
    
    @Test
    public void get() {
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo = logUtil.get(231);
        return;
    }
    
    @Test
    public void getLength(){
        LogUtil logUtil = LogUtil.getInstance();
        logUtil.getLength();
    }
    
    @Test
    public void put(){
    }
    
}

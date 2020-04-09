package com.coconason.dtf.manager.log;

import com.coconason.dtf.manager.log.LogUtil;
import com.coconason.dtf.manager.log.LogUtilForSyncApproveSubmit;
import com.coconason.dtf.manager.log.LogUtilForSyncFinalSuspend;
import com.coconason.dtf.manager.message.MessageInfo;
import com.coconason.dtf.manager.message.MessageInfoInterface;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogUtilTest {
    
    @Test
    public void assertAppend(){
        LogUtil logUtil = LogUtil.getInstance();
        logUtil.initializeMetadataPosition();
        MessageInfoInterface messageInfo1 = new MessageInfo("1", true, "http://localhost:8080/test", new Object(), System.currentTimeMillis(), "post");
        int position1 = logUtil.append(messageInfo1);
        MessageInfoInterface messageInfo2 = new MessageInfo("2", true, "http://localhost:8082/test", new Object(), System.currentTimeMillis(), "post");
        int position2 = logUtil.append(messageInfo2);
        assertThat(position1,is(0));
        assertThat(position2,is(110));
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
        MessageInfoInterface messageInfo2 = new MessageInfo("1", false, "http://localhost:8080/test", new Object(), System.currentTimeMillis(), "post");
        messageInfo2.setPosition(0);
        logUtil.updateCommitStatus(messageInfo2);
        MessageInfoInterface messageInfo22 = logUtil.get(0);
        assertThat(messageInfo2.isCommitted(), is(messageInfo22.isCommitted()));
    }
    
    @Test
    public void assertQAppend(){
        LogUtil logUtil = LogUtil.getInstance();
        logUtil.initializeMetadataPosition();
        MessageInfoInterface messageInfo1 = new MessageInfo("3", true, "http://localhost:8083/test", new Object(), System.currentTimeMillis(), "post");
        int position1 = logUtil.append(messageInfo1);
        MessageInfoInterface messageInfo2 = new MessageInfo("4", true, "http://localhost:8084/test", new Object(), System.currentTimeMillis(), "post");
        int position2 = logUtil.append(messageInfo2);
        assertThat(position1,is(220));
        assertThat(position2,is(330));
    }
    
    @Test
    public void assertQGet() {
        LogUtil logUtil = LogUtil.getInstance();
        MessageInfoInterface messageInfo1 = logUtil.get(0);
        MessageInfoInterface messageInfo2 = logUtil.get(110);
        assertThat(messageInfo1.getUrl(), is("http://localhost:8080/test"));
        assertThat(messageInfo1.getGroupMemberId(), is("1"));
        assertThat(messageInfo2.getUrl(), is("http://localhost:8082/test"));
        assertThat(messageInfo2.getGroupMemberId(), is("2"));
        assertThat(messageInfo1.isCommitted(), is(false));
    }
    
    @Test
    public void assertQMultiThread(){
        LogUtil logUtil = LogUtil.getInstance();
        ExecutorService executorService =  Executors.newCachedThreadPool();
        for(int i = 0; i < 50; i++) {
            executorService.execute(new LogRunnable(logUtil));
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MessageInfoInterface messageInfo = logUtil.get(110*50*100);
        assertThat(messageInfo.getUrl(), is("http://localhost:8083/test"));
        assertThat(messageInfo.isCommitted(), is(true));
        assertThat(messageInfo.getHttpAction(), is("post"));
    }


    private class LogRunnable implements Runnable {

        LogUtil logUtil;

        public LogRunnable(LogUtil logUtil) {
            this.logUtil = logUtil;
        }

        @Override
        public void run() {
            for(int i = 0; i < 100; i++){
                logUtil.append(new MessageInfo(UUID.randomUUID().toString(), true, "http://localhost:8083/test", new Object(), System.currentTimeMillis(), "post"));
            }
        }
    }
    
}

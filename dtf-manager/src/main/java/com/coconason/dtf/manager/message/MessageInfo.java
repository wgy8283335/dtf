package com.coconason.dtf.manager.message;

import com.coconason.dtf.manager.service.ConsumerFailingAsyncRequestRunnable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Message information.
 * 
 * @Author: Jason
 */
public final class MessageInfo implements MessageInfoInterface {
    
    private static final long serialVersionUID = -1L;

    /**
     * Logger of ConsumerFailingAsyncRequestRunnable class.
     */
    private final Logger logger = LoggerFactory.getLogger(MessageInfo.class);
    
    /**
     * Member id.
     */
    private String memberId;
    
    /**
     * Url of request.
     */
    private String url;
    
    /**
     * Parameter of request.
     */
    private Object obj;
    
    /**
     * Time stamp of request.
     */
    private long timeStamp;
    
    /**
     * Whether is committed.
     */
    private boolean isCommitted;
    
    /**
     * Position in log.
     */
    private int position;
    
    public MessageInfo(final String memberId, final boolean isCommitted, final String url, final Object obj, final long timeStamp) {
        this.memberId = memberId;
        this.isCommitted = isCommitted;
        this.url = url;
        this.obj = obj;
        this.timeStamp = timeStamp;
    }
    
    /**
     * Get time stamp of group.
     * @return time stamp
     */
    @Override
    public long getTimeStamp() {
        return timeStamp;
    }
    
    /**
     * Set position in log.
     * @return time stamp
     */
    @Override
    public void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * Get position in log.
     * @return time stamp
     */
    @Override
    public int getPosition() {
        return position;
    }
    
    /**
     * Get member id of group.
     * @return member id
     */
    @Override
    public String getGroupMemberId() {
        return memberId;
    }
    
    /**
     * Check whether is committed.
     * @return Whether is committed
     */
    @Override
    public Boolean isCommitted() {
        return isCommitted;
    }
    
    /**
     * Get url of request.
     * 
     * @return url of request
     */
    @Override
    public String getUrl() {
        return url;
    }
    
    /**
     * Get parameter of request.
     * 
     * @return parameter of request
     */
    @Override
    public Object getObj() {
        return obj;
    }

    /**
     * Set the value of isCommitted.
     * 
     * @param committed boolean
     */
    @Override
    public void setCommitted(final boolean committed) {
        isCommitted = committed;
    }

    /**
     * Override equals method.
     * 
     * @param o MessageInfo object
     * @return true or false
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageInfo that = (MessageInfo) o;
        if (isCommitted != that.isCommitted) {
            return false;
        }
        if (!memberId.equals(that.memberId)) {
            return false;
        }
        if (!getUrl().equals(that.getUrl())) {
            return false;
        }
        return getObj().equals(that.getObj());
    }
    
    /**
     * Override hashCode method.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = memberId.hashCode();
        result = 31 * result + (isCommitted ? 1 : 0);
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getObj().hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "{" +
                "memberId:" + memberId + 
                ", url:" + url +
                ", obj:" + obj +
                ", timeStamp:" + timeStamp +
                ", isCommitted:" + isCommitted +
                "}";
    }
    
    @Override
    public byte[] toBytes() throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        byte[] result=null;
        try {
            oos.writeObject(this);
            oos.flush();
            result = bos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } finally {
            bos.close();
            oos.close();
        }
        return result;
    }
    
}

package com.dtf.client.core.nettyclient.sender;

/**
 * Interface of message sender.
 * 
 * @author wangguangyuan
 */
public interface MessageSenderInterface {
    
    /**
     * Send message.
     * 
     * @throws InterruptedException interrrupted exception
     */
    void startSendMessage() throws InterruptedException;
}

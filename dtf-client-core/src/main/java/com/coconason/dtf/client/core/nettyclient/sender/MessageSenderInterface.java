package com.coconason.dtf.client.core.nettyclient.sender;

/**
 * Interface of message sender.
 * 
 * @Author: Jason
 */
public interface MessageSenderInterface {
    
    /**
     * Send message.
     * 
     * @throws InterruptedException interrrupted exception
     */
    void startSendMessage() throws InterruptedException;
}

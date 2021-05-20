package com.dtf.manager.cache;

import com.dtf.manager.message.MessageInfoInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Queue of asynchronous message.
 * 
 * @Author: wangguangyuan
 */
public final class MessageAsyncQueueProxy implements Queue {
    
    /**
     * Logger for MessageAsyncQueueProxy.
     */
    private Logger logger = LoggerFactory.getLogger(MessageAsyncQueueProxy.class);
    
    /**
     * Queue of message information.
     */
    private LinkedBlockingQueue<MessageInfoInterface> messageQueue;
    
    public MessageAsyncQueueProxy() {
        messageQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }
    
    /**
     * Whether queue is empty.
     * 
     * @return boolean
     */
    @Override
    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }
    
    /**
     * Put element in the queue. If space of queue is not enough, wait.
     * 
     * @param o message information interface
     * @return whether success
     */
    @Override
    public boolean add(final Object o) {
        try {
            messageQueue.put((MessageInfoInterface) o);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Put element in the queue. If space of queue is not enough, return false.
     * 
     * @param o element
     * @return whether success
     */
    @Override
    public boolean offer(final Object o) {
        return messageQueue.offer((MessageInfoInterface) o);
    }
    
    /**
     * Acquire header element of queue. If queue is empty, wait.
     * 
     * @return message information
     */
    @Override
    public MessageInfoInterface poll() {
        MessageInfoInterface result;
        try {
            result = messageQueue.take();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            return null;
        }
        return result;
    }
    
    /**
     * Acquire header element of queue and remove. 
     * If queue is empty, throw no such element exception.
     *
     * @return element
     */
    @Override
    public Object remove() {
        return messageQueue.remove();
    }
    
    @Override
    public boolean remove(final Object o) {
        return messageQueue.remove(o);
    }
    
    @Override
    public Object element() {
        return messageQueue.element();
    }
    
    @Override
    public Object peek() {
        return messageQueue.peek();
    }
    
    @Override
    public void clear() {
        messageQueue.clear();
    }
    
    @Override
    public int size() {
        return messageQueue.size();
    }
    
    @Override
    public boolean contains(final Object o) {
        return messageQueue.contains(o);
    }
    
    @Override
    public Iterator iterator() {
        return messageQueue.iterator();
    }
    
    @Override
    public Object[] toArray() {
        return messageQueue.toArray();
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        return messageQueue.toArray();
    }
    
    @Override
    public boolean containsAll(final Collection c) {
        return messageQueue.containsAll(c);
    }
    
    @Override
    public boolean addAll(final Collection c) {
        return messageQueue.addAll(c);
    }
    
    @Override
    public boolean removeAll(final Collection c) {
        return messageQueue.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection c) {
        return messageQueue.retainAll(c);
    }
    
}

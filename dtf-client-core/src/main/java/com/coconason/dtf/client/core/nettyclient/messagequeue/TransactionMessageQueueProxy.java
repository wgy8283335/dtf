package com.coconason.dtf.client.core.nettyclient.messagequeue;

import com.coconason.dtf.client.core.beans.BaseTransactionServiceInfo;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Linked blocking queue storing base transaction service information.
 * 
 * @Author: Jason
 */
@Component
public final class TransactionMessageQueueProxy implements Queue {

    private final LinkedBlockingQueue<BaseTransactionServiceInfo> messageQueue;

    public TransactionMessageQueueProxy() {
        messageQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }

    /**
     * Check whether the queue is empty.
     * 
     * @return If success, return true. Else, return false
     */
    @Override
    public boolean isEmpty() {
        return messageQueue.isEmpty();
    }

    /**
     * Add base transaction service information into the queue.
     * Blocking method.
     * 
     * @param o base transaction service information
     * @return If success, return true. Else, return false
     */
    @Override
    public boolean add(final Object o) {
        try {
            messageQueue.put((BaseTransactionServiceInfo) o);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Add base transaction service information to queue.
     * Non blocking method.
     * 
     * @param o base transaction service information
     * @return If success, return true. Else, return false
     */
    @Override
    public boolean offer(final Object o) {
        return messageQueue.offer((BaseTransactionServiceInfo) o);
    }

    /**
     * Remove a base transaction service information from queue.
     * Non blocking method.
     * 
     * @return base transaction service information
     */
    @Override
    public Object remove() {
        return messageQueue.remove();
    }
        
    @Override
    public boolean remove(final Object o) {
        return messageQueue.remove(o);
    }
    
    /**
     * Get first base transaction service information from queue.
     * Blocking method.
     *
     * @return base transaction service information
     */
    @Override
    public BaseTransactionServiceInfo poll() {
        BaseTransactionServiceInfo result;
        try { 
            result = messageQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return result;
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

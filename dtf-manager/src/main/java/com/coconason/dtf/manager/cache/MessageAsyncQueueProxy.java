package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.MessageInfoInterface;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author: Jason
 * @date: 2018/9/6-12:15
 */
public class MessageAsyncQueueProxy implements Queue {
    private LinkedBlockingQueue<MessageInfoInterface> messageQueue;

    public MessageAsyncQueueProxy() {
        messageQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean isEmpty(){
        return messageQueue.isEmpty();
    }

    @Override
    public boolean add(Object o){
        try{
            messageQueue.put((MessageInfoInterface)o);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean offer(Object o) {
        return messageQueue.offer((MessageInfoInterface)o);
    }

    @Override
    public Object remove() {
        return messageQueue.remove();
    }

    @Override
    public MessageInfoInterface poll() {
        MessageInfoInterface result;
        try{
            result=messageQueue.take();
        }catch (Exception e){
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
    public boolean contains(Object o) {
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
    public Object[] toArray(Object[] a) {
        return messageQueue.toArray();
    }

    @Override
    public boolean remove(Object o) {
        return messageQueue.remove(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return messageQueue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        return messageQueue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return messageQueue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return messageQueue.retainAll(c);
    }
}

package com.coconason.dtf.client.core.dbconnection;

import com.coconason.dtf.client.core.utils.UuidGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:09
 */
@Component
public class ThreadsInfo {
    private final ConcurrentHashMap<String,LockAndCondition> threadInfoMap = new ConcurrentHashMap<>();

    public void put(String key,LockAndCondition lc){
        threadInfoMap.put(key,lc);
    }

    public LockAndCondition get(String key){
        return threadInfoMap.get(key);
    }

}

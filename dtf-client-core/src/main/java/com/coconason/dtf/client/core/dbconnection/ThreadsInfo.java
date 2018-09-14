package com.coconason.dtf.client.core.dbconnection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jason
 * @date: 2018/8/22-11:09
 */
@Component(value="threadsInfo")
@Scope(value="prototype")
public class ThreadsInfo {
    private final ConcurrentHashMap<String,LockAndCondition> threadInfoMap = new ConcurrentHashMap<>();

    public void put(String key,LockAndCondition lc){
        threadInfoMap.put(key,lc);
    }

    public LockAndCondition get(String key){
        return threadInfoMap.get(key);
    }

}

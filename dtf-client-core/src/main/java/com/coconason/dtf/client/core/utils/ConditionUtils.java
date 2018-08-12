package com.coconason.dtf.client.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Jason
 * @date: 2018/8/10-11:02
 */
public class ConditionUtils {



    private static ConditionUtils instance = null;

    private Map<String, Task> taskMap = new ConcurrentHashMap<String, Task>();


    private ConditionUtils(){

    }

    public static ConditionUtils getInstance() {
        if (instance == null) {
            synchronized (ConditionUtils.class) {
                if(instance==null){
                    instance = new ConditionUtils();
                }
            }
        }
        return instance;
    }



    public Task createTask(String key) {
        Task task = new Task();
        task.setKey(key);
        taskMap.put(key, task);
        return task;
    }


    public Task getTask(String key) {
        return taskMap.get(key);
    }


    public void removeKey(String key) {
        if (StringUtils.isNotEmpty(key)) {
            taskMap.remove(key);
        }
    }

    public boolean hasKey(String key) {
        return taskMap.containsKey(key);
    }
}

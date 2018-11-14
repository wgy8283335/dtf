package com.coconason.dtf.manager.cache;

import com.coconason.dtf.manager.message.TransactionMessageGroupInterface;

/**
 * @Author: Jason
 * @date: 2018/10/3-9:32
 */

public interface MessageCacheInterface {

    TransactionMessageGroupInterface get(String id);

    void put(String id, TransactionMessageGroupInterface msg);

    void putDependsOnCondition(TransactionMessageGroupInterface group);

    long getSize();

    void invalidate(Object id);


}

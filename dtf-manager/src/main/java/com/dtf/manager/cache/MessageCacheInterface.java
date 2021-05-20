package com.dtf.manager.cache;

import com.dtf.manager.message.TransactionMessageGroupInterface;

/**
 * Interface of management of message cache.
 * 
 * @author wangguangyuan
 */
public interface MessageCacheInterface {
    
    /**
     * Get TransactionMessageGroupInterface by group id.
     *
     * @param id group id
     * @return transaction message group
     */
    TransactionMessageGroupInterface get(String id);

    /**
     * Put group id and transaction message group in the cache.
     *
     * @param id group id
     * @param msg transaction message
     */
    void put(String id, TransactionMessageGroupInterface msg);

    /**
     * Put transaction message group in the cache.
     *
     * @param group transaction message group
     */
    void putDependsOnCondition(TransactionMessageGroupInterface group);

    /**
     * Get size of cache.
     *
     * @return size
     */
    long getSize();

    /**
     * Invalidate the id related element from the cache.
     *
     * @param id group id.
     */
    void invalidate(Object id);
    
}

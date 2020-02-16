package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfoInterface;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility of set.
 * 
 * @Author: Jason
 */
public final class SetUtil {
    
    /**
     * Check whether set1 equals set2.
     * 
     * @param set1 set
     * @param set2 set
     * @return true or false
     */
    public static boolean isSetEqual(final Set set1, final Set set2) {
    
        if (set1 == null && set2 == null) {
            return true;
        }
        if (set1 == null || set2 == null) {
            return false;
        }
        if (set1.size() == 0 || set2.size() == 0 || set1.size() != set2.size()) {
            return false;
        }
        Iterator ite2 = set2.iterator();
        boolean isFullEqual = true;
        while (ite2.hasNext()) {
            if (!set1.contains(ite2.next())) {
                isFullEqual = false;
            }
        }
        return isFullEqual;
    }
    
    /**
     * Transfer set of MessageInfoInterface into set of group member id.
     * 
     * @param set set
     * @return set of group member id
     */
    public static Set setTransfer(final Set<MessageInfoInterface> set) { 
        Set<String> resultSet = new HashSet<String>();
        for (MessageInfoInterface messageInfo:set) {
            resultSet.add(messageInfo.getGroupMemberId());
        }
        return resultSet;
    }
}

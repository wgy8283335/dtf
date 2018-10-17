package com.coconason.dtf.manager.utils;

import com.coconason.dtf.manager.message.MessageInfoInterface;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/19-9:55
 */
public final class SetUtil {
    public static boolean isSetEqual(Set set1, Set set2) {

        if(set1 == null && set2 == null){
            //if both are null,return true.
            return true;
        }

        if (set1 == null || set2 == null || set1.size() != set2.size()
                || set1.size() == 0 || set2.size() == 0) {
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

    public static Set setTransfer(Set<MessageInfoInterface> set){
        Set<String> resultSet = new HashSet<String>();
        for (MessageInfoInterface messageInfo:set) {
            resultSet.add(messageInfo.getGroupMemberId());
        }
        return  resultSet;
    }
}

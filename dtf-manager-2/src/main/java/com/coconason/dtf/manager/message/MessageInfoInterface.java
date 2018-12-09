package com.coconason.dtf.manager.message;

/**
 * @Author: Jason
 * @date: 2018/9/6-13:14
 */
public interface MessageInfoInterface {

    String getGroupMemberId();

    Boolean isCommitted();

    String getUrl();

    Object getObj();

    void setCommitted(boolean committed);
}

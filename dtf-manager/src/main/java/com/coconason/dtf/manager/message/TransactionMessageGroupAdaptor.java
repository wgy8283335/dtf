package com.coconason.dtf.manager.message;

/**
 * @Author: Jason
 * @date: 2018/8/24-17:04
 */
public abstract class TransactionMessageGroupAdaptor implements TransactionMessageGroupInterface<String> {

    @Override
    public void addMember(String memberId, String url, Object obj) {
    }

    @Override
    public String getGroupMemberId(){
        return null;
    }

    @Override
    public void setCommitted(boolean commited){
    }
    @Override
    public void setGroupId(String groupId){

    }

    @Override
    public Boolean isCommitted() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Object getObj() {
        return null;
    }
}

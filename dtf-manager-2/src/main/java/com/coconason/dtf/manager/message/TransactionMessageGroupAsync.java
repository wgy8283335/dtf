package com.coconason.dtf.manager.message;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/9/6-11:54
 */
public final class TransactionMessageGroupAsync extends TransactionMessageGroupAsyncAdaptor {
    private String groupId;
   private Set<MessageInfoInterface> memberSet;

    TransactionMessageGroupAsync(String groupId) {
        this.groupId = groupId;
        this.memberSet = new HashSet<MessageInfoInterface>();
    }

    @Override
    public String getGroupId() {
        return groupId;
    }
    @Override
    public Set<MessageInfoInterface> getMemberSet() {
        return memberSet;
    }

    @Override
    public List<TransactionMessageForAddingInterface> getMemberList() {
        return null;
    }

    @Override
    public void addMember(String memberId,String url,Object obj){
        memberSet.add(new MessageInfo(memberId,false,url,obj));
    }

    @Override
    public boolean equals(Object o) {
        boolean result = true;
        if(o==null){
            return false;
        }
        TransactionMessageGroupAsync temp =  (TransactionMessageGroupAsync) o;
        if(temp.getGroupId()==null||this.getGroupId()==null||(!this.groupId.equals(temp.getGroupId()))){
            return false;
        }
        if(memberSet != null){
            memberSet.removeAll(temp.getMemberSet());
            if(!memberSet.isEmpty()){
                return false;
            }
        }else if(memberSet==null||temp.getMemberSet()==null){
            return false;
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = getGroupId() != null ? getGroupId().hashCode() : 0;
        result = 31 * result + (getMemberSet() != null ? getMemberSet().hashCode() : 0);
        return result;
    }
}

package com.coconason.dtf.manager.message;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.common.protobuf.MessageProto;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: Jason
 * @date: 2018/8/24-18:43
 */
public class TransactionMessageForSubmit {
    private String groupId;
    private Set<String> memberSet;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Set<String> getMemberSet() {
        return memberSet;
    }

    public void setMemberSet(Set<String> memberSet) {
        this.memberSet = memberSet;
    }

    public TransactionMessageForSubmit(MessageProto.Message message) {
        JSONObject info = JSONObject.parseObject(message.getInfo());
        String groupId = info.get("groupId").toString();
        this.groupId = groupId;
        String[] strArray = info.get("groupMemberSet").toString().replace("[","").replace("]","").split(", ");
        this.memberSet =  new HashSet<String>();
        CollectionUtils.addAll(this.memberSet,strArray);
    }
}

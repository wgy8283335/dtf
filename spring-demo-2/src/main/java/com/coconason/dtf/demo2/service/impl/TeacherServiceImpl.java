package com.coconason.dtf.demo2.service.impl;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.demo2.constant.ErrorCode;
import com.coconason.dtf.demo2.dao.TeacherMapper;
import com.coconason.dtf.demo2.model.DemoResult;
import com.coconason.dtf.demo2.po.Sc;
import com.coconason.dtf.demo2.po.Teacher;
import com.coconason.dtf.demo2.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:08
 */
@Service
public class TeacherServiceImpl implements ITeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private RestClient restClient;

    @Override
    //w@DtfTransaction(type="SYNC_STRONG")
    @DtfTransaction
    @Transactional
    public DemoResult addTeacherInfo(Teacher teacher) throws Exception {
        if(teacherMapper.insertSelective(teacher)>0){
            if(teacher.getT()==2){
                Sc sc = new Sc();
                sc.setC(5);
                sc.setS(7);
                sc.setScore(95);
                restClient.sendPost("http://localhost:8083/add_sc_info",sc);
            }
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(),ErrorCode.SYS_ERROR.msg());
        }
    }

    @Override
    @DtfTransaction(type="ASYNC_FINAL")
    @Transactional
    public DemoResult addTeacherInfoAsync(Teacher teacher) throws Exception {
        if(teacherMapper.insertSelective(teacher)>0){
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(),ErrorCode.SYS_ERROR.msg());
        }
    }
}

package com.coconason.dtf.manager.service.impl;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.manager.constant.ErrorCode;
import com.coconason.dtf.manager.dao.TeacherMapper;
import com.coconason.dtf.manager.model.DemoResult;
import com.coconason.dtf.manager.po.Sc;
import com.coconason.dtf.manager.po.Teacher;
import com.coconason.dtf.manager.po.TeacherExample;
import com.coconason.dtf.manager.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @DtfTransaction(type="SYNC_STRONG")
    @Transactional
    public DemoResult addTeacherInfoStrong(Teacher teacher) throws Exception {
        if(teacherMapper.insertSelective(teacher)>0){
            if(teacher.getT()==2){
                Sc sc = new Sc();
                sc.setC(5);
                sc.setS(7);
                sc.setScore(95);
                restClient.sendPost("http://localhost:8083/add_sc_info_strong",sc);
            }
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(),ErrorCode.SYS_ERROR.msg());
        }
    }

    @Override
    @Transactional
    public DemoResult addTeacherInfoAsync(Teacher teacher) throws Exception {
        if(teacherMapper.insertSelective(teacher)>0){
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(),ErrorCode.SYS_ERROR.msg());
        }
    }

    @Override
    @DtfTransaction
    @Transactional(readOnly=true)
    public Teacher getTeacherInfo(int id) throws Exception {
        TeacherExample teacherExample = new TeacherExample();
        TeacherExample.Criteria criteria = teacherExample.createCriteria();
        criteria.andTEqualTo(id);
        List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
        Teacher teacher = teacherList.get(0);
        return teacher;
    }
}

package com.coconason.dtf.demo2.service.impl;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.demo2.constant.ErrorCode;
import com.coconason.dtf.demo2.dao.TeacherMapper;
import com.coconason.dtf.demo2.model.DemoResult;
import com.coconason.dtf.demo2.po.Sc;
import com.coconason.dtf.demo2.po.Teacher;
import com.coconason.dtf.demo2.po.TeacherExample;
import com.coconason.dtf.demo2.service.ITeacherService;
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
    @Transactional
    public DemoResult addTeacherInfoWithoutDtf(Teacher teacher) throws Exception {
        if(teacherMapper.insertSelective(teacher)>0){
            //int kk =6/0;
            if(teacher.getT()==2){
                Sc sc = new Sc();
                sc.setC(teacher.getT());
                sc.setS(teacher.getT());
                sc.setScore(95);
                restClient.sendPost("http://localhost:8083/add_sc_info_without_dtf",sc);
            }
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(), ErrorCode.SYS_ERROR.msg());
        }
    }
    
    @Override
    @DtfTransaction
    @Transactional
    public DemoResult addTeacherInfo(Teacher teacher) {
        teacherMapper.insertSelective(teacher);
        Sc sc = new Sc();
        sc.setC(teacher.getT());
        sc.setS(teacher.getT());
        sc.setScore(95);
        String response = restClient.sendPost("http://localhost:8083/add_sc_info",sc);
        DemoResult result = new DemoResult(response);
        if(200 != result.getCode()) {
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(), ErrorCode.SYS_ERROR.msg());
        }
        teacher.setT(teacher.getT()*10);
        teacherMapper.insertSelective(teacher);
        return new DemoResult().ok();
    }
    
    @Override
    @DtfTransaction(type="SYNC_STRONG")
    @Transactional
    public DemoResult addTeacherInfoStrong(Teacher teacher) throws Exception {
        if(teacherMapper.insertSelective(teacher)>0){
            if(teacher.getT()==2){
                Sc sc = new Sc();
                sc.setC(teacher.getT());
                sc.setS(teacher.getT());
                sc.setScore(95);
                restClient.sendPost("http://localhost:8083/add_sc_info_strong",sc);
            }
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(), ErrorCode.SYS_ERROR.msg());
        }
    }
    
    @Override
    @DtfTransaction(type="ASYNC_FINAL")
    @Transactional
    public DemoResult addTeacherInfoAsync(Teacher teacher) throws Exception {
        teacherMapper.insertSelective(teacher);
            //int kk = 6/0;
        return new DemoResult().ok();
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
        //int kk = 6/0;
        return teacher;
    }


    @Override
    public Teacher getTeacher(int id) throws Exception {
        TeacherExample teacherExample = new TeacherExample();
        TeacherExample.Criteria criteria = teacherExample.createCriteria();
        criteria.andTEqualTo(id);
        List<Teacher> teacherList = teacherMapper.selectByExample(teacherExample);
        Teacher teacher = teacherList.get(0);
        //int kk = 6/0;
        return teacher;
    }
    
}

package com.coconason.dtf.demo.service.impl;

import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.demo.dao.CourseMapper;
import com.coconason.dtf.demo.model.DemoResult;
import com.coconason.dtf.demo.po.Course;
import com.coconason.dtf.demo.po.Teacher;
import com.coconason.dtf.demo.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:08
 */
@Service
public class CourseServiceImpl implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RestClient restClient;

    @Override
    @DtfTransaction
    @Transactional
    public DemoResult addCourseInfo(Course course) throws Exception {
//        if(courseMapper.insertSelective(course)>0){
            courseMapper.insertSelective(course);
            Teacher teacher = new Teacher();
            teacher.setT(1);
            teacher.setTname("林俊生");
            restClient.sendPost("http://localhost:8082/set_teacher_info",teacher);
            return new DemoResult().ok();
//        }else{
//            return new DemoResult().build(ErrorCode.SYS_ERROR.value(),ErrorCode.SYS_ERROR.msg());
//        }
    }
}

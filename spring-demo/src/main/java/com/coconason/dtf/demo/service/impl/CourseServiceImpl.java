package com.coconason.dtf.demo.service.impl;

import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.demo.constant.ErrorCode;
import com.coconason.dtf.demo.dao.CourseMapper;
import com.coconason.dtf.demo.model.DemoResult;
import com.coconason.dtf.demo.po.Course;
import com.coconason.dtf.demo.po.Teacher;
import com.coconason.dtf.demo.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:08
 */
public class CourseServiceImpl implements ICourseService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RestClient restClient;

    @Override
    public DemoResult addCourseInfo(Course course) throws Exception {

        if(courseMapper.insertSelective(course)>0){
            Teacher teacher = new Teacher();
            restClient.sendPost("http://localhost:",teacher);
            return new DemoResult().ok();
        }else{
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(),ErrorCode.SYS_ERROR.msg());
        }
    }
}

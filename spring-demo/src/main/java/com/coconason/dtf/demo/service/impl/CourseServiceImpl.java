package com.coconason.dtf.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.coconason.dtf.client.core.annotation.DtfTransaction;
import com.coconason.dtf.client.core.spring.client.RestClient;
import com.coconason.dtf.client.core.spring.client.RestClientAsync;
import com.coconason.dtf.demo.dao.CourseMapper;
import com.coconason.dtf.demo.model.DemoResult;
import com.coconason.dtf.demo.po.Course;
import com.coconason.dtf.demo.po.CourseExample;
import com.coconason.dtf.demo.po.Sc;
import com.coconason.dtf.demo.po.Teacher;
import com.coconason.dtf.demo.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    private RestClientAsync restClientAsync;

    @Override
    @DtfTransaction
    @Transactional
    public DemoResult addCourseInfo(Course course) throws Exception {
        courseMapper.insertSelective(course);
        Teacher teacher = new Teacher();
        teacher.setT(1);
        teacher.setTname("Lin");
        restClient.sendPost("http://localhost:8082/set_teacher_info",teacher);
        Teacher teacher1 = new Teacher();
        teacher1.setT(2);
        teacher1.setTname("Yun");
        restClient.sendPost("http://localhost:8082/set_teacher_info",teacher1);
        return new DemoResult().ok();
    }

    @Override
    @DtfTransaction(type="SYNC_STRONG")
    @Transactional
    public DemoResult addCourseInfoStrong(Course course) throws Exception {
        courseMapper.insertSelective(course);
        Teacher teacher = new Teacher();
        teacher.setT(1);
        teacher.setTname("Lin");
        restClient.sendPost("http://localhost:8082/set_teacher_info_strong",teacher);
        Teacher teacher1 = new Teacher();
        teacher1.setT(2);
        teacher1.setTname("Yun");
        restClient.sendPost("http://localhost:8082/set_teacher_info_strong",teacher1);
        return new DemoResult().ok();
    }

    @Override
    @DtfTransaction(type="ASYNC_FINAL")
    @Transactional
    public DemoResult addCourseInfoAsync(Course course) throws Exception {
        courseMapper.insertSelective(course);
        Teacher teacher = new Teacher();
        teacher.setT(2);
        teacher.setTname("Yun");
        restClientAsync.sendPost("http://localhost:8082/set_teacher_info_async",teacher);
        Sc sc = new Sc();
        sc.setC(5);
        sc.setS(7);
        sc.setScore(95);
        restClientAsync.sendPost("http://localhost:8083/add_sc_info_async",sc);
        return new DemoResult().ok();
    }
    @Override
    @DtfTransaction
    @Transactional(readOnly=true)
    public DemoResult getCourseInfo(int id) throws Exception{
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        criteria.andCEqualTo(id);
        List<Course> courseList = courseMapper.selectByExample(courseExample);
        Course course = courseList.get(0);
        Integer teacherId = course.getT();
        String result = restClient.sendGet("http://localhost:8082/get_teacher_info?id={1}",teacherId);
        JSONObject map = JSONObject.parseObject(result);
        DemoResult demoResult = new DemoResult().ok(map.get("datum"));
        return  demoResult;
    }
}

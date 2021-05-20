package com.dtf.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtf.client.core.annotation.DtfTransaction;
import com.dtf.client.core.spring.client.RestClient;
import com.dtf.client.core.spring.client.RestClientAsync;
import com.dtf.demo.constant.ErrorCode;
import com.dtf.demo.dao.CourseMapper;
import com.dtf.demo.model.DemoResult;
import com.dtf.demo.po.Course;
import com.dtf.demo.po.CourseExample;
import com.dtf.demo.po.Sc;
import com.dtf.demo.po.Teacher;
import com.dtf.demo.service.ICourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Course Service.
 * 
 * @Author: wangguangyuan
 * @date: 2018/8/27-15:08
 */
@Service
public class CourseServiceImpl implements ICourseService {
    
    private Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private RestClient restClient;
    
    @Autowired
    private RestClientAsync restClientAsync;

    /**
     * Add course information without distributed transaction.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @Transactional
    public DemoResult addCourseInfoWithoutDtf(final Course course) throws Exception {
        courseMapper.insertSelective(course);
        Teacher teacher = new Teacher();
        // uuid
        teacher.setT(course.getC());
        teacher.setTname("Lin");
        logger.debug("before sendPost ---------------------------" + System.currentTimeMillis());
        String result = restClient.sendPost("http://localhost:8082/set_teacher_info_without_dtf", teacher);
        logger.debug("after sendPost ---------------------------" + System.currentTimeMillis());
        Teacher teacher1 = new Teacher();
        teacher1.setT(course.getC());
        teacher1.setTname("Yun");
        logger.debug("before 2 sendPost ---------------------------" + System.currentTimeMillis());
        String result2 = restClient.sendPost("http://localhost:8082/set_teacher_info_without_dtf", teacher1);
        logger.debug("after 2 sendPost ---------------------------" + System.currentTimeMillis());
        return new DemoResult().ok();
    }

    /**
     * Add course information with distributed transaction in final synchronization mode.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction
    @Transactional
    public DemoResult addCourseInfo(final Course course) {
        courseMapper.insertSelective(course);
        Teacher teacher = new Teacher();
        teacher.setT(course.getT());
        teacher.setTname("Lin");
        logger.debug("before sendPost ---------------------------" + System.currentTimeMillis());
        String response = restClient.sendPost("http://localhost:8082/set_teacher_info", teacher);
        DemoResult result = new DemoResult(response);
        if (200 != result.getCode()) {
            return new DemoResult().build(ErrorCode.SYS_ERROR.value(), ErrorCode.SYS_ERROR.msg());
        }
        logger.debug("after sendPost ---------------------------" + System.currentTimeMillis());
        return new DemoResult().ok();
    }

    /**
     * Add course information with distributed transaction in strong synchronization mode.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction(type = "SYNC_STRONG")
    @Transactional
    public DemoResult addCourseInfoStrong(final Course course) throws Exception {
        courseMapper.insertSelective(course);
        Teacher teacher = new Teacher();
        teacher.setT(course.getT());
        teacher.setTname("Lin");
        restClient.sendPost("http://localhost:8082/set_teacher_info_strong", teacher);
        Teacher teacher1 = new Teacher();
        teacher1.setT(course.getT() * 10);
        teacher1.setTname("Yun");
        restClient.sendPost("http://localhost:8082/set_teacher_info_strong", teacher1);
        return new DemoResult().ok();
    }

    /**
     * Add course information with distributed transaction in asynchronization mode.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction(type = "ASYNC_FINAL")
    @Transactional
    public DemoResult addCourseInfoAsync(final Course course) throws Exception {
        courseMapper.insert(course);
        //int kk = 6/0;
        Teacher teacher = new Teacher();
        teacher.setT(course.getT());
        teacher.setTname("Yun");
        restClientAsync.sendPost("http://localhost:8082/set_teacher_info_async", teacher);
        Sc sc = new Sc();
        sc.setC(course.getC());
        sc.setS(course.getC());
        sc.setScore(95);
        restClientAsync.sendPost("http://localhost:8083/add_sc_info_async", sc);
        return new DemoResult().ok();
    }

    /**
     * Get course information.
     *
     * @param id integer
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    @DtfTransaction
    @Transactional(readOnly = true)
    public DemoResult getCourseInfo(final int id) throws Exception {
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        criteria.andCEqualTo(id);
        List<Course> courseList = courseMapper.selectByExample(courseExample);
        Course course = courseList.get(0);
        Integer teacherId = course.getT();
        //int kk = 6/0;
        String result = restClient.sendGet("http://localhost:8082/get_teacher_info?id={1}", teacherId);
        JSONObject map = JSONObject.parseObject(result);
        DemoResult demoResult = new DemoResult().ok(map.get("datum"));
        return demoResult;
    }

    /**
     * Get course.
     *
     * @param id integer
     * @return DemoResult
     * @throws Exception exception
     */
    @Override
    public Course getCourse(final int id) throws Exception {
        CourseExample courseExample = new CourseExample();
        CourseExample.Criteria criteria = courseExample.createCriteria();
        criteria.andCEqualTo(id);
        List<Course> courseList = courseMapper.selectByExample(courseExample);
        Course result = courseList.get(0);
        return result;
    }
    
}

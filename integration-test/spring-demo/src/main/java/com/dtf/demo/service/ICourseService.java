package com.dtf.demo.service;

/**
 * @author wangguangyuan
 */

import com.dtf.demo.model.DemoResult;
import com.dtf.demo.po.Course;

public interface ICourseService {
    
    /**
     * Add course information without distributed transaction.
     * 
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addCourseInfoWithoutDtf(Course course) throws Exception;

    /**
     * Add course information with distributed transaction in final synchronization mode.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addCourseInfo(Course course) throws Exception;

    /**
     * Add course information with distributed transaction in strong synchronization mode.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addCourseInfoStrong(Course course) throws Exception;

    /**
     * Add course information with distributed transaction in asynchronization mode.
     *
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addCourseInfoAsync(Course course) throws Exception;

    /**
     * Get course information.
     *
     * @param id integer
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult getCourseInfo(int id) throws Exception;

    /**
     * Get course.
     *
     * @param id integer
     * @return DemoResult
     * @throws Exception exception
     */
    Course getCourse(int id) throws Exception;
}

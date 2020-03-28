package com.coconason.dtf.demo.controller;

import com.coconason.dtf.demo.model.DemoResult;
import com.coconason.dtf.demo.po.Course;
import com.coconason.dtf.demo.po.Teacher;
import com.coconason.dtf.demo.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Course Controller.
 * 
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */
@RestController
public class CourseController {
    
    @Autowired
    private ICourseService courseService;
    
    /**
     * Add course information without distributed transaction.
     * 
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_course_info_without_dtf", method = RequestMethod.POST)
    public DemoResult addCourseInfoWithoutDtf(final @RequestBody Course course) throws Exception {
        DemoResult demoResult = courseService.addCourseInfoWithoutDtf(course);
        return demoResult;
    }

    /**
     * Add course information with distributed transaction in final synchronization mode.
     * 
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_course_info", method = RequestMethod.POST)
    public DemoResult addCourseInfo(final @RequestBody Course course) throws Exception {
        DemoResult demoResult = courseService.addCourseInfo(course);
        return demoResult;
    }

    /**
     * Add course information with distributed transaction in strong synchronization mode.
     * 
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_course_info_strong", method = RequestMethod.POST)
    public DemoResult addCourseInfoStrong(final @RequestBody Course course) throws Exception {
        DemoResult demoResult = courseService.addCourseInfoStrong(course);
        return demoResult;
    }

    /**
     * Add course information with distributed transaction in asynchronization mode.
     * 
     * @param course Course
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/add_course_info_async", method = RequestMethod.POST)
    public DemoResult addCourseInfoAsync(final @RequestBody Course course) throws Exception {
        DemoResult demoResult = courseService.addCourseInfoAsync(course);
        return demoResult;
    }

    /**
     * Get course information.
     * 
     * @param id id of course
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/get_course_info", method = RequestMethod.GET)
    public DemoResult getCourseInfo(final @RequestParam int id) throws Exception {
        DemoResult<Teacher> demoResult = courseService.getCourseInfo(id);
        return demoResult;
    }

    /**
     * Get course.
     * 
     * @param id id of course
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/get_course", method = RequestMethod.GET)
    public DemoResult getCourse(final @RequestParam int id) throws Exception {
        Course course = courseService.getCourse(id);
        return new DemoResult().ok(course);
    }
    
}

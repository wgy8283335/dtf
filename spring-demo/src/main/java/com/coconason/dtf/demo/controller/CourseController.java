package com.coconason.dtf.demo.controller;

import com.coconason.dtf.demo.model.DemoResult;
import com.coconason.dtf.demo.po.Course;
import com.coconason.dtf.demo.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */
@RestController
public class CourseController {

    @Autowired
    ICourseService courseService;

    @RequestMapping(value="/add_course_info",method = RequestMethod.POST)
    public DemoResult addCourseInfo(@RequestBody Course course) throws Exception{
        DemoResult demoResult = courseService.addCourseInfo(course);
        return demoResult;
    }

    @RequestMapping(value="/add_course_info_async",method = RequestMethod.POST)
    public DemoResult addCourseInfoAsync(@RequestBody Course course) throws Exception{
        DemoResult demoResult = courseService.addCourseInfoAsync(course);
        return demoResult;
    }
}

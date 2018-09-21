package com.coconason.dtf.demo.controller;

import com.coconason.dtf.demo.model.DemoResult;
import com.coconason.dtf.demo.po.Course;
import com.coconason.dtf.demo.po.Teacher;
import com.coconason.dtf.demo.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value="/add_course_info_strong",method = RequestMethod.POST)
    public DemoResult addCourseInfoStrong(@RequestBody Course course) throws Exception{
        DemoResult demoResult = courseService.addCourseInfoStrong(course);
        return demoResult;
    }

    @RequestMapping(value="/add_course_info_async",method = RequestMethod.POST)
    public DemoResult addCourseInfoAsync(@RequestBody Course course) throws Exception{
        DemoResult demoResult = courseService.addCourseInfoAsync(course);
        return demoResult;
    }

    @RequestMapping(value="/get_course_info",method = RequestMethod.GET)
    public DemoResult getCourseInfo(@RequestParam int id) throws Exception{
        DemoResult<Teacher> demoResult = courseService.getCourseInfo(id);
        return demoResult;
    }

}

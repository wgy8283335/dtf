package com.coconason.dtf.demo2.controller;

import com.coconason.dtf.demo2.model.DemoResult;
import com.coconason.dtf.demo2.po.Teacher;
import com.coconason.dtf.demo2.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */
@RestController
public class TeacherController {

    @Autowired
    ITeacherService teacherService;

    @RequestMapping(value="/set_teacher_info",method = RequestMethod.POST)
    public DemoResult setTeacherInfo(@RequestBody Teacher teacher) throws Exception{
        DemoResult demoResult = teacherService.addTeacherInfo(teacher);
        return demoResult;
    }

    @RequestMapping(value="/set_teacher_info_strong",method = RequestMethod.POST)
    public DemoResult setTeacherInfoStrong(@RequestBody Teacher teacher) throws Exception{
        DemoResult demoResult = teacherService.addTeacherInfoStrong(teacher);
        return demoResult;
    }

    @RequestMapping(value="/set_teacher_info_async",method = RequestMethod.POST)
    public DemoResult setTeacherInfoAsync(@RequestBody Teacher teacher) throws Exception{
        DemoResult demoResult = teacherService.addTeacherInfoAsync(teacher);
        return demoResult;
    }

    @RequestMapping(value="/get_teacher_info",method = RequestMethod.GET)
    public DemoResult getTeacherInfo(@RequestParam int id) throws Exception{
        Teacher teacher = teacherService.getTeacherInfo(id);
        return new DemoResult().ok(teacher);
    }
}

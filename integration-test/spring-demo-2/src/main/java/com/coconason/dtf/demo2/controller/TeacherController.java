package com.coconason.dtf.demo2.controller;

import com.coconason.dtf.demo2.constant.ErrorCode;
import com.coconason.dtf.demo2.model.DemoResult;
import com.coconason.dtf.demo2.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */
@RestController
public class TeacherController {

    @Autowired
    ITeacherService teacherService;

    @RequestMapping(value="/set_teacher_info_without_dtf",method = RequestMethod.POST)
    public DemoResult setTeacherInfoWithoutDtf(@RequestBody Teacher teacher) throws Exception{
        DemoResult demoResult = teacherService.addTeacherInfoWithoutDtf(teacher);
        return demoResult;
    }

    @RequestMapping(value="/set_teacher_info",method = RequestMethod.POST)
    public DemoResult setTeacherInfo(@RequestBody Teacher teacher) {
        DemoResult demoResult;
        try{
            demoResult = teacherService.addTeacherInfo(teacher);
        } catch (Exception e) {
            demoResult = new DemoResult().build(ErrorCode.SYS_ERROR.value(), ErrorCode.SYS_ERROR.msg());
        }
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
    
    @RequestMapping(value="/get_teacher",method = RequestMethod.GET)
    public DemoResult getTeacher(@RequestParam int id) throws Exception{
        Teacher teacher = teacherService.getTeacher(id);
        return new DemoResult().ok(teacher);
    }
}

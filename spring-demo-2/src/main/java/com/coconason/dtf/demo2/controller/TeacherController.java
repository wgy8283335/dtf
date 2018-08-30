package com.coconason.dtf.demo2.controller;

import com.coconason.dtf.demo2.model.DemoResult;
import com.coconason.dtf.demo2.po.Teacher;
import com.coconason.dtf.demo2.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */
@RestController
public class TeacherController {
    @Autowired
    ITeacherService teacherService;

    @RequestMapping(value="/set_teacher_info",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    public DemoResult setTeacherInfo(@RequestBody Teacher teacher) throws Exception{
        DemoResult demoResult = teacherService.addTeacherInfo(teacher);
        return demoResult;
    }
}

package com.dtf.demo2.controller;

import com.dtf.demo2.constant.ErrorCode;
import com.dtf.demo2.model.DemoResult;
import com.dtf.demo2.po.Teacher;
import com.dtf.demo2.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Teacher controller.
 * 
 * @Author: wangguangyuan
 * @date: 2018/8/27-15:00
 */
@RestController
public class TeacherController {
    
    @Autowired
    private ITeacherService teacherService;
    
    /**
     * Set teacher information without distributed transaction.
     * 
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/set_teacher_info_without_dtf", method = RequestMethod.POST)
    public DemoResult setTeacherInfoWithoutDtf(@RequestBody final Teacher teacher) throws Exception {
        DemoResult demoResult = teacherService.addTeacherInfoWithoutDtf(teacher);
        return demoResult;
    }
    
    /**
     * Set teacher information with distributed transaction in final synchronization mode.
     *
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/set_teacher_info", method = RequestMethod.POST)
    public DemoResult setTeacherInfo(@RequestBody final Teacher teacher) {
        DemoResult demoResult;
        try {
            demoResult = teacherService.addTeacherInfo(teacher);
        } catch (Exception e) {
            demoResult = new DemoResult().build(ErrorCode.SYS_ERROR.value(), ErrorCode.SYS_ERROR.msg());
        }
        return demoResult;
    }
    
    /**
     * Set teacher information with distributed transaction in strong synchronization mode.
     *
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/set_teacher_info_strong", method = RequestMethod.POST)
    public DemoResult setTeacherInfoStrong(@RequestBody final Teacher teacher) throws Exception {
        DemoResult demoResult = teacherService.addTeacherInfoStrong(teacher);
        return demoResult;
    }

    /**
     * Set teacher information with distributed transaction in asynchronization mode.
     *
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/set_teacher_info_async", method = RequestMethod.POST)
    public DemoResult setTeacherInfoAsync(@RequestBody final Teacher teacher) throws Exception {
        DemoResult demoResult = teacherService.addTeacherInfoAsync(teacher);
        return demoResult;
    }
    
    /**
     * Get teacher information.
     * 
     * @param id int
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/get_teacher_info", method = RequestMethod.GET)
    public DemoResult getTeacherInfo(@RequestParam final int id) throws Exception {
        Teacher teacher = teacherService.getTeacherInfo(id);
        return new DemoResult().ok(teacher);
    }
    
    /**
     * Get teacher.
     *
     * @param id int
     * @return DemoResult
     * @throws Exception exception
     */
    @RequestMapping(value = "/get_teacher", method = RequestMethod.GET)
    public DemoResult getTeacher(@RequestParam final int id) throws Exception {
        Teacher teacher = teacherService.getTeacher(id);
        return new DemoResult().ok(teacher);
    }
    
}

package com.coconason.dtf.demo2.service;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */


import com.coconason.dtf.demo2.model.DemoResult;
import com.coconason.dtf.demo2.po.Teacher;

public interface ITeacherService {
    DemoResult addTeacherInfo(Teacher teacher) throws Exception;

}

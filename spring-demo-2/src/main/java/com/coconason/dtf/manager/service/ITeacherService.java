package com.coconason.dtf.manager.service;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */


import com.coconason.dtf.manager.model.DemoResult;
import com.coconason.dtf.manager.po.Teacher;

public interface ITeacherService {
    DemoResult addTeacherInfo(Teacher teacher) throws Exception;

    DemoResult addTeacherInfoStrong(Teacher teacher) throws Exception;

    DemoResult addTeacherInfoAsync(Teacher teacher) throws Exception;

    Teacher getTeacherInfo(int id) throws Exception;
}

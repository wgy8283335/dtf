package com.coconason.dtf.demo2.service;

/**
 * @Author: Jason
 * @date: 2018/8/27-15:00
 */


import com.coconason.dtf.demo2.model.DemoResult;

public interface ITeacherService {

    DemoResult addTeacherInfoWithoutDtf(Teacher teacher) throws Exception;
    
    DemoResult addTeacherInfo(Teacher teacher) throws Exception;

    DemoResult addTeacherInfoStrong(Teacher teacher) throws Exception;

    DemoResult addTeacherInfoAsync(Teacher teacher) throws Exception;

    Teacher getTeacherInfo(int id) throws Exception;

    Teacher getTeacher(int id) throws Exception;
}

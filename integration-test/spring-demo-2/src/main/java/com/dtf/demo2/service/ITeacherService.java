package com.dtf.demo2.service;

import com.dtf.demo2.model.DemoResult;
import com.dtf.demo2.po.Teacher;

/**
 * Teacher service interface.
 * 
 * @author wangguangyuan
 */
public interface ITeacherService {
    
    /**
     * Add teacher information without distributed transaction.
     * 
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addTeacherInfoWithoutDtf(Teacher teacher) throws Exception;
    
    /**
     * Add teacher information with distributed transaction in final synchronization mode.
     *
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */    
    DemoResult addTeacherInfo(Teacher teacher) throws Exception;
    
    /**
     * Add teacher information with distributed transaction in strong synchronization mode.
     *
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addTeacherInfoStrong(Teacher teacher) throws Exception;

    /**
     * Add teacher information with distributed transaction in asynchronization mode.
     *
     * @param teacher Teacher
     * @return DemoResult
     * @throws Exception exception
     */
    DemoResult addTeacherInfoAsync(Teacher teacher) throws Exception;

    /**
     * Get teacher information.
     *
     * @param id int
     * @return DemoResult
     * @throws Exception exception
     */
    Teacher getTeacherInfo(int id) throws Exception;

    /**
     * Get teacher.
     *
     * @param id int
     * @return DemoResult
     * @throws Exception exception
     */
    Teacher getTeacher(int id) throws Exception;
}

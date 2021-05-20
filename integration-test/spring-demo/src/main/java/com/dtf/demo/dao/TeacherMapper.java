package com.dtf.demo.dao;

import com.dtf.demo.po.Teacher;
import com.dtf.demo.po.TeacherExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeacherMapper {
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param example TeacherExample
     * @return number of records
     */
    int countByExample(TeacherExample example);
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param example TeacherExample
     * @return number of records
     */
    int deleteByExample(TeacherExample example);
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param record Teacher
     * @return number of records
     */
    int insert(Teacher record);
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param record Teacher
     * @return number of records
     */
    int insertSelective(Teacher record);
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param example TeacherExample
     * @return number of records
     */
    List<Teacher> selectByExample(TeacherExample example);
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param record Teacher
     * @param example TeacherExample
     * @return number of records
     */
    int updateByExampleSelective(@Param("record") Teacher record, @Param("example") TeacherExample example);
    
    /**
     * This method corresponds to the database table Teacher.
     *
     * @param record Teacher
     * @param example TeacherExample
     * @return number of records
     */
    int updateByExample(@Param("record") Teacher record, @Param("example") TeacherExample example);
    
}

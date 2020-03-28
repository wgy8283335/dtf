package com.coconason.dtf.demo.dao;

import com.coconason.dtf.demo.po.Student;
import com.coconason.dtf.demo.po.StudentExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentMapper {
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param example StudentExample
     * @return number of records
     */
    int countByExample(StudentExample example);
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param example StudentExample
     * @return number of records
     */
    int deleteByExample(StudentExample example);
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param record Student
     * @return number of records
     */
    int insert(Student record);
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param record Student
     * @return number of records
     */
    int insertSelective(Student record);
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param example StudentExample
     * @return number of records
     */
    List<Student> selectByExample(StudentExample example);
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param record Student
     * @param example StudentExample
     * @return number of records
     */
    int updateByExampleSelective(@Param("record") Student record, @Param("example") StudentExample example);
    
    /**
     * This method corresponds to the database table Student.
     *
     * @param record Student
     * @param example StudentExample
     * @return number of records
     */
    int updateByExample(@Param("record") Student record, @Param("example") StudentExample example);
    
}

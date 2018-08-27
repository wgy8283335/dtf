package com.coconason.dtf.demo2.dao;


import com.coconason.dtf.demo2.po.Student;
import com.coconason.dtf.demo2.po.StudentExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int countByExample(StudentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int deleteByExample(StudentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int insert(Student record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int insertSelective(Student record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    List<Student> selectByExample(StudentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int updateByExampleSelective(@Param("record") Student record, @Param("example") StudentExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Student
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int updateByExample(@Param("record") Student record, @Param("example") StudentExample example);
}
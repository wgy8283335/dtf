package com.coconason.dtf.demo2.dao;


import com.coconason.dtf.demo2.po.Teacher;
import com.coconason.dtf.demo2.po.TeacherExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int countByExample(TeacherExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int deleteByExample(TeacherExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int insert(Teacher record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int insertSelective(Teacher record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    List<Teacher> selectByExample(TeacherExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int updateByExampleSelective(@Param("record") Teacher record, @Param("example") TeacherExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Teacher
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    int updateByExample(@Param("record") Teacher record, @Param("example") TeacherExample example);
}
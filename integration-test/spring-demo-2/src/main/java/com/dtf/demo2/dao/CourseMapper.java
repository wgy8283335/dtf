package com.dtf.demo2.dao;

import com.dtf.demo2.po.Course;
import com.dtf.demo2.po.CourseExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper {

    /**
     * This method corresponds to the database table Course.
     *
     * @param example CourseExample
     * @return number of records
     */
    int countByExample(CourseExample example);

    /**
     * This method corresponds to the database table Course.
     *
     * @param example CourseExample
     * @return number of records
     */
    int deleteByExample(CourseExample example);

    /**
     * This method corresponds to the database table Course.
     *
     * @param record Course
     * @return number of records
     */
    int insert(Course record);

    /**
     * This method corresponds to the database table Course.
     *
     * @param record Course
     * @return number of records
     */
    int insertSelective(Course record);

    /**
     * This method corresponds to the database table Course.
     *
     * @param example CourseExample
     * @return number of records
     */
    List<Course> selectByExample(CourseExample example);

    /**
     * This method corresponds to the database table Course.
     *
     * @param record Course
     * @param example CourseExample
     * @return number of records
     */
    int updateByExampleSelective(@Param("record") Course record, @Param("example") CourseExample example);

    /**
     * This method corresponds to the database table Course.
     *
     * @param record Course
     * @param example CourseExample
     * @return number of records
     */
    int updateByExample(@Param("record") Course record, @Param("example") CourseExample example);

}

package com.coconason.dtf.demo.po;

import java.io.Serializable;

public class Course implements Serializable {
    
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table Course
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column Course.C
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    private Integer c;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column Course.Cname
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    private String cname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column Course.T
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    private Integer t;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column Course.C
     *
     * @return the value of Course.C
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public Integer getC() {
        return c;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column Course.C
     *
     * @param c the value for Course.C
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void setC(final Integer c) {
        this.c = c;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column Course.Cname
     *
     * @return the value of Course.Cname
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public String getCname() {
        return cname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column Course.Cname
     *
     * @param cname the value for Course.Cname
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void setCname(final String cname) {
        this.cname = cname == null ? null : cname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column Course.T
     *
     * @return the value of Course.T
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public Integer getT() {
        return t;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column Course.T
     *
     * @param t the value for Course.T
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void setT(final Integer t) {
        this.t = t;
    }
    
}

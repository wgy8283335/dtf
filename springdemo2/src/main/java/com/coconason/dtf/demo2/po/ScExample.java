package com.coconason.dtf.demo2.po;

import java.util.ArrayList;
import java.util.List;

public class ScExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public ScExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andSIsNull() {
            addCriterion("S is null");
            return (Criteria) this;
        }

        public Criteria andSIsNotNull() {
            addCriterion("S is not null");
            return (Criteria) this;
        }

        public Criteria andSEqualTo(Integer value) {
            addCriterion("S =", value, "s");
            return (Criteria) this;
        }

        public Criteria andSNotEqualTo(Integer value) {
            addCriterion("S <>", value, "s");
            return (Criteria) this;
        }

        public Criteria andSGreaterThan(Integer value) {
            addCriterion("S >", value, "s");
            return (Criteria) this;
        }

        public Criteria andSGreaterThanOrEqualTo(Integer value) {
            addCriterion("S >=", value, "s");
            return (Criteria) this;
        }

        public Criteria andSLessThan(Integer value) {
            addCriterion("S <", value, "s");
            return (Criteria) this;
        }

        public Criteria andSLessThanOrEqualTo(Integer value) {
            addCriterion("S <=", value, "s");
            return (Criteria) this;
        }

        public Criteria andSIn(List<Integer> values) {
            addCriterion("S in", values, "s");
            return (Criteria) this;
        }

        public Criteria andSNotIn(List<Integer> values) {
            addCriterion("S not in", values, "s");
            return (Criteria) this;
        }

        public Criteria andSBetween(Integer value1, Integer value2) {
            addCriterion("S between", value1, value2, "s");
            return (Criteria) this;
        }

        public Criteria andSNotBetween(Integer value1, Integer value2) {
            addCriterion("S not between", value1, value2, "s");
            return (Criteria) this;
        }

        public Criteria andCIsNull() {
            addCriterion("C is null");
            return (Criteria) this;
        }

        public Criteria andCIsNotNull() {
            addCriterion("C is not null");
            return (Criteria) this;
        }

        public Criteria andCEqualTo(Integer value) {
            addCriterion("C =", value, "c");
            return (Criteria) this;
        }

        public Criteria andCNotEqualTo(Integer value) {
            addCriterion("C <>", value, "c");
            return (Criteria) this;
        }

        public Criteria andCGreaterThan(Integer value) {
            addCriterion("C >", value, "c");
            return (Criteria) this;
        }

        public Criteria andCGreaterThanOrEqualTo(Integer value) {
            addCriterion("C >=", value, "c");
            return (Criteria) this;
        }

        public Criteria andCLessThan(Integer value) {
            addCriterion("C <", value, "c");
            return (Criteria) this;
        }

        public Criteria andCLessThanOrEqualTo(Integer value) {
            addCriterion("C <=", value, "c");
            return (Criteria) this;
        }

        public Criteria andCIn(List<Integer> values) {
            addCriterion("C in", values, "c");
            return (Criteria) this;
        }

        public Criteria andCNotIn(List<Integer> values) {
            addCriterion("C not in", values, "c");
            return (Criteria) this;
        }

        public Criteria andCBetween(Integer value1, Integer value2) {
            addCriterion("C between", value1, value2, "c");
            return (Criteria) this;
        }

        public Criteria andCNotBetween(Integer value1, Integer value2) {
            addCriterion("C not between", value1, value2, "c");
            return (Criteria) this;
        }

        public Criteria andScoreIsNull() {
            addCriterion("score is null");
            return (Criteria) this;
        }

        public Criteria andScoreIsNotNull() {
            addCriterion("score is not null");
            return (Criteria) this;
        }

        public Criteria andScoreEqualTo(Integer value) {
            addCriterion("score =", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotEqualTo(Integer value) {
            addCriterion("score <>", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThan(Integer value) {
            addCriterion("score >", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("score >=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThan(Integer value) {
            addCriterion("score <", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThanOrEqualTo(Integer value) {
            addCriterion("score <=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreIn(List<Integer> values) {
            addCriterion("score in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotIn(List<Integer> values) {
            addCriterion("score not in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreBetween(Integer value1, Integer value2) {
            addCriterion("score between", value1, value2, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("score not between", value1, value2, "score");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table Sc
     *
     * @mbggenerated do_not_delete_during_merge Mon Aug 27 14:24:30 CST 2018
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table Sc
     *
     * @mbggenerated Mon Aug 27 14:24:30 CST 2018
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}
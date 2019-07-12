package com.github.gantleman.shopd.entity;

import java.util.ArrayList;
import java.util.List;

public class CacheExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;
    
    protected Integer pageStart;

    protected Integer pageSize;

    public void setPageStart(Integer pageStart) {
        this.pageStart = pageStart;
    }

    public Integer getPageStart() {
        return pageStart;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public CacheExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

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

        public Criteria andCIdIsNull() {
            addCriterion("c_id is null");
            return (Criteria) this;
        }

        public Criteria andCIdIsNotNull() {
            addCriterion("c_id is not null");
            return (Criteria) this;
        }

        public Criteria andCIdEqualTo(Integer value) {
            addCriterion("c_id =", value, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdNotEqualTo(Integer value) {
            addCriterion("c_id <>", value, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdGreaterThan(Integer value) {
            addCriterion("c_id >", value, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_id >=", value, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdLessThan(Integer value) {
            addCriterion("c_id <", value, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdLessThanOrEqualTo(Integer value) {
            addCriterion("c_id <=", value, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdIn(List<Integer> values) {
            addCriterion("c_id in", values, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdNotIn(List<Integer> values) {
            addCriterion("c_id not in", values, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdBetween(Integer value1, Integer value2) {
            addCriterion("c_id between", value1, value2, "cId");
            return (Criteria) this;
        }

        public Criteria andCIdNotBetween(Integer value1, Integer value2) {
            addCriterion("c_id not between", value1, value2, "cId");
            return (Criteria) this;
        }

        public Criteria andCNameIsNull() {
            addCriterion("c_name is null");
            return (Criteria) this;
        }

        public Criteria andCNameIsNotNull() {
            addCriterion("c_name is not null");
            return (Criteria) this;
        }

        public Criteria andCNameEqualTo(String value) {
            addCriterion("c_name =", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameNotEqualTo(String value) {
            addCriterion("c_name <>", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameGreaterThan(String value) {
            addCriterion("c_name >", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameGreaterThanOrEqualTo(String value) {
            addCriterion("c_name >=", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameLessThan(String value) {
            addCriterion("c_name <", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameLessThanOrEqualTo(String value) {
            addCriterion("c_name <=", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameLike(String value) {
            addCriterion("c_name like", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameNotLike(String value) {
            addCriterion("c_name not like", value, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameIn(List<String> values) {
            addCriterion("c_name in", values, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameNotIn(List<String> values) {
            addCriterion("c_name not in", values, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameBetween(String value1, String value2) {
            addCriterion("c_name between", value1, value2, "cName");
            return (Criteria) this;
        }

        public Criteria andCNameNotBetween(String value1, String value2) {
            addCriterion("c_name not between", value1, value2, "cName");
            return (Criteria) this;
        }

        public Criteria andCIndexIsNull() {
            addCriterion("c_index is null");
            return (Criteria) this;
        }

        public Criteria andCIndexIsNotNull() {
            addCriterion("c_index is not null");
            return (Criteria) this;
        }

        public Criteria andCIndexEqualTo(Long value) {
            addCriterion("c_index =", value, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexNotEqualTo(Long value) {
            addCriterion("c_index <>", value, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexGreaterThan(Long value) {
            addCriterion("c_index >", value, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexGreaterThanOrEqualTo(Long value) {
            addCriterion("c_index >=", value, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexLessThan(Long value) {
            addCriterion("c_index <", value, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexLessThanOrEqualTo(Long value) {
            addCriterion("c_index <=", value, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexIn(List<Long> values) {
            addCriterion("c_index in", values, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexNotIn(List<Long> values) {
            addCriterion("c_index not in", values, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexBetween(Long value1, Long value2) {
            addCriterion("c_index between", value1, value2, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCIndexNotBetween(Long value1, Long value2) {
            addCriterion("c_index not between", value1, value2, "cIndex");
            return (Criteria) this;
        }

        public Criteria andCHostIsNull() {
            addCriterion("c_host is null");
            return (Criteria) this;
        }

        public Criteria andCHostIsNotNull() {
            addCriterion("c_host is not null");
            return (Criteria) this;
        }

        public Criteria andCHostEqualTo(String value) {
            addCriterion("c_host =", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostNotEqualTo(String value) {
            addCriterion("c_host <>", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostGreaterThan(String value) {
            addCriterion("c_host >", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostGreaterThanOrEqualTo(String value) {
            addCriterion("c_host >=", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostLessThan(String value) {
            addCriterion("c_host <", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostLessThanOrEqualTo(String value) {
            addCriterion("c_host <=", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostLike(String value) {
            addCriterion("c_host like", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostNotLike(String value) {
            addCriterion("c_host not like", value, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostIn(List<String> values) {
            addCriterion("c_host in", values, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostNotIn(List<String> values) {
            addCriterion("c_host not in", values, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostBetween(String value1, String value2) {
            addCriterion("c_host between", value1, value2, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHostNotBetween(String value1, String value2) {
            addCriterion("c_host not between", value1, value2, "cHost");
            return (Criteria) this;
        }

        public Criteria andCHost2IsNull() {
            addCriterion("c_host2 is null");
            return (Criteria) this;
        }

        public Criteria andCHost2IsNotNull() {
            addCriterion("c_host2 is not null");
            return (Criteria) this;
        }

        public Criteria andCHost2EqualTo(String value) {
            addCriterion("c_host2 =", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2NotEqualTo(String value) {
            addCriterion("c_host2 <>", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2GreaterThan(String value) {
            addCriterion("c_host2 >", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2GreaterThanOrEqualTo(String value) {
            addCriterion("c_host2 >=", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2LessThan(String value) {
            addCriterion("c_host2 <", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2LessThanOrEqualTo(String value) {
            addCriterion("c_host2 <=", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2Like(String value) {
            addCriterion("c_host2 like", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2NotLike(String value) {
            addCriterion("c_host2 not like", value, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2In(List<String> values) {
            addCriterion("c_host2 in", values, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2NotIn(List<String> values) {
            addCriterion("c_host2 not in", values, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2Between(String value1, String value2) {
            addCriterion("c_host2 between", value1, value2, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCHost2NotBetween(String value1, String value2) {
            addCriterion("c_host2 not between", value1, value2, "cHost2");
            return (Criteria) this;
        }

        public Criteria andCStampIsNull() {
            addCriterion("c_stamp is null");
            return (Criteria) this;
        }

        public Criteria andCStampIsNotNull() {
            addCriterion("c_stamp is not null");
            return (Criteria) this;
        }

        public Criteria andCStampEqualTo(Long value) {
            addCriterion("c_stamp =", value, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampNotEqualTo(Long value) {
            addCriterion("c_stamp <>", value, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampGreaterThan(Long value) {
            addCriterion("c_stamp >", value, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampGreaterThanOrEqualTo(Long value) {
            addCriterion("c_stamp >=", value, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampLessThan(Long value) {
            addCriterion("c_stamp <", value, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampLessThanOrEqualTo(Long value) {
            addCriterion("c_stamp <=", value, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampIn(List<Long> values) {
            addCriterion("c_stamp in", values, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampNotIn(List<Long> values) {
            addCriterion("c_stamp not in", values, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampBetween(Long value1, Long value2) {
            addCriterion("c_stamp between", value1, value2, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStampNotBetween(Long value1, Long value2) {
            addCriterion("c_stamp not between", value1, value2, "cStamp");
            return (Criteria) this;
        }

        public Criteria andCStamp2IsNull() {
            addCriterion("c_stamp2 is null");
            return (Criteria) this;
        }

        public Criteria andCStamp2IsNotNull() {
            addCriterion("c_stamp2 is not null");
            return (Criteria) this;
        }

        public Criteria andCStamp2EqualTo(Long value) {
            addCriterion("c_stamp2 =", value, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2NotEqualTo(Long value) {
            addCriterion("c_stamp2 <>", value, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2GreaterThan(Long value) {
            addCriterion("c_stamp2 >", value, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2GreaterThanOrEqualTo(Long value) {
            addCriterion("c_stamp2 >=", value, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2LessThan(Long value) {
            addCriterion("c_stamp2 <", value, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2LessThanOrEqualTo(Long value) {
            addCriterion("c_stamp2 <=", value, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2In(List<Long> values) {
            addCriterion("c_stamp2 in", values, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2NotIn(List<Long> values) {
            addCriterion("c_stamp2 not in", values, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2Between(Long value1, Long value2) {
            addCriterion("c_stamp2 between", value1, value2, "cStamp2");
            return (Criteria) this;
        }

        public Criteria andCStamp2NotBetween(Long value1, Long value2) {
            addCriterion("c_stamp2 not between", value1, value2, "cStamp2");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

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
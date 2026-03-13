package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

import java.util.List;

/**
* Created by  on 2020/01/14.
*/
public class CallAgvCriteria extends QueryCriteria {
    private String errorCode;
    private String methodName;
    private List<Long> ids;
    private Integer state;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public CallAgvCriteria() {
    }

    public CallAgvCriteria(String errorCode, Integer state,Integer pageSize) {
        this.errorCode = errorCode;
        this.state = state;
        this.setPageSize(pageSize);
    }
}

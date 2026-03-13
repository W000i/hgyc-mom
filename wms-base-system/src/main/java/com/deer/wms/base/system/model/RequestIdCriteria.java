package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

import java.util.List;

/**
* Created by  on 2019/12/26.
*/
public class RequestIdCriteria extends QueryCriteria {
    private Integer type;
    private Integer state;
    private String requestId;
    private Integer id;
    private String loginPersonCardNo;
    private List<Integer> ids;

    public String getLoginPersonCardNo() {
        return loginPersonCardNo;
    }

    public void setLoginPersonCardNo(String loginPersonCardNo) {
        this.loginPersonCardNo = loginPersonCardNo;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public RequestIdCriteria(Integer state) {
        this.state = state;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RequestIdCriteria() {
    }

    public RequestIdCriteria(String requestId, Integer id) {
        this.requestId = requestId;
        this.id = id;
    }
}

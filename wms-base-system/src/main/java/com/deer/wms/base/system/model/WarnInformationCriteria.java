package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

import java.util.List;

/**
* Created by  on 2020/01/06.
*/
public class WarnInformationCriteria extends QueryCriteria {
    private Integer state;
    private String loginPersonCardNo;
    private List<Integer> ids;
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public WarnInformationCriteria() {
    }

    public WarnInformationCriteria(Integer state) {
        this.state = state;
    }
}

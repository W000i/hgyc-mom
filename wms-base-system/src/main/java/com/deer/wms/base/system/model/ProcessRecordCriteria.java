package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

/**
* Created by  on 2019/12/03.
*/
public class ProcessRecordCriteria extends QueryCriteria {
    private Integer subInventoryId;
    private String flowCode;
    private String loginPersonCardNo;
    private String itemCode;
    private String batch;
    private String exp;

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getLoginPersonCardNo() {
        return loginPersonCardNo;
    }

    public void setLoginPersonCardNo(String loginPersonCardNo) {
        this.loginPersonCardNo = loginPersonCardNo;
    }
}

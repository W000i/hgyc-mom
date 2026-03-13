package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

/**
* Created by  on 2019/10/11.
*/
public class CarrierCriteria extends QueryCriteria {
    private Integer state;
    private String billNo;
    private Integer carrierId;

    public Integer getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Integer carrierId) {
        this.carrierId = carrierId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }
}

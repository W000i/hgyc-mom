package com.deer.wms.base.system.model.bill;

import com.deer.wms.common.core.service.QueryCriteria;


/**
 * 入库单表 bill_in_master
 * 
 * @author guo
 * @date 2019-05-13
 */

public class BillOutDetailCriteria extends QueryCriteria
{
    private Integer state;
    private Integer type;
    private Integer orderByState;
    private Integer billId;
    private Integer sequence;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOrderByState() {
        return orderByState;
    }

    public void setOrderByState(Integer orderByState) {
        this.orderByState = orderByState;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public BillOutDetailCriteria() {
    }

    public BillOutDetailCriteria(Integer state, Integer type, Integer orderByState, Integer sequence) {
        this.state = state;
        this.type = type;
        this.orderByState = orderByState;
        this.sequence = sequence;
    }
}

package com.deer.wms.base.system.model.bill;

import com.deer.wms.common.core.service.QueryCriteria;

import java.util.List;


/**
 * 入库单表 bill_in_master
 * 
 * @author guo
 * @date 2019-05-13
 */

public class BillOutMasterCriteria extends QueryCriteria
{
    private String billNo;
    private Integer type;
    private Integer state;
    private String itemCode;
    private String loginPersonCardNo;

    private Integer outQuantity;
    private String boxCode;
    private Integer billOutDetailId;
    private Integer orderByState;
    private Integer billId;
    private List<Integer> billIds;

    public List<Integer> getBillIds() {
        return billIds;
    }

    public void setBillIds(List<Integer> billIds) {
        this.billIds = billIds;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public Integer getOrderByState() {
        return orderByState;
    }

    public void setOrderByState(Integer orderByState) {
        this.orderByState = orderByState;
    }

    public Integer getBillOutDetailId() {
        return billOutDetailId;
    }

    public void setBillOutDetailId(Integer billOutDetailId) {
        this.billOutDetailId = billOutDetailId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getOutQuantity() {
        return outQuantity;
    }

    public void setOutQuantity(Integer outQuantity) {
        this.outQuantity = outQuantity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getLoginPersonCardNo() {
        return loginPersonCardNo;
    }

    public void setLoginPersonCardNo(String loginPersonCardNo) {
        this.loginPersonCardNo = loginPersonCardNo;
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

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public BillOutMasterCriteria() {
    }

    public BillOutMasterCriteria(String billNo) {
        this.billNo = billNo;
    }

    public BillOutMasterCriteria(Integer type, Integer state) {
        this.type = type;
        this.state = state;
    }

    public BillOutMasterCriteria(Integer type, Integer state, String itemCode) {
        this.type = type;
        this.state = state;
        this.itemCode = itemCode;
    }

    public BillOutMasterCriteria(Integer type, Integer state, Integer orderByState, Integer billId) {
        this.type = type;
        this.state = state;
        this.orderByState = orderByState;
        this.billId = billId;
    }

    public BillOutMasterCriteria(String billNo, Integer orderByState) {
        this.billNo = billNo;
        this.orderByState = orderByState;
    }
}

package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

/**
* Created by  on 2020/09/16.
*/
public class BillInReceiveCriteria extends QueryCriteria {
    private Integer state;
    private String receiptNum;
    private Integer orderParam;

    private Integer poHeaderId;
    private Integer poLineId;
    private Integer lineLocationId;
    private Integer poDistributionId;
    private String batch;
    private Integer itemId;
    private Integer quantity;

    public Integer getPoHeaderId() {
        return poHeaderId;
    }

    public void setPoHeaderId(Integer poHeaderId) {
        this.poHeaderId = poHeaderId;
    }

    public Integer getPoLineId() {
        return poLineId;
    }

    public void setPoLineId(Integer poLineId) {
        this.poLineId = poLineId;
    }

    public Integer getLineLocationId() {
        return lineLocationId;
    }

    public void setLineLocationId(Integer lineLocationId) {
        this.lineLocationId = lineLocationId;
    }

    public Integer getPoDistributionId() {
        return poDistributionId;
    }

    public void setPoDistributionId(Integer poDistributionId) {
        this.poDistributionId = poDistributionId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getOrderParam() {
        return orderParam;
    }

    public void setOrderParam(Integer orderParam) {
        this.orderParam = orderParam;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getReceiptNum() {
        return receiptNum;
    }

    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
    }

    public BillInReceiveCriteria() {
    }

    public BillInReceiveCriteria(Integer state, Integer orderParam) {
        this.state = state;
        this.orderParam = orderParam;
    }

    public BillInReceiveCriteria(Integer state, String receiptNum) {
        this.state = state;
        this.receiptNum = receiptNum;
    }

    public BillInReceiveCriteria(Integer state, Integer orderParam, Integer poHeaderId, Integer poLineId, Integer lineLocationId, Integer poDistributionId, String batch, Integer itemId, Integer quantity) {
        this.state = state;
        this.orderParam = orderParam;
        this.poHeaderId = poHeaderId;
        this.poLineId = poLineId;
        this.lineLocationId = lineLocationId;
        this.poDistributionId = poDistributionId;
        this.batch = batch;
        this.itemId = itemId;
        this.quantity = quantity;
    }
}

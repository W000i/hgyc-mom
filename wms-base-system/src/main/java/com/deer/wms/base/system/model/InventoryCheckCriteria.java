package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

/**
* Created by  on 2019/12/31.
*/
public class InventoryCheckCriteria extends QueryCriteria {
    private Integer billOutDetailId;
    private String loginPersonCardNo;

    private Integer type;
    private String boxCode;
    private Integer dispositionId;
    private Integer checkQuantity;
    private Integer subInventoryId;
    private String itemCode;
    private String batch;
    private Integer inventoryCheckId;
    private String taskId;
    private String toBoxCode;

    public String getToBoxCode() {
        return toBoxCode;
    }

    public void setToBoxCode(String toBoxCode) {
        this.toBoxCode = toBoxCode;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getInventoryCheckId() {
        return inventoryCheckId;
    }

    public void setInventoryCheckId(Integer inventoryCheckId) {
        this.inventoryCheckId = inventoryCheckId;
    }

    public Integer getDispositionId() {
        return dispositionId;
    }

    public void setDispositionId(Integer dispositionId) {
        this.dispositionId = dispositionId;
    }

    public Integer getCheckQuantity() {
        return checkQuantity;
    }

    public void setCheckQuantity(Integer checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
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

    public String getLoginPersonCardNo() {
        return loginPersonCardNo;
    }

    public void setLoginPersonCardNo(String loginPersonCardNo) {
        this.loginPersonCardNo = loginPersonCardNo;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getBillOutDetailId() {
        return billOutDetailId;
    }

    public void setBillOutDetailId(Integer billOutDetailId) {
        this.billOutDetailId = billOutDetailId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public InventoryCheckCriteria() {
    }

    public InventoryCheckCriteria(Integer billOutDetailId, Integer type) {
        this.billOutDetailId = billOutDetailId;
        this.type = type;
    }
}

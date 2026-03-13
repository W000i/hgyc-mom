package com.deer.wms.base.system.model;

import javax.persistence.*;


public class RequestIdDto extends RequestId{
    private String itemCode;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
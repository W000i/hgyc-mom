package com.deer.wms.base.system.model;

import javax.persistence.*;


public class CombineBoxRecordDto extends CombineBoxRecord{
    private String operatorName;
    private String subInventoryName;

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getSubInventoryName() {
        return subInventoryName;
    }

    public void setSubInventoryName(String subInventoryName) {
        this.subInventoryName = subInventoryName;
    }
}
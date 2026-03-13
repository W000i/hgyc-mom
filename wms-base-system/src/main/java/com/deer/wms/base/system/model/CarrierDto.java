package com.deer.wms.base.system.model;

import com.deer.wms.common.annotation.Excel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class CarrierDto  extends Carrier{
    @Excel(name="工单Id",type=Excel.Type.EXPORT,column = 1)
    private String billNo;
    private Integer alreadyOutQuantity;

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Integer getAlreadyOutQuantity() {
        return alreadyOutQuantity;
    }

    public void setAlreadyOutQuantity(Integer alreadyOutQuantity) {
        this.alreadyOutQuantity = alreadyOutQuantity;
    }
}
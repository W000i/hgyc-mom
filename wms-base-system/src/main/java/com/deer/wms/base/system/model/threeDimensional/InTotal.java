package com.deer.wms.base.system.model.threeDimensional;

public class InTotal {
    private Integer billId;
    private String segment;
    private String createUserName;
    private String unit;
    private String itemCode;
    private String itemDescription;
    private String acceptTime;
    private Integer acceptQuantity;
    private String vendorName;
    private String expectedArrivalDate;
    private Integer itemId;
    private String batch;
    private Integer surplusReceivedQuantity;

    public Integer getSurplusReceivedQuantity() {
        return surplusReceivedQuantity;
    }

    public void setSurplusReceivedQuantity(Integer surplusReceivedQuantity) {
        this.surplusReceivedQuantity = surplusReceivedQuantity;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }


    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Integer getAcceptQuantity() {
        return acceptQuantity;
    }

    public void setAcceptQuantity(Integer acceptQuantity) {
        this.acceptQuantity = acceptQuantity;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getExpectedArrivalDate() {
        return expectedArrivalDate;
    }

    public void setExpectedArrivalDate(String expectedArrivalDate) {
        this.expectedArrivalDate = expectedArrivalDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}

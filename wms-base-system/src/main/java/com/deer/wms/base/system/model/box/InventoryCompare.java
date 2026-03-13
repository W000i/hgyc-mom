package com.deer.wms.base.system.model.box;

public class InventoryCompare {
    private String itemName;
    private String itemCode;
    private String batch;
    private Integer quantity;
    private Integer ebsQuantity;
    private Integer differenceQuantity;
    private String exp;
    private String subInventoryCode;
    private String slotting;
    private String subInventoryName;
    private Integer subInventoryId;
    private Integer inventoryItemId;

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public Integer getDifferenceQuantity() {
        return differenceQuantity;
    }

    public void setDifferenceQuantity(Integer differenceQuantity) {
        this.differenceQuantity = differenceQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getEbsQuantity() {
        return ebsQuantity;
    }

    public void setEbsQuantity(Integer ebsQuantity) {
        this.ebsQuantity = ebsQuantity;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getSubInventoryCode() {
        return subInventoryCode;
    }

    public void setSubInventoryCode(String subInventoryCode) {
        this.subInventoryCode = subInventoryCode;
    }

    public String getSlotting() {
        return slotting;
    }

    public void setSlotting(String slotting) {
        this.slotting = slotting;
    }

    public String getSubInventoryName() {
        return subInventoryName;
    }

    public void setSubInventoryName(String subInventoryName) {
        this.subInventoryName = subInventoryName;
    }
}

package com.deer.wms.base.system.model.threeDimensional;

public class Box {
    private Integer boxId;
    private String boxCode;
    private Integer boxCellId;
    private Integer boxState;
    private Integer hasGoods;
    private String itemCode;
    private String batch;
    private Integer quantity;
    private String pd;
    private String inTime;
    private String exp;
    private Integer subInventoryId;

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getBoxCellId() {
        return boxCellId;
    }

    public void setBoxCellId(Integer boxCellId) {
        this.boxCellId = boxCellId;
    }

    public Integer getBoxState() {
        return boxState;
    }

    public void setBoxState(Integer boxState) {
        this.boxState = boxState;
    }

    public Integer getHasGoods() {
        return hasGoods;
    }

    public void setHasGoods(Integer hasGoods) {
        this.hasGoods = hasGoods;
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

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }
}

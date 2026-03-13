package com.deer.wms.base.system.model.threeDimensional;

public class Cell {
    private Integer cellId;
    private Integer shelfId;
    private Integer sRow;
    private Integer sColumn;
    private Integer state;
    private String boxCode;
    private Integer boxType;
    private Integer boxState;
    private Integer hasGoods;
    private Integer subInventoryId;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getBoxType() {
        return boxType;
    }

    public void setBoxType(Integer boxType) {
        this.boxType = boxType;
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

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public Integer getShelfId() {
        return shelfId;
    }

    public void setShelfId(Integer shelfId) {
        this.shelfId = shelfId;
    }

    public Integer getsRow() {
        return sRow;
    }

    public void setsRow(Integer sRow) {
        this.sRow = sRow;
    }

    public Integer getsColumn() {
        return sColumn;
    }

    public void setsColumn(Integer sColumn) {
        this.sColumn = sColumn;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}

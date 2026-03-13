package com.deer.wms.base.system.model.task;

public class HayStackerLocation {
    private Integer fromShelf;
    private Integer fromColumn;
    private Integer fromRow;
    private Integer toShelf;
    private Integer toColumn;
    private Integer toRow;
    private String cardNo;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Integer getFromShelf() {
        return fromShelf;
    }

    public void setFromShelf(Integer fromShelf) {
        this.fromShelf = fromShelf;
    }

    public Integer getFromColumn() {
        return fromColumn;
    }

    public void setFromColumn(Integer fromColumn) {
        this.fromColumn = fromColumn;
    }

    public Integer getFromRow() {
        return fromRow;
    }

    public void setFromRow(Integer fromRow) {
        this.fromRow = fromRow;
    }

    public Integer getToShelf() {
        return toShelf;
    }

    public void setToShelf(Integer toShelf) {
        this.toShelf = toShelf;
    }

    public Integer getToColumn() {
        return toColumn;
    }

    public void setToColumn(Integer toColumn) {
        this.toColumn = toColumn;
    }

    public Integer getToRow() {
        return toRow;
    }

    public void setToRow(Integer toRow) {
        this.toRow = toRow;
    }
}

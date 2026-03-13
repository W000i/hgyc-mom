package com.deer.wms.base.system.model;

public class MainTotal {
    //货位利用率
    private double cellOccupyRatio;
    //总货位
    private int totalCell;
    //可用货位
    private int notItemCell;
    //出库单总数
    private int totalOut;
    //未出库出库单
    private int totalNoOut;
    //入库单总数
    private int totalIn;
    //入库单未完成
    private int totalNoIn;

    public double getCellOccupyRatio() {
        return cellOccupyRatio;
    }

    public void setCellOccupyRatio(double cellOccupyRatio) {
        this.cellOccupyRatio = cellOccupyRatio;
    }

    public int getTotalCell() {
        return totalCell;
    }

    public void setTotalCell(int totalCell) {
        this.totalCell = totalCell;
    }

    public int getNotItemCell() {
        return notItemCell;
    }

    public void setNotItemCell(int notItemCell) {
        this.notItemCell = notItemCell;
    }

    public int getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(int totalOut) {
        this.totalOut = totalOut;
    }

    public int getTotalNoOut() {
        return totalNoOut;
    }

    public void setTotalNoOut(int totalNoOut) {
        this.totalNoOut = totalNoOut;
    }

    public int getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(int totalIn) {
        this.totalIn = totalIn;
    }

    public int getTotalNoIn() {
        return totalNoIn;
    }

    public void setTotalNoIn(int totalNoIn) {
        this.totalNoIn = totalNoIn;
    }
}

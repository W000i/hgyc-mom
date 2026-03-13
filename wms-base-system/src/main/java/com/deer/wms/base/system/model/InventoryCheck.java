package com.deer.wms.base.system.model;

import javax.persistence.*;

@Table(name = "inventory_check")
public class InventoryCheck {
    /**
     * 盘点
     */
    @Id
    @Column(name = "inventory_check_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventoryCheckId;

    /**
     * 从箱号
     */
    @Column(name = "box_code")
    private String boxCode;

    /**
     * 到箱号
     */
    @Column(name = "to_box_code")
    private String toBoxCode;

    /**
     * 当前箱数量
     */
    private Integer quantity;

    /**
     * 盘点后数量
     */
    @Column(name = "after_check_quantity")
    private Integer afterCheckQuantity;

    /**
     * 盘盈/盘亏
     */
    @Column(name = "check_quantity")
    private Integer checkQuantity;

    /**
     * 类型 1-工单出库盘点  2-非工单出库盘点 3-盘点任务
     */
    private Integer type;

    /**
     * 1-待下发 2-已下发 3- 已完成
     */
    private Integer state;

    /**
     * 物料编码
     */
    @Column(name = "item_code")
    private String itemCode;

    /**
     * 批次
     */
    private String batch;

    @Column(name="disposition_id")
    private Integer dispositionId;

    @Column(name="card_no")
    private String cardNo;

    @Column(name="sub_inventory_id")
    private Integer subInventoryId;

    @Column(name="create_time")
    private String createTime;

    @Column(name="commit_time")
    private String commitTime;

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * 获取盘点
     *
     * @return inventory_check_id - 盘点
     */
    public Integer getInventoryCheckId() {
        return inventoryCheckId;
    }

    /**
     * 设置盘点
     *
     * @param inventoryCheckId 盘点
     */
    public void setInventoryCheckId(Integer inventoryCheckId) {
        this.inventoryCheckId = inventoryCheckId;
    }

    /**
     * 获取从箱号
     *
     * @return box_code - 从箱号
     */
    public String getBoxCode() {
        return boxCode;
    }

    /**
     * 设置从箱号
     *
     * @param boxCode 从箱号
     */
    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    /**
     * 获取到箱号
     *
     * @return to_box_code - 到箱号
     */
    public String getToBoxCode() {
        return toBoxCode;
    }

    /**
     * 设置到箱号
     *
     * @param toBoxCode 到箱号
     */
    public void setToBoxCode(String toBoxCode) {
        this.toBoxCode = toBoxCode;
    }

    /**
     * 获取当前箱数量
     *
     * @return quantity - 当前箱数量
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置当前箱数量
     *
     * @param quantity 当前箱数量
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取盘点后数量
     *
     * @return after_check_quantity - 盘点后数量
     */
    public Integer getAfterCheckQuantity() {
        return afterCheckQuantity;
    }

    /**
     * 设置盘点后数量
     *
     * @param afterCheckQuantity 盘点后数量
     */
    public void setAfterCheckQuantity(Integer afterCheckQuantity) {
        this.afterCheckQuantity = afterCheckQuantity;
    }

    /**
     * 获取盘盈/盘亏
     *
     * @return check_quantity - 盘盈/盘亏
     */
    public Integer getCheckQuantity() {
        return checkQuantity;
    }

    /**
     * 设置盘盈/盘亏
     *
     * @param checkQuantity 盘盈/盘亏
     */
    public void setCheckQuantity(Integer checkQuantity) {
        this.checkQuantity = checkQuantity;
    }

    /**
     * 获取类型 1-工单出库盘点  2-非工单出库盘点 3-盘点任务
     *
     * @return type - 类型 1-工单出库盘点  2-非工单出库盘点 3-盘点任务
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型 1-工单出库盘点  2-非工单出库盘点 3-盘点任务
     *
     * @param type 类型 1-工单出库盘点  2-非工单出库盘点 3-盘点任务
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取1-待下发 2-已下发 3- 已完成
     *
     * @return state - 1-待下发 2-已下发 3- 已完成
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置1-待下发 2-已下发 3- 已完成
     *
     * @param state 1-待下发 2-已下发 3- 已完成
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取物料编码
     *
     * @return item_code - 物料编码
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * 设置物料编码
     *
     * @param itemCode 物料编码
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 获取批次
     *
     * @return batch - 批次
     */
    public String getBatch() {
        return batch;
    }

    /**
     * 设置批次
     *
     * @param batch 批次
     */
    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getDispositionId() {
        return dispositionId;
    }

    public void setDispositionId(Integer dispositionId) {
        this.dispositionId = dispositionId;
    }

    public InventoryCheck() {
    }

    public InventoryCheck(String boxCode, String toBoxCode, Integer quantity, Integer afterCheckQuantity, Integer checkQuantity, Integer type, Integer state, String itemCode, String batch, Integer dispositionId, String cardNo) {
        this.boxCode = boxCode;
        this.toBoxCode = toBoxCode;
        this.quantity = quantity;
        this.afterCheckQuantity = afterCheckQuantity;
        this.checkQuantity = checkQuantity;
        this.type = type;
        this.state = state;
        this.itemCode = itemCode;
        this.batch = batch;
        this.dispositionId = dispositionId;
        this.cardNo = cardNo;
    }
}
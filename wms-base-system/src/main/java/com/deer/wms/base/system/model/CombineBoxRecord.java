package com.deer.wms.base.system.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "combine_box_record")
public class CombineBoxRecord {
    /**
     * 合框记录id
     */
    @Id
    @Column(name = "combine_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer combineId;

    /**
     * 从箱
     */
    @Column(name = "from_box_code")
    private String fromBoxCode;

    /**
     * 从本箱数量
     */
    @Column(name = "from_quantity")
    private Integer fromQuantity;

    /**
     * 到箱
     */
    @Column(name = "to_box_code")
    private String toBoxCode;

    /**
     * 到箱数量
     */
    @Column(name = "to_quantity")
    private Integer toQuantity;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    /**
     * 操作人工号
     */
    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "from_batch")
    private String fromBatch;

    @Column(name = "to_batch")
    private String toBatch;

    @Column(name = "item_code")
    private String itemCode;

    @Column(name = "sub_inventory_id")
    private Integer subInventoryId;
    @Column(name = "from_exp")
    private String fromExp;
    @Column(name = "to_exp")
    private String toExp;

    public Integer getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Integer subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public String getFromExp() {
        return fromExp;
    }

    public void setFromExp(String fromExp) {
        this.fromExp = fromExp;
    }

    public String getToExp() {
        return toExp;
    }

    public void setToExp(String toExp) {
        this.toExp = toExp;
    }

    public String getFromBatch() {
        return fromBatch;
    }

    public void setFromBatch(String fromBatch) {
        this.fromBatch = fromBatch;
    }

    public String getToBatch() {
        return toBatch;
    }

    public void setToBatch(String toBatch) {
        this.toBatch = toBatch;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * 获取合框记录id
     *
     * @return combine_id - 合框记录id
     */
    public Integer getCombineId() {
        return combineId;
    }

    /**
     * 设置合框记录id
     *
     * @param combineId 合框记录id
     */
    public void setCombineId(Integer combineId) {
        this.combineId = combineId;
    }

    /**
     * 获取从箱
     *
     * @return from_box_code - 从箱
     */
    public String getFromBoxCode() {
        return fromBoxCode;
    }

    /**
     * 设置从箱
     *
     * @param fromBoxCode 从箱
     */
    public void setFromBoxCode(String fromBoxCode) {
        this.fromBoxCode = fromBoxCode;
    }

    /**
     * 获取从本箱数量
     *
     * @return from_quantity - 从本箱数量
     */
    public Integer getFromQuantity() {
        return fromQuantity;
    }

    /**
     * 设置从本箱数量
     *
     * @param fromQuantity 从本箱数量
     */
    public void setFromQuantity(Integer fromQuantity) {
        this.fromQuantity = fromQuantity;
    }

    /**
     * 获取到箱
     *
     * @return to_box_code - 到箱
     */
    public String getToBoxCode() {
        return toBoxCode;
    }

    /**
     * 设置到箱
     *
     * @param toBoxCode 到箱
     */
    public void setToBoxCode(String toBoxCode) {
        this.toBoxCode = toBoxCode;
    }

    /**
     * 获取到箱数量
     *
     * @return to_quantity - 到箱数量
     */
    public Integer getToQuantity() {
        return toQuantity;
    }

    /**
     * 设置到箱数量
     *
     * @param toQuantity 到箱数量
     */
    public void setToQuantity(Integer toQuantity) {
        this.toQuantity = toQuantity;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取操作人工号
     *
     * @return card_no - 操作人工号
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * 设置操作人工号
     *
     * @param cardNo 操作人工号
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public CombineBoxRecord() {
    }

    public CombineBoxRecord(String fromBoxCode, Integer fromQuantity, String toBoxCode, Integer toQuantity, String createTime, String cardNo,String fromBatch,String toBatch,String itemCode) {
        this.fromBoxCode = fromBoxCode;
        this.fromQuantity = fromQuantity;
        this.toBoxCode = toBoxCode;
        this.toQuantity = toQuantity;
        this.createTime = createTime;
        this.cardNo = cardNo;
        this.fromBatch = fromBatch;
        this.toBatch = toBatch;
        this.itemCode = itemCode;
    }
}
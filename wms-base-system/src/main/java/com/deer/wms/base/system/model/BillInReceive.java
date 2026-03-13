package com.deer.wms.base.system.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "bill_in_receive")
public class BillInReceive {
    /**
     * 入库接收统计
     */
    @Id
    @Column(name = "bill_in_receive_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer billInReceiveId;

    /**
     * 订单Id
     */
    @Column(name = "bill_in_detail_id")
    private Integer billInDetailId;

    /**
     * 此订单接收数量总和
     */
    @Column(name = "receive_quantity")
    private Integer receiveQuantity;

    /**
     * 箱号
     */
    @Column(name = "box_code")
    private String boxCode;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    /**
     * 生产日期
     */
    private String pd;

    /**
     * 失效日期
     */
    private String exp;

    /**
     * 批次
     */
    private String batch;

    /**
     * 接收单号
     */
    @Column(name = "receipt_num")
    private String receiptNum;

    /**
     * 接收人
     */
    @Column(name = "operator_no")
    private String operatorNo;

    /**
     * 状态
     */
    private Integer state;

    @Column(name = "item_id")
    private Integer itemId;
    /**  分配行ID */
    @Column(name="po_distribution_id")
    private Integer poDistributionId;
    /** 采购订单头ID **/
    @Column(name="po_header_id")
    private Integer poHeaderId;
    /** 采购订单行ID **/
    @Column(name="po_line_id")
    private Integer poLineId;
    /** 发运行ID **/
    @Column(name="line_location_id")
    private Integer lineLocationId;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getPoDistributionId() {
        return poDistributionId;
    }

    public void setPoDistributionId(Integer poDistributionId) {
        this.poDistributionId = poDistributionId;
    }

    public Integer getPoHeaderId() {
        return poHeaderId;
    }

    public void setPoHeaderId(Integer poHeaderId) {
        this.poHeaderId = poHeaderId;
    }

    public Integer getPoLineId() {
        return poLineId;
    }

    public void setPoLineId(Integer poLineId) {
        this.poLineId = poLineId;
    }

    public Integer getLineLocationId() {
        return lineLocationId;
    }

    public void setLineLocationId(Integer lineLocationId) {
        this.lineLocationId = lineLocationId;
    }

    /**
     * 获取入库接收统计
     *
     * @return bill_in_receive_id - 入库接收统计
     */
    public Integer getBillInReceiveId() {
        return billInReceiveId;
    }

    /**
     * 设置入库接收统计
     *
     * @param billInReceiveId 入库接收统计
     */
    public void setBillInReceiveId(Integer billInReceiveId) {
        this.billInReceiveId = billInReceiveId;
    }

    /**
     * 获取订单Id
     *
     * @return bill_in_detail_id - 订单Id
     */
    public Integer getBillInDetailId() {
        return billInDetailId;
    }

    /**
     * 设置订单Id
     *
     * @param billInDetailId 订单Id
     */
    public void setBillInDetailId(Integer billInDetailId) {
        this.billInDetailId = billInDetailId;
    }

    /**
     * 获取此订单接收数量总和
     *
     * @return receive_quantity - 此订单接收数量总和
     */
    public Integer getReceiveQuantity() {
        return receiveQuantity;
    }

    /**
     * 设置此订单接收数量总和
     *
     * @param receiveQuantity 此订单接收数量总和
     */
    public void setReceiveQuantity(Integer receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    /**
     * 获取箱号
     *
     * @return box_code - 箱号
     */
    public String getBoxCode() {
        return boxCode;
    }

    /**
     * 设置箱号
     *
     * @param boxCode 箱号
     */
    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
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

    /**
     * 获取接收单号
     *
     * @return receipt_num - 接收单号
     */
    public String getReceiptNum() {
        return receiptNum;
    }

    /**
     * 设置接收单号
     *
     * @param receiptNum 接收单号
     */
    public void setReceiptNum(String receiptNum) {
        this.receiptNum = receiptNum;
    }

    /**
     * 获取接收人
     *
     * @return operator_no - 接收人
     */
    public String getOperatorNo() {
        return operatorNo;
    }

    /**
     * 设置接收人
     *
     * @param operatorNo 接收人
     */
    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    /**
     * 获取状态
     *
     * @return state - 状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(Integer state) {
        this.state = state;
    }

    public BillInReceive() {
    }

    public BillInReceive(Integer billInDetailId, Integer receiveQuantity, String boxCode, String createTime, String pd, String exp, String batch, String operatorNo, Integer state, Integer itemId, Integer poDistributionId, Integer poHeaderId, Integer poLineId, Integer lineLocationId) {
        this.billInDetailId = billInDetailId;
        this.receiveQuantity = receiveQuantity;
        this.boxCode = boxCode;
        this.createTime = createTime;
        this.pd = pd;
        this.exp = exp;
        this.batch = batch;
        this.operatorNo = operatorNo;
        this.state = state;
        this.itemId = itemId;
        this.poDistributionId = poDistributionId;
        this.poHeaderId = poHeaderId;
        this.poLineId = poLineId;
        this.lineLocationId = lineLocationId;
    }
}
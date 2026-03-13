package com.deer.wms.base.system.model;

import com.deer.wms.common.annotation.Excel;

import java.util.Date;
import javax.persistence.*;

@Table(name = "delay_worker_order")
public class DelayWorkerOrder {
    /**
     * 延期库存工单
     */
    @Id
    @Column(name = "delay_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer delayId;

    /**
     * 工单Id
     */
    @Excel(name="工单Id",type=Excel.Type.ALL,column = 0)
    @Column(name = "worker_order_id")
    private String workerOrderId;

    /**
     * 料号
     */
    @Excel(name="料号",type=Excel.Type.ALL,column = 1)
    @Column(name = "item_code")
    private String itemCode;

    /**
     * 批次
     */
    @Excel(name="批次",type=Excel.Type.ALL,column = 2)
    private String batch;

    /**
     * 延期日期
     */
    @Excel(name="失效日期",type=Excel.Type.ALL,column = 3)
    private String exp;

    @Column(name="operator_no")
    private String operatorNo;

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    /**
     * 获取延期库存工单
     *
     * @return delay_id - 延期库存工单
     */
    public Integer getDelayId() {
        return delayId;
    }

    /**
     * 设置延期库存工单
     *
     * @param delayId 延期库存工单
     */
    public void setDelayId(Integer delayId) {
        this.delayId = delayId;
    }

    /**
     * 获取工单Id
     *
     * @return worker_order_id - 工单Id
     */
    public String getWorkerOrderId() {
        return workerOrderId;
    }

    /**
     * 设置工单Id
     *
     * @param workerOrderId 工单Id
     */
    public void setWorkerOrderId(String workerOrderId) {
        this.workerOrderId = workerOrderId;
    }

    /**
     * 获取料号
     *
     * @return item_code - 料号
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * 设置料号
     *
     * @param itemCode 料号
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

    /**
     * 获取延期日期
     *
     * @return exp - 延期日期
     */
    public String getExp() {
        return exp;
    }

    /**
     * 设置延期日期
     *
     * @param exp 延期日期
     */
    public void setExp(String exp) {
        this.exp = exp;
    }
}
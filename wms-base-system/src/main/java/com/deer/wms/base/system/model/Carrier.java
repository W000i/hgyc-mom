package com.deer.wms.base.system.model;

import com.deer.wms.common.annotation.Excel;

import javax.persistence.*;

public class Carrier {
    /**
     * AGV载具id
     */
    @Id
    @Column(name = "carrier_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer carrierId;

    /**
     * 载具编码
     *
     */
    @Excel(name="载具号",type=Excel.Type.EXPORT,column = 0)
    @Column(name = "carrier_code")
    private String carrierCode;

    /**
     * 载具状态
     * 1.在输送线上的空载具
     * 2.此载具已出库
     */
    @Excel(name="类型",type=Excel.Type.EXPORT,column = 4,readConverterExp = "1=排队中,2=已出库")
    @Column(name = "carrier_state")
    private Integer carrierState;

    /**
     * 运送载具的Mes任务号
     */
    @Column(name = "carrier_task_id")
    private String carrierTaskId;
    @Excel(name="载具入库时间",type=Excel.Type.EXPORT,column = 2)
    @Column(name="create_time")
    private String createTime;
    @Column(name="code")
    private String code;
    @Excel(name="载具出库时间",type=Excel.Type.EXPORT,column = 3)
    @Column(name="time")
    private String time;

    @Column(name="bill_out_detail_id")
    private Integer billOutDetailId;

    public Integer getBillOutDetailId() {
        return billOutDetailId;
    }

    public void setBillOutDetailId(Integer billOutDetailId) {
        this.billOutDetailId = billOutDetailId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取AGV载具id
     *
     * @return carrier_id - AGV载具id
     */
    public Integer getCarrierId() {
        return carrierId;
    }

    /**
     * 设置AGV载具id
     *
     * @param carrierId AGV载具id
     */
    public void setCarrierId(Integer carrierId) {
        this.carrierId = carrierId;
    }

    /**
     * 获取载具编码
     *
     * @return carrier_code - 载具编码
     */
    public String getCarrierCode() {
        return carrierCode;
    }

    /**
     * 设置载具编码
     *
     * @param carrierCode 载具编码
     */
    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    /**
     * 获取载具状态
     *
     * @return carrier_state - 载具状态
     */
    public Integer getCarrierState() {
        return carrierState;
    }

    /**
     * 设置载具状态
     *
     * @param carrierState 载具状态
     */
    public void setCarrierState(Integer carrierState) {
        this.carrierState = carrierState;
    }

    /**
     * 获取运送载具的任务号
     *
     * @return carrier_task_id - 运送载具的任务号
     */
    public String getCarrierTaskId() {
        return carrierTaskId;
    }

    /**
     * 设置运送载具的任务号
     *
     * @param carrierTaskId 运送载具的任务号
     */
    public void setCarrierTaskId(String carrierTaskId) {
        this.carrierTaskId = carrierTaskId;
    }

    public Carrier() {
    }

    public Carrier(String carrierCode, Integer carrierState, String carrierTaskId ,String createTime) {
        this.carrierCode = carrierCode;
        this.carrierState = carrierState;
        this.carrierTaskId = carrierTaskId;
        this.createTime = createTime;
    }
}
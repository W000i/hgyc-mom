package com.deer.wms.base.system.model;

import com.deer.wms.common.annotation.Excel;

import javax.persistence.*;

public class Operator {
    /**
     * 入库操作员id 
     */
    @Id
    @Column(name = "operator_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer operatorId;
    /**
     * 操作员名称
     */
    @Excel(name="操作员名称",type=Excel.Type.EXPORT,column = 0)
    @Column(name = "operator_name")
    private String operatorName;
    /**
     * 操作员卡号
     */
    @Excel(name="操作员卡号",type=Excel.Type.EXPORT,column = 1)
    @Column(name = "operator_card")
    private String operatorCard;

    /**
     * 操作员工号
     */
    @Excel(name="操作员工号",type=Excel.Type.EXPORT,column = 2)
    @Column(name = "emp_no")
    private String empNo;
    /**
     * 初始化权限
     * 1-无
     * 2-有
     */
    @Excel(name="初始化权限",type=Excel.Type.EXPORT,column = 3,readConverterExp = "1=无,2=有")
    @Column(name="autoverify_permission")
    private Integer autoverifyPermission;
    /**
     * 退库权限
     * 1-无
     * 2-有
     */
    @Excel(name="退库权限",type=Excel.Type.EXPORT,column = 4,readConverterExp = "1=无,2=有")
    @Column(name="back_ware_permission")
    private Integer backWarePermission;
    /**
     * 合框权限
     * 1-无
     * 2-有
     */
    @Excel(name="合框权限",type=Excel.Type.EXPORT,column = 5,readConverterExp = "1=无,2=有")
    @Column(name="combine_permission")
    private Integer combinePermission;
    /**
     * 退货权限
     * 1-无
     * 2-有
     */
    @Excel(name="退货权限",type=Excel.Type.EXPORT,column = 6,readConverterExp = "1=无,2=有")
    @Column(name="return_item_permission")
    private Integer returnItemPermission;
    /**
     * 品质异常检验权限
     * 1-无
     * 2-有
     */
    @Excel(name="品质异常检验权限",type=Excel.Type.EXPORT,column = 7,readConverterExp = "1=无,2=有")
    @Column(name="quality_check_permission")
    private Integer qualityCheckPermission;
    /**
     * 报废权限
     * 1-无
     * 2-有
     */
    @Excel(name="报废权限",type=Excel.Type.EXPORT,column = 8,readConverterExp = "1=无,2=有")
    @Column(name="scrap_permission")
    private Integer scrapPermission;
    /**
     * 非工单出库权限
     * 1-无
     * 2-有
     */
    @Excel(name="非工单出库权限",type=Excel.Type.EXPORT,column = 9,readConverterExp = "1=无,2=有")
    @Column(name="manual_out_permission")
    private Integer manualOutPermission;

    /**
     * 转库权限
     * 1-无
     * 2-有
     */
    @Excel(name="转库权限",type=Excel.Type.EXPORT,column = 10,readConverterExp = "1=无,2=有")
    @Column(name="transfer_warehouse_permission")
    private Integer transferWarehousePermission;

    /**
     *  注销标志
     *  1-正常使用
     *  2-注销
     */
    @Column(name="logout_flag")
    private Integer logoutFlag;

    /**
     *  多批次合框权限
     *  1-无
     *  2-有
     * @return
     */
    @Excel(name="多批次合框权限",type=Excel.Type.EXPORT,column = 11,readConverterExp = "1=无,2=有")
    @Column(name="many_batch_permission")
    private Integer manyBatchPermission;

    /**
     * 盘点权限
     * 1-无
     * 2-有
     * @return
     */
    @Excel(name="盘点权限",type=Excel.Type.EXPORT,column = 12,readConverterExp = "1=无,2=有")
    @Column(name="check_permission")
    private Integer checkPermission;

    public Integer getCheckPermission() {
        return checkPermission;
    }

    public void setCheckPermission(Integer checkPermission) {
        this.checkPermission = checkPermission;
    }

    public Integer getManyBatchPermission() {
        return manyBatchPermission;
    }

    public void setManyBatchPermission(Integer manyBatchPermission) {
        this.manyBatchPermission = manyBatchPermission;
    }

    public Integer getTransferWarehousePermission() {
        return transferWarehousePermission;
    }

    public void setTransferWarehousePermission(Integer transferWarehousePermission) {
        this.transferWarehousePermission = transferWarehousePermission;
    }

    public Integer getLogoutFlag() {
        return logoutFlag;
    }

    public void setLogoutFlag(Integer logoutFlag) {
        this.logoutFlag = logoutFlag;
    }

    public Integer getBackWarePermission() {
        return backWarePermission;
    }

    public void setBackWarePermission(Integer backWarePermission) {
        this.backWarePermission = backWarePermission;
    }

    public Integer getCombinePermission() {
        return combinePermission;
    }

    public void setCombinePermission(Integer combinePermission) {
        this.combinePermission = combinePermission;
    }

    public Integer getReturnItemPermission() {
        return returnItemPermission;
    }

    public void setReturnItemPermission(Integer returnItemPermission) {
        this.returnItemPermission = returnItemPermission;
    }

    public Integer getQualityCheckPermission() {
        return qualityCheckPermission;
    }

    public void setQualityCheckPermission(Integer qualityCheckPermission) {
        this.qualityCheckPermission = qualityCheckPermission;
    }

    public Integer getScrapPermission() {
        return scrapPermission;
    }

    public void setScrapPermission(Integer scrapPermission) {
        this.scrapPermission = scrapPermission;
    }

    public Integer getManualOutPermission() {
        return manualOutPermission;
    }

    public void setManualOutPermission(Integer manualOutPermission) {
        this.manualOutPermission = manualOutPermission;
    }

    public Integer getAutoverifyPermission() {
        return autoverifyPermission;
    }

    public void setAutoverifyPermission(Integer autoverifyPermission) {
        this.autoverifyPermission = autoverifyPermission;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * 获取入库操作员id 
     *
     * @return operator_id - 入库操作员id 
     */
    public Integer getOperatorId() {
        return operatorId;
    }

    /**
     * 设置入库操作员id 
     *
     * @param operatorId 入库操作员id 
     */
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return operator_name
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * @return operator_card
     */
    public String getOperatorCard() {
        return operatorCard;
    }

    /**
     * @param operatorCard
     */
    public void setOperatorCard(String operatorCard) {
        this.operatorCard = operatorCard;
    }
}
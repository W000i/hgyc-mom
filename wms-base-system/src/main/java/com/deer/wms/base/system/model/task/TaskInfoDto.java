package com.deer.wms.base.system.model.task;


import com.deer.wms.common.annotation.Excel;

/**
 * 任务表 task_info
 * 
 * @author cai
 * @date 2019-07-03		用于任务表关联查询单据号 ， 现已作废
 */


public class TaskInfoDto extends TaskInfo
{
	private Integer billId;
	private String itemCode;
	private String priority;
	@Excel(name="工单Id",type=Excel.Type.EXPORT,column = 4)
	private String billNo;
	@Excel(name="操作人员",type=Excel.Type.EXPORT,column = 5)
	private String operatorName;
	private Integer inventoryItemId;
	private String unit;

	private String batch;
	private String pd;
	private String exp;
	private String inTime;
	private Double height;

	private Integer billMasterType;

	public Integer getBillMasterType() {
		return billMasterType;
	}

	public void setBillMasterType(Integer billMasterType) {
		this.billMasterType = billMasterType;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
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

	public String getInTime() {
		return inTime;
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public Integer getBillId() {
		return billId;
	}

	public void setBillId(Integer billId) {
		this.billId = billId;
	}

	public Integer getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(Integer inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}

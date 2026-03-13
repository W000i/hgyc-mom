package com.deer.wms.base.system.model.bill;


/**
 * 入库单表 bill_in_master
 * 
 * @author guo
 * @date 2019-05-13
 */
public class BillOutMasterDto extends BillOutMaster
{
	 private String wareName;
	 private String accountAlias;
	 private String itemCode;
	 private Integer quantity;
	 private String priority;
	 private Integer alreadyOutQuantity;
	 private Integer billOutDetailId;
	 private Integer sequence;
	 private String taskId;
	 private String finishedCode;

	public String getFinishedCode() {
		return finishedCode;
	}

	public void setFinishedCode(String finishedCode) {
		this.finishedCode = finishedCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getBillOutDetailId() {
		return billOutDetailId;
	}

	public void setBillOutDetailId(Integer billOutDetailId) {
		this.billOutDetailId = billOutDetailId;
	}

	public Integer getAlreadyOutQuantity() {
		return alreadyOutQuantity;
	}

	public void setAlreadyOutQuantity(Integer alreadyOutQuantity) {
		this.alreadyOutQuantity = alreadyOutQuantity;
	}

	public String getWareName() {
		return wareName;
	}

	public void setWareName(String wareName) {
		this.wareName = wareName;
	}

	public String getAccountAlias() {
		return accountAlias;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
}

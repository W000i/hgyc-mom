package com.deer.wms.base.system.model.task;


import com.deer.wms.common.annotation.Excel;
import com.deer.wms.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

/**
 * 任务表 task_info
 * 
 * @author guo
 * @date 2019-06-03
 */


@Table(name = "task_info")
public class TaskInfo
{

	/** ID */
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** GUID */
	private String taskId;
	/** 起始位置 */
	@Excel(name="起始位置",type=Excel.Type.EXPORT,column = 0)
	private String startPosition;
	/** 结束位置 */
	@Excel(name="结束位置",type=Excel.Type.EXPORT,column = 1)
	private String endPosition;
	/**
	 * 任务类型:
     *
	 * <option value="1">出空筐或半筐到入库口</option>
	 * <option value="2">入库口入空筐</option>
	 * <option value="3">入库口入有料筐</option>
	 * <option value="11">出筐到点数机</option>
	 * <option value="12">点数任务执行</option>
	 * <option value="13">点数机出筐回货架</option>
	 * <option value="14">载具出货到出货口</option>
	 * <option value="15">异常处理位调度中</option>
	 * <option value="21">盘点任务(待盘点物料出库)</option>
	 * <option value="22">盘点任务(空筐出库)</option>
	 * <option value="23">盘点任务(点数任务执行)</option>
	 * <option value="24">盘点任务(空筐回货架)</option>
	 * <option value="25">盘点任务(满筐至入库口)</option>
	 * <option value="26">盘点任务(贴标回库)</option>
	 *
	 */

	@Excel(name="类型",type=Excel.Type.EXPORT,column = 3,readConverterExp = "1=出空筐或半筐到入库口\n" +
			",2=入库口入空筐\n" +
			",3=入库口入有料筐\n" +
			",11=出筐到点数机\n" +
			",12=点数任务执行\n" +
			",13=点数机出筐回货架\n" +
			",14=载具出货到出货口\n" +
			",15=异常处理位调度中\n" +
			",21=盘点任务(待盘点物料出库)\n" +
			",22=盘点任务(空筐出库)\n" +
			",23=盘点任务(点数任务执行)\n" +
			",24=盘点任务(空筐回货架)\n" +
			",25=盘点任务(满筐至入库口)\n" +
			",26=盘点任务(贴标回库)")
	private Integer type;
	/**
	 * 状态
	 * 		0-已下发
	 * 		1-执行中
	 * 		2-报错
	 * 		3-已完成
	 */
	@Excel(name="类型",type=Excel.Type.EXPORT,column = 8,readConverterExp = "0=待下发,1=执行中,2=异常,3=已完成,5=执行中")
	private Integer state;
	/** 点数数量 */
	private Integer quantity;
	/** 完成数量 */
	private Integer completeQuantity;
	/** 条码信息（贴标使用） */
	private String barCode;
	/** 托盘/料箱编码 */
	@Excel(name="箱号",type=Excel.Type.EXPORT,column = 2)
	private String boxCode;
	/** 外键 */
	private Integer billInDetailId;
	/** 是否置顶  0-否 1-是 */
	@Column(name = "istop")
	private String isTop;
	/** 任务开始时间 */
	@Column(name="card_no")
	private String cardNo;
	/** 任务开始时间 */
	@Excel(name="任务开始时间",type=Excel.Type.EXPORT,column = 6)
	@Column(name = "task_start_time")
	private String taskStartTime;
	/** 任务结束时间 * */
	@Excel(name="任务结束时间",type=Excel.Type.EXPORT,column = 7)
	@Column(name = "task_end_time")
	private String taskEndTime;

	//盘点单Id
	@Column(name="inventory_check_id")
	private Integer inventoryCheckId;

	public Integer getInventoryCheckId() {
		return inventoryCheckId;
	}

	public void setInventoryCheckId(Integer inventoryCheckId) {
		this.inventoryCheckId = inventoryCheckId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getTaskStartTime() {
		return taskStartTime;
	}

	public void setTaskStartTime(String taskStartTime) {
		this.taskStartTime = taskStartTime;
	}

	public String getTaskEndTime() {
		return taskEndTime;
	}

	public void setTaskEndTime(String taskEndTime) {
		this.taskEndTime = taskEndTime;
	}

	private Integer billOutDetailId;

	public Integer getBillOutDetailId() {
		return billOutDetailId;
	}

	public void setBillOutDetailId(Integer billOutDetailId) {
		this.billOutDetailId = billOutDetailId;
	}

	public String getIsTop() {
		return isTop;
	}

	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(String startPosition) {
		this.startPosition = startPosition;
	}

	public String getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(String endPosition) {
		this.endPosition = endPosition;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getCompleteQuantity() {
		return completeQuantity;
	}

	public void setCompleteQuantity(Integer completeQuantity) {
		this.completeQuantity = completeQuantity;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getBillInDetailId() {
		return billInDetailId;
	}

	public void setBillInDetailId(Integer billInDetailId) {
		this.billInDetailId = billInDetailId;
	}

	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("taskId", getTaskId())
            .append("startPosition", getStartPosition())
            .append("endPosition", getEndPosition())
            .append("type", getType())
            .append("state", getState())
            .append("quantity", getQuantity())
            .append("completeQuantity", getCompleteQuantity())
            .append("barCode", getBarCode())
            .append("boxNo", getBoxCode())
			.append("cardNo",getCardNo())
			.append("taskStartTime",getTaskStartTime())
			.append("taskEndTime",getTaskEndTime())
            .toString();
    }

	public TaskInfo() {

	}

	public TaskInfo(Integer id,String taskId, String startPosition, String endPosition, Integer type, Integer state, Integer quantity,String boxCode) {
		this.id = id;
		this.taskId = taskId;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.type = type;
		this.state = state;
		this.quantity = quantity;
		this.boxCode = boxCode;
	}

	public TaskInfo(String taskId, String startPosition, String endPosition, Integer type, Integer state, Integer quantity, String boxCode, String isTop, Integer billOutDetailId) {
		this.taskId = taskId;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.type = type;
		this.state = state;
		this.quantity = quantity;
		this.boxCode = boxCode;
		this.isTop = isTop;
		this.billOutDetailId = billOutDetailId;
	}

	public TaskInfo(String taskId, String startPosition, String endPosition, Integer type, Integer state, Integer quantity, String boxCode, String isTop, String cardNo, String taskStartTime) {
		this.taskId = taskId;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.type = type;
		this.state = state;
		this.quantity = quantity;
		this.boxCode = boxCode;
		this.isTop = isTop;
		this.cardNo = cardNo;
		this.taskStartTime = taskStartTime;
	}
}

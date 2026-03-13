package com.deer.wms.base.system.model.task;


/**
 * 任务表 task_info
 * 
 * @author cai
 * @date 2019-07-03		用于任务表关联查询单据号 ， 现已作废
 */


public class TotalSevenDays
{
//	时间
	private String times;
//	入库料号数量
	private Integer totalItemCodeQuantity;
//	入库箱数
	private Integer totalBoxQuantity;
//	入库总数
	private Integer totalQuantity;

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public Integer getTotalItemCodeQuantity() {
		return totalItemCodeQuantity;
	}

	public void setTotalItemCodeQuantity(Integer totalItemCodeQuantity) {
		this.totalItemCodeQuantity = totalItemCodeQuantity;
	}

	public Integer getTotalBoxQuantity() {
		return totalBoxQuantity;
	}

	public void setTotalBoxQuantity(Integer totalBoxQuantity) {
		this.totalBoxQuantity = totalBoxQuantity;
	}

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}
}

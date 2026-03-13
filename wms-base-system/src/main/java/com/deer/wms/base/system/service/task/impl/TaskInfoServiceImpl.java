package com.deer.wms.base.system.service.task.impl;

import com.alibaba.fastjson.JSONArray;
import com.deer.wms.base.system.dao.task.TaskInfoMapper;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.task.*;
import com.deer.wms.base.system.model.threeDimensional.Task;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.rabbitMQ.MsgProducer;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.common.core.text.Convert;
import com.deer.wms.common.json.JSONObject;
import com.deer.wms.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务 服务层实现
 * 
 * @author guo
 * @date 2019-06-03
 */
@Service
public class TaskInfoServiceImpl extends AbstractService<TaskInfo, Integer> implements ITaskInfoService
{
	@Autowired
	private TaskInfoMapper taskInfoMapper;

	@Autowired
	private ICellInfoService cellInfoService;

	@Autowired
	private IBoxItemService boxItemService;

	@Autowired
	private ITaskInfoService taskInfoService;

	@Autowired
	private BoxInfoService boxInfoService;

	@Autowired
	private PickTaskService pickTaskService;

	@Autowired
	private MsgProducer msgProducer;

	/**
	 *
	 *	根据出库单id查找出库单的所有任务信息
	 *
	 * @param billId
	 * @return
	 */
	@Override
	public List<TaskInfo> selectTaskInfoByBillOutMasterId(Integer billId) {

		return taskInfoMapper.selectTaskInfoByBillOutMasterId(billId);
	}


	/**
	 * WCS完成任务后的回调处理
	 *
	 * @param taskId
	 * @param state
	 */
	@Override
	public void finishTask(String taskId, Integer state) {

		TaskInfo taskInfo = taskInfoService.getTaskInfoByTaskId(taskId);
		if(taskInfo != null){

			taskInfo.setState(state);
			taskInfoService.updateTaskInfo(taskInfo);

			//获得该任务的类型(可参考数据库注释)
			int type = taskInfo.getType();

			//1-入库任务(将空/半空托盘从货位上移到固定位置)
			if(state == 3 && type == 1){


//				System.out.println("44444444444444444444444444444444");
//				System.out.println("44444444444444444444444444444444");
//				System.out.println("44444444444444444444444444444444");


//				//将货位状态改成0
//				CellInfo cellInfo = cellInfoService.getCellInfoByTaskId(taskId);
//				cellInfo.setState(0);
//				cellInfoService.updateCellInfo(cellInfo);
//
//				//将容器表中的boxCellId改为null
//				BoxInfo boxInfo = boxInfoService.getBoxInfoByTaskId(taskId);
//				boxInfo.setBoxCellId(null);
//				//设置托盘状态为0，托盘离开货位
//				boxInfo.setBoxState(0);
//				boxInfoService.update(boxInfo);

			}

			//2-入库任务(当托盘已经到达装货点，装货完毕后   寻找合适的货位返回)
			if(state == 3 && type == 2){

				//将货位状态改成0
				CellInfo cellInfo = cellInfoService.getCellInfoByTaskId(taskId);

				int cellId = cellInfo.getCellId();

				cellInfo.setState(1);
				cellInfoService.updateCellInfo(cellInfo);

				//将容器表中的boxCellId改为cellId
				BoxInfo boxInfo = boxInfoService.getBoxInfoByTaskId(taskId);
				boxInfo.setBoxCellId(cellId);
				//设置托盘状态为1，托盘进入货位
				boxInfo.setBoxState(1);
				boxInfoService.update(boxInfo);

			}

			//3-出库任务(根据货物编码  以及需要出库的数量，生成需要的N条任务信息，将合适的托盘移到固定位置/卸货位置)
			if(state == 3 && type == 3){

				//将货位状态改为0
				CellInfo cellInfo = cellInfoService.getCellInfoByTaskId(taskId);
				cellInfo.setState(0);
				cellInfoService.updateCellInfo(cellInfo);

				//将托盘中的数量清零
				BoxItem boxItem = boxItemService.getBoxItemByTaskId(taskId);
				boxItem.setQuantity(0);
				//设置托盘状态为0，托盘离开货位
				BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());
				boxInfo.setBoxState(0);
				boxInfoService.update(boxInfo);
			}

			//4-根据托盘编码 ， 将托盘放置空货位上
			if(state == 3 && type == 4){

				//将货位状态改为1
				CellInfo cellInfo = cellInfoService.getCellInfoByTaskId(taskId);
				cellInfo.setState(1);
				cellInfoService.updateCellInfo(cellInfo);

				int cellId = cellInfo.getCellId();
				//向该货位增加托盘， 根据托盘编码查出该托盘，并将托盘表boxInfo中的  货位id字段绑定
				BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());
				boxInfo.setBoxCellId(cellId);
				//设置托盘状态为1，托盘进入货位
				boxInfo.setBoxState(1);
				boxInfoService.update(boxInfo);

			}

			//5-出库任务-将有合适货物的托盘调度到出货口(固定位置)
			if(state == 3 && type == 5){

				//将货位状态改为0
				CellInfo cellInfo = cellInfoService.getCellInfoByTaskId(taskId);
				cellInfo.setState(0);
				cellInfoService.updateCellInfo(cellInfo);

				//将托盘表中绑定的cellid设置为null  解绑
				BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());
				boxInfo.setBoxCellId(null);
				//设置托盘状态为0，托盘离开货位
				boxInfo.setBoxState(0);
				boxInfoService.update(boxInfo);

				//将托盘中的数量设置为(原有的数量 - pick的数量)
				BoxItem boxItem = boxItemService.getBoxItemByBoxCode(taskInfo.getBoxCode());
//				PickTask pickTask = pickTaskService.getPickTaskByBoxItemId(boxItem.getId());
//				boxItem.setQuantity(boxItem.getQuantity()-pickTask.getPickQuantity());
				boxItemService.updateBoxItem(boxItem);


			}


		}

	}

	/**
	 * 根据任务ID查询任务信息，便于更改状态值
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public TaskInfo getTaskInfoByTaskId(String taskId) {

		return taskInfoMapper.getTaskInfoByTaskId(taskId);
	}



	/**
	 *根据billInMasterId查询任务列表
	 *
	 * @param billId
	 * @return
	 */
	@Override
	public List<TaskInfo> selectTaskInfoByBillInMasterId(Integer billId) {

		return taskInfoMapper.selectTaskInfoByBillInMasterId(billId);
	}

	/**
	 * 查询状态值为0的任务，(为WCS提供接口)
	 *
	 * @return
	 */
	@Override
	public List<TaskInfoWcs> selectTaskInfoForWcsByState() {

		return taskInfoMapper.selectTaskInfoForWcsByState();
	}

	/**
     * 查询任务信息
     * 
     * @param id 任务ID
     * @return 任务信息
     */
    @Override
	public TaskInfo selectTaskInfoById(Integer id)
	{
	    return taskInfoMapper.selectTaskInfoById(id);
	}
	
	/**
     * 查询任务列表
     * 
     * @param taskInfo 任务信息
     * @return 任务集合
     */
	@Override
	public List<TaskInfo> selectTaskInfoList(TaskInfo taskInfo)
	{
	    return taskInfoMapper.selectTaskInfoList(taskInfo);
	}


    /**
     * 新增任务
     * 
     * @param taskInfo 任务信息
     * @return 结果
     */
	@Override
	public int insertTaskInfo(TaskInfo taskInfo)
	{
	    return taskInfoMapper.insertTaskInfo(taskInfo);
	}
	
	/**
     * 修改任务
     * 
     * @param taskInfo 任务信息
     * @return 结果
     */
	@Override
	public int updateTaskInfo(TaskInfo taskInfo)
	{
	    return taskInfoMapper.updateTaskInfo(taskInfo);
	}

	/**
     * 删除任务对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	@Override
	public int deleteTaskInfoByIds(String ids)
	{
		return taskInfoMapper.deleteTaskInfoByIds(Convert.toStrArray(ids));
	}

	/**
	 * 根据参数查询数据接口
	 */
	@Override
	public List<TaskInfoDto> findList(TaskInfoCriteria criteria){
		return taskInfoMapper.findList(criteria);
	}

	@Override
	public TaskInfo findByItemCodeAndBatchAndExp(TaskInfoCriteria criteria){
		return taskInfoMapper.findByItemCodeAndBatchAndExp(criteria);
	}

	@Override
	public void save(TaskInfo taskInfo){
		if(taskInfo.getType().equals(TaskTypeConstant.IN_NULL_BOX) || taskInfo.getType().equals(TaskTypeConstant.IN_AVAILABLE_BOX)){
			taskInfo.setTaskEndTime(DateUtils.getTime());
			String taskEndTime = taskInfoMapper.findByBoxCodeLastTask(taskInfo.getBoxCode());
			if(taskEndTime != null){
				taskInfo.setTaskStartTime(taskEndTime);
			}else{
				taskInfo.setTaskStartTime(DateUtils.getTime());
			}
		}else{
			taskInfo.setTaskStartTime(DateUtils.getTime());
		}
		super.save(taskInfo);
		msgProducer.sendMsg(JSONArray.toJSONString(
				new TaskInfoWcs(taskInfo.getId().toString(),taskInfo.getTaskId(),taskInfo.getStartPosition(),taskInfo.getEndPosition(),
						taskInfo.getType().toString(),taskInfo.getState().toString(), DateUtils.getTime(),taskInfo.getIsTop(),
						taskInfo.getBarCode(),taskInfo.getQuantity(), taskInfo.getBoxCode()
		)));
	}

	@Override
	public List<TaskInfoDto> findByType(TaskInfoCriteria criteria){
		return taskInfoMapper.findByType(criteria);
	}

	@Override
	public List<TotalSevenDays> totalSevenDays(){
		return taskInfoMapper.totalSevenDays();
	}

	@Override
	public List<TaskInfo> findByTypeAndState(){
		return taskInfoMapper.findByTypeAndState();
	}

	@Override
	public List<Task> findByStateAndType(TaskInfoCriteria criteria){
		return taskInfoMapper.findByStateAndType(criteria);
	}

	@Override
	public boolean judgeWhetherCheckTaskInfo(){
		List<Task> tasks = taskInfoMapper.findByStateAndType(new TaskInfoCriteria());
		if(tasks.size()>0){
			for(Task task : tasks){
				if(task.getType().equals(TaskTypeConstant.CHECK_CELL_TO_PAPER_COUNTERS_LEFT)
						|| task.getType().equals(TaskTypeConstant.CHECK_NULL_BOX_TO_PAPER_COUNTERS_RIGHT)
						|| task.getType().equals(TaskTypeConstant.CHECK_COUNT)
						|| task.getType().equals(TaskTypeConstant.CHECK_LEFT_BOX_TO_CELL_FROM_PAPERS_COUNTERS)
						|| task.getType().equals(TaskTypeConstant.CHECK_RIGHT_BOX_TO_LABELER_FROM_PAPERS_COUNTERS)
						|| task.getType().equals(TaskTypeConstant.CHECK_TO_CELL_FROM_LABELER)
				){
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String a = JSONArray.toJSONString(
				new TaskInfoWcs("1234","1234","010101","020202", "11", "0", DateUtils.getTime().toString(), "1", "", 55, "FTB002"));

		System.out.println(a);

	}

}

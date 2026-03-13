package com.deer.wms.base.system.service.ware.impl;

import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.bill.BillOutDetail;
import com.deer.wms.base.system.model.bill.BillOutMasterCriteria;
import com.deer.wms.base.system.model.bill.BillOutMasterDto;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.*;
import com.deer.wms.base.system.model.threeDimensional.Cell;
import com.deer.wms.base.system.model.ware.CellInfoCriteria;
import com.deer.wms.base.system.model.ware.WareInfo;
import com.deer.wms.base.system.service.RequestIdAutoService;
import com.deer.wms.base.system.service.RequestIdService;
import com.deer.wms.base.system.service.SubInventoryService;
import com.deer.wms.base.system.service.bill.IBillOutDetailService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.IWareInfoService;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.common.core.text.Convert;
import com.deer.wms.base.system.dao.ware.CellInfoMapper;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.GuidUtils;
import com.deer.wms.framework.util.MyUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 货位设置 服务层实现
 * 
 * @author deer
 * @date 2019-05-08
 */
@Service
public class CellInfoServiceImpl  extends AbstractService<CellInfo, Integer> implements ICellInfoService
{
	@Autowired
	private CellInfoMapper cellInfoMapper;

	@Autowired
	private IBoxItemService boxItemService;

	@Autowired
	private ITaskInfoService taskInfoService;

	@Autowired
	private BoxInfoService boxInfoService;

	@Autowired
	private IBillOutMasterService billOutMasterService;

	@Autowired
	private PickTaskService pickTaskService;

	@Autowired
	private IBillOutDetailService billOutDetailService;

	@Autowired
	private IItemInfoService itemInfoService;
	@Autowired
	private RequestIdAutoService requestIdAutoService;
	@Autowired
	private RequestIdService requestIdService;
	@Autowired
	private SubInventoryService subInventoryService;

	/**
	 * 将坐标值封装格式
	 *
	 * @param str
	 * @return
	 */
	@Override
	public String toStringForWcs(String str) {

		String[] strs = str.split(":");


		String s1 = strs[0];
		if(s1.length() == 1){

			s1 = "0" + s1;
		}

		String s2 = strs[1];
		if(s2.length() == 1){

			s2 = "00" + s2;
		}else if(s2.length() == 2){

			s2 = "0" +s2;
		}

		String s3 = strs[2];
		if(s3.length() == 1){

			s3 = "00" + s3;
		}else if(s3.length() == 2){

			s3 = "0" + s3;
		}
		String newStr = s1 + s2 + s3;

		return newStr;
	}


	/**
	 * 根据任务id查找货位信息
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public CellInfo getCellInfoByTaskId(String taskId) {

		return cellInfoMapper.getCellInfoByTaskId(taskId);
	}

	/**
	 * 查询没有托盘的货位  排序查第一个
	 *
	 * @return
	 */
	@Override
	public CellInfo getCellInfoHasNoBoxInfo() {

		return cellInfoMapper.getCellInfoHasNoBoxInfo();
	}

	/**
	 * 根据物料名，物料编码，批次 查找货位相关信息
	 *
	 * @return
	 */
	@Override
	public List<CellInfoDto> findCellInfoDtoByItemNameAndItemCodeAndBatch(String itemName, String itemCode, String batch) {

		return cellInfoMapper.findCellInfoDtoByItemNameAndItemCodeAndBatch(itemName,itemCode,batch);
	}

	/**
	 * 查询货位表主键最大值，用于同步添加容器box
	 *
	 * @return
	 */
	@Override
	public Integer selectMaxCellInfoId() {

		return cellInfoMapper.selectMaxCellInfoId();
	}

	/**
	 * 根据货架ID查询所有货位信息
	 *
	 * @param shelfId
	 * @return
	 */
	@Override
	public List<CellInfo> selectCellInfoByShelfId(Integer shelfId) {


		return cellInfoMapper.selectCellInfoByShelfId(shelfId);
	}

	/**
     * 查询货位设置信息
     * 
     * @param cellId 货位设置ID
     * @return 货位设置信息
     */
    @Override
	public CellInfo selectCellInfoById(Integer cellId)
	{
	    return cellInfoMapper.selectCellInfoById(cellId);
	}
	
	/**
     * 查询货位设置列表
     * 
     * @param cellInfo 货位设置信息
     * @return 货位设置集合
     */
	@Override
	public List<CellInfo> selectCellInfoList(CellInfo cellInfo)
	{
	    return cellInfoMapper.selectCellInfoList(cellInfo);
	}
	
    /**
     * 新增货位设置
     * 
     * @param cellInfo 货位设置信息
     * @return 结果
     */
	@Override
	public int insertCellInfo(CellInfo cellInfo)
	{
	    return cellInfoMapper.insertCellInfo(cellInfo);
	}
	
	/**
     * 修改货位设置
     * 
     * @param cellInfo 货位设置信息
     * @return 结果
     */
	@Override
	public int updateCellInfo(CellInfo cellInfo)
	{
	    return cellInfoMapper.updateCellInfo(cellInfo);
	}

	/**
     * 删除货位设置对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	@Override
	public int deleteCellInfoByIds(String ids)
	{
		return cellInfoMapper.deleteCellInfoByIds(Convert.toStrArray(ids));
	}

	/**
	 * 通过CellId获得Postion
	 * @param cellId
	 * @return
	 */
	@Override
	public String getPositionByCellId(Integer cellId) {
		CellInfo cellInfo = super.findBy("cellId",cellId);
		String position = ""+cellInfo.getShelfId()+cellInfo.getSColumn()+cellInfo.getSColumn();
		return position;
	}

	public void deleteByShelfId(Integer shelfId){
		cellInfoMapper.deleteByShelfId(shelfId);
	}

	public Double cellOccupyRatio(){
		return cellInfoMapper.cellOccupyRatio();
	}
	public int notItemCell(){
		return cellInfoMapper.notItemCell();
	}
	public int count(){return cellInfoMapper.count();}
	public int available(){return cellInfoMapper.available();}

	public CellInfoDto getBestCell(){
		return cellInfoMapper.getBestCell();
	}

	/**
	 * 根据批次与料号查询信息
	 * @param criteria
	 * @return
	 */
	@Override
	public List<CellInfoDto> findList(CellInfoCriteria criteria){
		return 	cellInfoMapper.findList(criteria);
	}

	@Override
	public void updateCellInfoState(CellInfo cellInfo,@Param("state") Integer state){
		cellInfo.setState(state);
		update(cellInfo);
	}

	@Override
	public List<CellInfo> selectCellInfoListByAreaId(@Param("areaId") Integer areaId){
		return cellInfoMapper.selectCellInfoListByAreaId(areaId);
	}
	@Override
	public String judgeBoxItemState(List<BoxItemDto> boxItemDtos){
		for(BoxItemDto boxItemDto : boxItemDtos){
			if(!boxItemDto.getCellState().equals(1)){
				return "选中箱不在货位";
			}
			if(!boxItemDto.getBoxState().equals(1)){
				return "选中箱任务中";
			}
			if(boxItemDto.getWorkOrderStockState().equals(1)){
				return "选中箱工单锁定中";
			}
		}
		return "success";
	}

	/** 操作台下发出空框或半框到入库口任务*/
	@Override
	public String findOutBox(Integer math,String itemCode, String batch,String loginPersonCard,Integer quantity,Integer boxType){
		String message = "";
		CellInfoCriteria cellInfoCriteria = new CellInfoCriteria();
		cellInfoCriteria.setTypeAndState(math);
		//出半框
		if(math.equals(1001)){
			cellInfoCriteria.setItemCode(itemCode);
			cellInfoCriteria.setBatch(batch);
		}else{
			cellInfoCriteria.setBoxType(boxType);
		}
		List<CellInfoDto> cellInfoDtos = cellInfoMapper.findList(cellInfoCriteria);
		if(cellInfoDtos.size()>0) {
			Bloop:
			for(CellInfoDto cellInfoDto : cellInfoDtos) {
				message = "当前扫码数量与此筐现有量超过此物料单筐最大存储数量！";
				if (math.equals(1001) && cellInfoDto.getQuantity() + quantity > cellInfoDto.getMaxPackQty()){
					continue;
				}else {
					if (math.equals(1001)) {
						BoxItem boxItem = boxItemService.getBoxItemByBoxCode(cellInfoDto.getBoxCode());
						boxItem.setSubInventoryId(TaskTypeConstant.DESIRED);
						boxItemService.update(boxItem);
					}
					updateCellInfoState(cellInfoDto, 2);
					BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(cellInfoDto.getBoxCode());
					boxInfo.setBoxState(3);
					boxInfoService.update(boxInfo);
					TaskInfo taskInfo = new TaskInfo(null, new GuidUtils().toString(),
							MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto.getShelfName(), cellInfoDto.getSColumn(), cellInfoDto.getSRow()),
							"105", TaskTypeConstant.CELL_TO_OPERATOR_FLOOR, 0, cellInfoDto.getQuantity(), cellInfoDto.getBoxCode());
					taskInfo.setIsTop("0");
					taskInfo.setCardNo(loginPersonCard);
					taskInfoService.save(taskInfo);
					message = "success";
					break;
				}
			}
		}else {
			if(math.equals(1001)){
				message = "当前无可出半筐";
			}else{
				message = "当前无可出"+(boxType==1?"A":"B")+"筐";
			}

		}
		return message;
	}

	//入空框
	@Override
	public String inNullBox(@Param("boxCode") String boxCode,@Param("loginPersonCard") String loginPersonCard,@Param("boxType") Integer boxType){
		CellInfoDto cellInfoDto = getBestCell();
		String error = "";
		if (cellInfoDto != null) {
			updateCellInfoState(cellInfoDto, 2);

			BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxCode);
			if (boxInfo != null) {
				boxInfo.setBoxCellId(cellInfoDto.getCellId());
				boxInfo.setBoxState(3);
				boxInfo.setHasGoods(0);
				boxInfoService.update(boxInfo);
				BoxItem boxItem = boxItemService.getBoxItemByBoxCode(boxCode);
				boxItem.setQuantity(0);
//				boxItem.setSubInventoryId(0);
				boxItem.setForecastStockQuantity(0);
                boxItem.setWorkOrderStockState(0);
                boxItem.setWorkerOrderNo(null);
                boxItem.setInTime(null);
                boxItem.setLockQuantity(0);
//                boxItem.setPd(null);
//				boxItem.setExp(null);
				boxItemService.updateBoxItem(boxItem);
			} else {
				boxInfo = new BoxInfo(
						null, boxCode, boxType, cellInfoDto.getCellId(), 3, 0);
				boxInfoService.save(boxInfo);
				BoxItem boxItem = new BoxItem(boxCode,null,null,0,null,0);
				boxItem.setForecastStockQuantity(0);
				boxItem.setWorkOrderStockState(0);
				boxItem.setLockQuantity(0);
				boxItemService.save(boxItem);
			}
			TaskInfo taskInfo = new TaskInfo(null, new GuidUtils().toString(), "105",
					MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto.getShelfName(), cellInfoDto.getSColumn(), cellInfoDto.getSRow()),
					TaskTypeConstant.IN_NULL_BOX, 0, 0, boxCode);
			taskInfo.setIsTop("0");
			taskInfo.setCardNo(loginPersonCard);
			taskInfoService.save(taskInfo);
			error = "success";
		} else {
			error = "货位已满!";
		}
		return error;
	}
	//手动工单出库后入半框
	@Override
	public String  inAvailableBoxAfterManualOut(String boxCode,Integer outQuantity,String loginPersonCard,String billNo) throws Exception{
		String error = "";
		//类型为手动出库
		BoxItem boxItem = boxItemService.getBoxItemByBoxCode(boxCode);
		BillOutMasterDto billOutMasterDto = billOutMasterService.findList(new BillOutMasterCriteria(billNo)).get(0);
		BillOutDetail billOutDetail = billOutDetailService.findById(billOutMasterDto.getBillOutDetailId());
		Integer alreadyQuantity = billOutMasterDto.getAlreadyOutQuantity()+outQuantity;
		if(outQuantity>(boxItem.getQuantity()- boxItem.getLockQuantity())){
			return "出库数量大于当前箱可出数量，请重新输入！";
		}
		if(billOutMasterDto.getQuantity() < alreadyQuantity){
			return "当前出库数量大于工单数量，请重新输入！";
		}
		else if(billOutMasterDto.getQuantity() == alreadyQuantity){
			//单据出库完成
			billOutMasterDto.setState(2);
			//当工单出库完成后取消boxItem数量锁定，boxInfo锁定，cellInfo锁定，取消自动pickTask及taskInfo任务
			List<PickTaskDto> pickTasks = pickTaskService.findList(new PickTaskCriteria(null,billOutDetail.getBillOutDetailId(),TaskTypeConstant.WORKER_ORDER_OUT));
			for(PickTaskDto pickTaskDto:pickTasks){
				//一个工单不会生成两条锁定同一箱的出库任务，查询此箱并释放锁定库存
				BoxItem boxItemOne = boxItemService.getBoxItemByBoxCode(pickTaskDto.getBoxCode());
				Integer surplus = boxItemOne.getForecastStockQuantity() - pickTaskDto.getPickQuantity();
				if(surplus>0){
					boxItemOne.setForecastStockQuantity(surplus);
				}else {
					boxItemOne.setForecastStockQuantity(0);
					boxItemOne.setWorkOrderStockState(0);
				}
				boxItemService.update(boxItemOne);
				//锁定任务取消
				pickTaskDto.setPickState(5);
				pickTaskService.update(pickTaskDto);
			}
		}
		else if(billOutMasterDto.getQuantity() > alreadyQuantity){
			//工单手动出库部分
			billOutMasterDto.setState(1);
		}
		billOutDetail.setAlreadyOutQuantity(alreadyQuantity);
		billOutDetailService.update(billOutDetail);
		billOutMasterService.update(billOutMasterDto);
		PickTask pickTask = new PickTask(boxCode,outQuantity,billOutDetail.getBillOutDetailId(),4,boxItem.getBatch(),
				boxItem.getSubInventoryId(),DateUtils.getTime(),DateUtils.getTime());
		pickTask.setPickType(TaskTypeConstant.WORKER_ORDER_MANUAL_OUT);
		pickTaskService.save(pickTask);
		ItemInfo itemInfo = itemInfoService.findByItemCode(boxItem.getItemCode());
		if((boxItem.getQuantity()-outQuantity) > 0){
			boxItem.setQuantity(boxItem.getQuantity()-outQuantity);
			boxItemService.update(boxItem);
			inAvailableBox(boxItem,loginPersonCard,billOutDetail.getBillOutDetailId(),itemInfo.getItemName());
		}else{
			inNullBox(boxItem.getBoxCode(),loginPersonCard,null);
		}
		RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS工单发料写入EBS接口");
//                        WorkerOrderIssueTime workerOrderIssueTime = workerOrderIssueTimeService.findById(1);
		WorkerOrderIssueTime workerOrderIssueTime = TaskTypeConstant.workerOrderIssueTime;
		List<RequestId> requestIds = new ArrayList<>();
		List<Map<String, String>> lists = new ArrayList<>();
		SubInventory subInventory = subInventoryService.findById(boxItem.getSubInventoryId());
		lists.add(MyUtils.wipOut(TaskTypeConstant.organizationId.toString(),TaskTypeConstant.MES_BILL_OUT,
				billOutMasterDto.getBillNo(),itemInfo.getInventoryItemId().toString(),(outQuantity*(-1))+"",
				workerOrderIssueTime.getOperationSeqnum()==null ?null:workerOrderIssueTime.getOperationSeqnum(),
				boxItem.getBatch(),subInventory.getSubInventoryCode(),subInventory.getSlotting() == null ? "":subInventory.getSlotting(),
				MyUtils.getNinetySecondsAgo(),itemInfo.getUnit()));
		RequestId requestId = new RequestId(requestIdAuto.getRequestId(),itemInfo.getInventoryItemId(),outQuantity*(-1),
				boxItem.getBatch(),subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null:Integer.parseInt(subInventory.getSlotting()),
				TaskTypeConstant.organizationId,TaskTypeConstant.MES_BILL_OUT,billOutMasterDto.getBillNo(),workerOrderIssueTime.getOperationSeqnum(),
				DateUtils.getTime(),itemInfo.getUnit(), TaskTypeConstant.OUT,TaskTypeConstant.FAIL_WAIT_MANAGE,"调用WMS工单发料写入EBS接口失败","ERROR");
//                           requestIdService.save(requestId);
		requestIds.add(requestId);
		//工单出库扣减库存
		requestIdService.inventoryMinus(requestIds,requestIdAuto,lists);
		return "success";
	}

	//出半框到操作台
	@Override
	public void updateCellStateAndBoxStateAndSendTaskInfo(BoxItemDto boxItemDto, Integer billOutDetailId, String loginPersonCard){
		BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxItemDto.getBoxCode());
		boxInfo.setBoxState(3);
		boxInfoService.update(boxInfo);
		CellInfo cellInfo = findById(boxItemDto.getBoxCellId());
		updateCellInfoState(cellInfo,2);
		TaskInfo taskInfo = new TaskInfo(new GuidUtils().toString(),
				MyUtils.connectShelfNameAndRowAndColumn(boxItemDto.getShelfName(),boxItemDto.getsColumn(),boxItemDto.getsRow()),
				"105", TaskTypeConstant.CELL_TO_OPERATOR_FLOOR,0,boxItemDto.getQuantity(),boxItemDto.getBoxCode(), "0",billOutDetailId
		);
		if(billOutDetailId != null){
			taskInfo.setBillOutDetailId(billOutDetailId);
		}
		taskInfo.setIsTop("0");
		taskInfo.setCardNo(loginPersonCard);
		taskInfoService.save(taskInfo);
	}

	//从操作台入半框
	@Override
	public void inAvailableBox(BoxItem boxItem,String loginPersonCard,Integer billOutDetailId,String itemName) throws Exception{
		CellInfoDto cellInfoDto = getBestCell();
		updateCellInfoState(cellInfoDto, 2);
		BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxItem.getBoxCode());
		boxInfo.setBoxState(3);
		boxInfo.setBoxCellId(cellInfoDto.getCellId());
		boxInfoService.update(boxInfo);
		TaskInfo taskInfo = new TaskInfo(
				null, new GuidUtils().toString(), "105",
				MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto.getShelfName(), cellInfoDto.getSColumn(), cellInfoDto.getSRow()),
				TaskTypeConstant.IN_AVAILABLE_BOX, 0, boxItem.getQuantity(), boxItem.getBoxCode());
		taskInfo.setCompleteQuantity(boxItem.getQuantity());
		taskInfo.setBillOutDetailId(billOutDetailId);
		taskInfo.setIsTop("0");
		taskInfo.setBarCode(MyUtils.connectPrintString(boxItem.getItemCode(),boxItem.getQuantity(),boxItem.getExp(),boxItem.getBatch(),itemName));
		taskInfo.setCardNo(loginPersonCard);
		taskInfo.setTaskEndTime(DateUtils.getTime());
		taskInfoService.save(taskInfo);
	}

	@Override
	public CellInfoDto findByCellId(@Param("cellId") Integer cellId){
		return cellInfoMapper.findByCellId(cellId);
	}

	@Override
	public List<Cell> findStateEqualsOne(){
		return cellInfoMapper.findStateEqualsOne();
	}

	@Override
	public List<CellInfoDto> findListTwo(){
		return cellInfoMapper.findListTwo();
	}
}

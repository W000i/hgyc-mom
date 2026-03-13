package com.deer.wms.base.system.service.bill.impl;

import com.deer.wms.base.system.model.CallAgv;
import com.deer.wms.base.system.model.Carrier;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.bill.*;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.Door;
import com.deer.wms.base.system.service.CallAgvService;
import com.deer.wms.base.system.service.CarrierService;
import com.deer.wms.base.system.service.MESWebService.BillOutWorkerOrder;
import com.deer.wms.base.system.service.MESWebService.WebserviceResponse;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.base.system.service.ware.IDoorService;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.base.system.web.task.TaskInfoController;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.base.system.dao.bill.BillOutDetailMapper;
import com.deer.wms.base.system.service.bill.IBillOutDetailService;
import com.deer.wms.common.utils.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.Null;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


/**
 * 入库单 服务层实现
 * 
 * @author cai
 * @date 2019-07-17
 */
@Service
public class BillOutDetailServiceImpl extends AbstractService<BillOutDetail, Integer> implements IBillOutDetailService {

	@Autowired
	private BillOutDetailMapper billOutDetailMapper;

	@Autowired
	private IBoxItemService boxItemService;
	@Autowired
	private IBillOutMasterService billOutMasterService;
	@Autowired
	private IDoorService doorService;
	@Autowired
	private CarrierService carrierService;
	@Autowired
	private ICellInfoService cellInfoService;
	@Autowired
	private PickTaskService pickTaskService;
	@Autowired
	private CallAgvService callAgvService;

	/**
	 * 根据BillOutDetailId删除BillOutDetail
	 *
	 */
	@Override
	public void deleteBillOutDetailByBillOutDetailId(Integer billOutDetailId) {

		billOutDetailMapper.deleteBillOutDetailByBillOutDetailId(billOutDetailId);
	}

	/**
	 *  根据billId查询BillOutDetail信息
	 *
	 * @param billId
	 * @return
	 */
	@Override
	public List<BillOutDetailDto> findListByBillId(Integer billId) {

		return billOutDetailMapper.findListByBillId(billId);
	}

	/**
	 * 保存BillOutDetail  出库单详情
	 *
	 * @param billOutDetail
	 */
	@Override
	public void saveBillOutDetail(BillOutDetail billOutDetail) {

		billOutDetailMapper.saveBillOutDetail(billOutDetail);
	}
	@Override
	public List<BillOutDetail> findList(BillOutDetailCriteria criteria){
		return billOutDetailMapper.findList(criteria);
	}

	@Override
	public void save(BillOutDetail billOutDetail){
		super.save(billOutDetail);
	}
	/**
	 * MES下发计划工单
	 */
	@Override
	@Transactional
	public synchronized WebserviceResponse downWipToStock(@Param("input") String input){
		WebserviceResponse webServiceResponse = null;
		String taskCode = null;
		CallAgv callAgv = new CallAgv();
		try {
			if(input.length() > 0) {
				BillOutWorkerOrder billOutWorkerOrder = splitDownWipToStockXmlCode(input);
				taskCode = billOutWorkerOrder.getTaskCode();
				List<BillOutMasterDto> billOutMasterDtos = billOutMasterService.findList(new BillOutMasterCriteria(billOutWorkerOrder.getWipEntity(),103));
				if(billOutMasterDtos.size()<=0) {
					webServiceResponse = lockInventoryManage(billOutWorkerOrder);
				}
				else{
					webServiceResponse = new WebserviceResponse(taskCode, "-1", "此工单已下发给WMS", null);
				}
				callAgv.setItemCode(billOutWorkerOrder.getItemCode());
				callAgv.setWipEntity(billOutWorkerOrder.getWipEntity());
				callAgv.setQuantity(billOutWorkerOrder.getQuantity());
			}else{
				webServiceResponse = new WebserviceResponse("", "-1", "缺少信息", null);
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw, true));
			String error = sw.toString();
			webServiceResponse = new WebserviceResponse(taskCode,"-1","系统内部错误，请联系管理员",null);
		}
		finally{
			callAgv.setMethodName("DownWipToStock");
			callAgv.setCode(input);
			callAgv.setTaskCode(taskCode);
			callAgv.setErrorCode(webServiceResponse.getErrorCode());
			callAgv.setErrorMsg("MES向WMS下发工单接口。"+webServiceResponse.getErrorMsg());
			callAgv.setCreateTime(DateUtils.getTime());
			callAgvService.save(callAgv);
		}
		return webServiceResponse;
	}

	@Override
	public WebserviceResponse lockInventoryManage(BillOutWorkerOrder billOutWorkerOrder) throws Exception{
		WebserviceResponse webServiceResponse = null;
		Integer outQuantitys = billOutWorkerOrder.getQuantity();
		List<BoxItemDto> boxItemDtos = boxItemService.findSuitByItemCodeAndQuantity(billOutWorkerOrder.getItemCode(), billOutWorkerOrder.getQuantity(),billOutWorkerOrder.getWipEntity());
		if (boxItemDtos != null) {
			String nowStr = DateUtils.getTime();
			List<BillOutMasterDto> billOutMasterDtos = billOutMasterService.findList(new BillOutMasterCriteria(
					TaskTypeConstant.WORKER_ORDER_OUT,0));
			BillOutMaster billOutMaster = new BillOutMaster();
			billOutMaster.setBillId(null);
			billOutMaster.setBillNo(billOutWorkerOrder.getWipEntity());
			billOutMaster.setCreateTime(nowStr);
			billOutMaster.setState(0);
			billOutMaster.setMemo("MES工单");
			billOutMaster.setWareId(TaskTypeConstant.wareInfo.getWareId());
			billOutMaster.setType(TaskTypeConstant.WORKER_ORDER_OUT);
			billOutMasterService.save(billOutMaster);
			BillOutDetail billOutDetail = new BillOutDetail();
			billOutDetail.setBillOutDetailId(null);
			billOutDetail.setBillId(billOutMaster.getBillId());
			billOutDetail.setItemCode(billOutWorkerOrder.getItemCode());
			billOutDetail.setQuantity(billOutWorkerOrder.getQuantity());
			billOutDetail.setTaskId(billOutWorkerOrder.getTaskCode());
			billOutDetail.setFinishedCode(billOutWorkerOrder.getFinishedCode());
			billOutDetail.setPriority(billOutWorkerOrder.getPriority());
			billOutDetail.setItemCode(billOutWorkerOrder.getItemCode());
			billOutDetail.setAlreadyOutQuantity(0);

			//根据状态为0查询是否有出库工单
			if(billOutMasterDtos.size()>0){
				//如果有根据状态物料编号查询一次
				List<BillOutMasterDto> billOutMasterDtoOnes = billOutMasterService.findList(new BillOutMasterCriteria(
						TaskTypeConstant.WORKER_ORDER_OUT,0,billOutWorkerOrder.getItemCode()));
				//如果根据物料Id查到工单排序根据查到最后一个工单的优先级的+1
				if(billOutMasterDtoOnes.size()>0){
					BillOutMasterDto billOutMasterDtoOne = billOutMasterDtoOnes.get(billOutMasterDtoOnes.size()-1);
					billOutDetail.setSequence(
							(billOutMasterDtoOne.getSequence()+1)
					);
					//根据当前获取的Id查询大于此Id未发料工单，并循环优先级+1
					List<BillOutDetail> billOutDetails = billOutDetailMapper.findList(new BillOutDetailCriteria(
							0,TaskTypeConstant.WORKER_ORDER_OUT,102,billOutMasterDtoOne.getSequence()));
					if(billOutDetails.size()>0){
						for(BillOutDetail billOutDetail1 : billOutDetails){
							billOutDetail1.setSequence(billOutDetail1.getSequence()+1);
							update(billOutDetail1);
						}
					}
				}
				//如果没根据物料编码查到此物料其他工单，则按照查询到其他工单的排序累加+1
				else{
					billOutDetail.setSequence(
							(billOutMasterDtos.get(billOutMasterDtos.size()-1).getSequence()+1)
					);
				}
			}
			else{
				//如果没有优先级为1
				billOutDetail.setSequence(1);
			}
			save(billOutDetail);
			for (BoxItemDto boxItemDto : boxItemDtos) {
				boxItemDto.setWorkOrderStockState(1);
				Integer canOutInventoryQuantity = boxItemDto.getQuantity() - boxItemDto.getForecastStockQuantity() - (boxItemDto.getLockQuantity()==null?0:boxItemDto.getLockQuantity());
				PickTask pickTask = new PickTask(boxItemDto.getBoxCode(), 0, billOutDetail.getBillOutDetailId(), 1, boxItemDto.getBatch(), boxItemDto.getSubInventoryId(),DateUtils.getTime());
				if (outQuantitys <= canOutInventoryQuantity) {
					boxItemDto.setForecastStockQuantity(outQuantitys + boxItemDto.getForecastStockQuantity());
					pickTask.setPickQuantity(outQuantitys);
				} else {
					boxItemDto.setForecastStockQuantity(boxItemDto.getQuantity());
					pickTask.setPickQuantity(canOutInventoryQuantity);
					outQuantitys -= canOutInventoryQuantity;
				}
				pickTask.setLockPickQuantity(pickTask.getPickQuantity());
				pickTask.setPickType(TaskTypeConstant.WORKER_ORDER_OUT);
				pickTaskService.save(pickTask);
				boxItemService.update(boxItemDto);
			}
			webServiceResponse = new WebserviceResponse(billOutWorkerOrder.getTaskCode(), "0", "OK", null);
		}
		else {
			webServiceResponse = new WebserviceResponse(billOutWorkerOrder.getTaskCode(), "-1", "库存不足", null);
		}
		return webServiceResponse;
	}

	/**
	 * 空载具到达出库输送线的入库口
	 */
	@Override
	public WebserviceResponse emptyShelfArrive(String input) {
		WebserviceResponse webServiceResponse = null;
		CallAgv callAgv = new CallAgv();
		try {
			if(input.length() > 0) {
				BillOutWorkerOrder billOutWorkerOrder = splitEmptyShelfArriveXmlCode(input);
				String taskCode = billOutWorkerOrder.getTaskCode();
				if (taskCode != null && taskCode != "" && billOutWorkerOrder.getShelfCode() != null && billOutWorkerOrder.getShelfCode() != "") {
					Carrier carrier = new Carrier(billOutWorkerOrder.getShelfCode().replace("ID",""), 1, billOutWorkerOrder.getTaskCode(), DateUtils.getTime());
					Carrier carrierOne = carrierService.inValidate(carrier.getCarrierCode());
					if(carrierOne != null){
						WebSocketServer.sendInfo("当前载具已在排队中，请勿重复回传，请查看与MES交互记录！", TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
					}else {
						TaskTypeConstant.GET_EMPTY_BOX_STATE = 4;
						carrierService.save(carrier);
					}
					webServiceResponse = new WebserviceResponse(taskCode, "0", "OK", null);
				} else {
					webServiceResponse = new WebserviceResponse(taskCode, "-1", "缺少信息", null);
				}
			}else{
				webServiceResponse = new WebserviceResponse("", "-1", "缺少信息", null);
			}
		}catch(Exception e){
			e.printStackTrace();
			webServiceResponse = new WebserviceResponse("","-1","服务器内部错误，请联系管理员",null);
		}finally{
			callAgv.setMethodName("EmptyShelfArrive");
			callAgv.setCode(input);
			callAgv.setTaskCode(webServiceResponse.getTaskCode());
			callAgv.setErrorCode(webServiceResponse.getErrorCode());
			callAgv.setErrorMsg("MES空载具到达出库输送线的入库口呼叫WMS接口。"+webServiceResponse.getErrorMsg());
			callAgv.setCreateTime(DateUtils.getTime());
			callAgvService.save(callAgv);
		}
		return webServiceResponse;
	}

	/**
	 * 解析MES下发工单字符串
	 * @param xmlCode
	 * @return
	 * @throws Exception
	 */
	private BillOutWorkerOrder splitDownWipToStockXmlCode(String xmlCode)throws Exception{
		BillOutWorkerOrder billOutWorkerOrder = new BillOutWorkerOrder();
		try {
			Document doc = DocumentHelper.parseText(xmlCode);
			Element root = doc.getRootElement();
			List<Element> elements = root.elements();
			billOutWorkerOrder.setMacCode(root.attributeValue("macCode"));
			billOutWorkerOrder.setTaskCode(root.attributeValue("taskCode"));
			billOutWorkerOrder.setWipEntity(root.attributeValue("wipEntity"));
			Door door = doorService.selectDoorById(5);
			String code = door.getCode();
			for(Element element : elements){
				String tagCode = element.attributeValue("tagCode");
				String tagValue = element.attributeValue("tagValue");
				if(tagCode.equals(code+"_1000")){
					billOutWorkerOrder.setFinishedCode(tagValue);
				}else if(tagCode.equals(code+"_1001")){
					billOutWorkerOrder.setQuantity(Integer.parseInt(tagValue));
				}else if(tagCode.equals(code+"_1002")){
					billOutWorkerOrder.setPriority(tagValue);
				}else if(tagCode.equals(code+"_1003")){
					billOutWorkerOrder.setItemCode(tagValue);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return billOutWorkerOrder;
	}

	/**
	 * 解析Mes空载具到达立体库入料口xml字符串
	 * @param xmlCode
	 * @return
	 * @throws Exception
	 */
	private BillOutWorkerOrder splitEmptyShelfArriveXmlCode(String xmlCode)throws Exception{
		BillOutWorkerOrder billOutWorkerOrder = new BillOutWorkerOrder();
		try {
			Document doc = DocumentHelper.parseText(xmlCode);
			Element root = doc.getRootElement();
			billOutWorkerOrder.setMacCode(root.attributeValue("macCode"));
			billOutWorkerOrder.setTaskCode(root.attributeValue("taskCode"));
			billOutWorkerOrder.setShelfCode(root.attributeValue("shelfCode"));
		}catch(Exception e){
			e.printStackTrace();
		}
		return billOutWorkerOrder;
	}
	public static void main(String args[]){

	}
}



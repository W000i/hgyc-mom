package com.deer.wms.base.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.deer.wms.base.system.dao.RequestIdMapper;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.service.BillInReceiveService;
import com.deer.wms.base.system.service.BillInRecordService;
import com.deer.wms.base.system.service.RequestIdService;


import com.deer.wms.base.system.service.ServerVisitAddressService;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.framework.util.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by  on 2019/12/26.
 */
@Service
@Transactional
public class RequestIdServiceImpl extends AbstractService<RequestId, Integer> implements RequestIdService {

    @Autowired
    private RequestIdMapper requestIdMapper;

    @Autowired
    private ServerVisitAddressService serverVisitAddressService;

    @Autowired
    private RequestIdService requestIdService;

    @Autowired
    private BillInRecordService billInRecordService;

    @Autowired
    private BillInReceiveService billInReceiveService;

    @Override
    public List<RequestIdDto> selectList(RequestIdCriteria requestIdCriteria){
        return requestIdMapper.selectList(requestIdCriteria);
    }

    @Override
    public  List<RequestIdDto> findProcessing(RequestIdCriteria criteria){
        return requestIdMapper.findProcessing(criteria);
    }

    /**
     * 子库转移请求EBS
     */
    @Override
    public void subInventoryTransfer(RequestIdAuto requestIdAuto,List<RequestId> requestIds, List<Map<String,String>> lists)  throws Exception{
        EbsBack entityStr = serverVisitAddressService.requestServerCode(requestIdAuto.getRequestId(),
                TaskTypeConstant.WMSSUBINV_TRANSPROC, TaskTypeConstant.SYNCHRONOUS_EXECUTE, null, lists);
        //判断是否请求到参数
        if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
            JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
            for (int i = 0; i < jsonArrays.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                // 处理状态
                String processStatus = MyUtils.backString(jsonObject.get("processStatus"));
                String lastUpdateDate = MyUtils.backString(jsonObject.get("lastUpdateDate"));
                String creationDate = MyUtils.backString(jsonObject.get("creationDate"));
                String errorMsg = MyUtils.backString(jsonObject.get("errorMsg"));
                String lastUpdatedBy = MyUtils.backString(jsonObject.get("lastUpdatedBy"));
                String createdBy = MyUtils.backString(jsonObject.get("createdBy"));
                Integer itemId = MyUtils.backDouble(jsonObject.get("itemId"));
                Integer ebsbackQuantity = MyUtils.backDouble(jsonObject.get("quantity"));
                String subInventorys = MyUtils.backString(jsonObject.get("subInventory"));
                Integer locatorId = MyUtils.backInteger(jsonObject.get("locatorId"));
                Integer organizationId = MyUtils.backInteger(jsonObject.get("organizationId"));
                String transDate = MyUtils.backString(jsonObject.get("transDate"));
                String transUom = MyUtils.backString(jsonObject.get("transUom"));
                Integer transTypeId = MyUtils.backInteger(jsonObject.get("transTypeId"));
                String lotNumber = MyUtils.backString(jsonObject.get("transLotNumber"));
                Integer sourceHeaderId = MyUtils.backInteger(jsonObject.get("sourceHeaderId"));
                Integer sourceLineId = MyUtils.backInteger(jsonObject.get("sourceLineId"));
                String transSubInventory = MyUtils.backString(jsonObject.get("transSubInventory"));
                Integer transLocatorId = MyUtils.backInteger(jsonObject.get("transLocatorId"));
                Integer id = MyUtils.backInteger(jsonObject.get("id"));
                RequestId requestId = new RequestId(requestIdAuto.getRequestId(),processStatus,lastUpdateDate,lastUpdatedBy,
                        creationDate,createdBy,itemId,ebsbackQuantity,lotNumber,id,transDate,errorMsg,subInventorys,locatorId,
                        organizationId, TaskTypeConstant.TRANSFER,transTypeId+"",transUom,transSubInventory,transLocatorId,
                        sourceHeaderId,sourceLineId) ;
                if(processStatus.equals("SUCCESS")){
                    requestId.setState(TaskTypeConstant.SUCCESS);
                }
                else if(processStatus.equals("PROCESSING")){
                    requestId.setState(TaskTypeConstant.MANAGING);
                }
                else{
                    requestId.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                }
                //保存请求EBS后的数据
                save(requestId);
            }
        }
        //失败则循环保存上面定义的请求记录
        else {
            for (RequestId requestId : requestIds) {
                requestIdService.save(requestId);
            }
        }
    }

    /**
     * 工单出库请求EBS
     */
    @Override
    public void inventoryMinus(List<RequestId> requestIds, RequestIdAuto requestIdAuto, List<Map<String,String>> lists) throws Exception{
        EbsBack entityStr = serverVisitAddressService.requestServerCode(requestIdAuto.getRequestId(),TaskTypeConstant.WMS_WIP_PROC,
                TaskTypeConstant.SYNCHRONOUS_EXECUTE,null, lists);
        if(entityStr != null && entityStr.getSuccess().equals("true") &&entityStr.getTotal() > 0){
            JSONArray wipOutArrays = JSONArray.parseArray(entityStr.getRows());
            for (int k = 0; k < wipOutArrays.size(); k++) {
                com.alibaba.fastjson.JSONObject wipOutjsonObject = wipOutArrays.getJSONObject(k);
                // 处理状态
                String processStatus = MyUtils.backString(wipOutjsonObject.get("processStatus"));
                String lastUpdateDate = MyUtils.backString(wipOutjsonObject.get("lastUpdateDate"));
                String errorMsg = MyUtils.backString(wipOutjsonObject.get("errorMsg"));
                String creationDate = MyUtils.backString(wipOutjsonObject.get("creationDate"));
                String lastUpdatedBy = MyUtils.backString(wipOutjsonObject.get("lastUpdatedBy"));
                String createdBy = MyUtils.backString(wipOutjsonObject.get("createdBy"));
                Integer itemId = MyUtils.backDouble(wipOutjsonObject.get("itemId"));
                Integer quantitys = MyUtils.backDouble(wipOutjsonObject.get("quantity"));
                String subInventorys = MyUtils.backString(wipOutjsonObject.get("subInventory"));
                Integer locatorId = MyUtils.backInteger(wipOutjsonObject.get("locatorId"));
                String lotNumber = MyUtils.backString(wipOutjsonObject.get("lotNumber"));
                Integer organizationId = MyUtils.backInteger(wipOutjsonObject.get("organizationId"));
                String transDate = MyUtils.backString(wipOutjsonObject.get("transDate"));
                String transUom = MyUtils.backString(wipOutjsonObject.get("transUom"));
                Integer transTypeId = MyUtils.backInteger(wipOutjsonObject.get("transTypeId"));
                Integer wipEntityId = MyUtils.backInteger(wipOutjsonObject.get("wipEntityId"));
                String operationSeqNum = MyUtils.backString(wipOutjsonObject.get("operationSeqNum"));
                Integer id = MyUtils.backInteger(wipOutjsonObject.get("id"));
                RequestId requestId = new RequestId(requestIdAuto.getRequestId(),processStatus,lastUpdateDate,
                        lastUpdatedBy,creationDate,createdBy,itemId,quantitys,lotNumber,id,transDate,
                        errorMsg,subInventorys,locatorId,organizationId,TaskTypeConstant.OUT,transTypeId+"",
                        wipEntityId+"",operationSeqNum,transUom);
                if(processStatus.equals("SUCCESS")){
                    requestId.setState(TaskTypeConstant.SUCCESS);
                }
                else if(processStatus.equals("PROCESSING")){
                    requestId.setState(TaskTypeConstant.MANAGING);
                }
                else{
                    requestId.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                }
                requestIdService.save(requestId);
            }
        }else{
            for(RequestId requestId : requestIds){
                requestIdService.save(requestId);
            }
        }
    }

    /**
     * 交货请求EBS
     */
    @Override
    public void delivery(List<RequestId> requestIds, List<Map<String,String>> lists, RequestIdAuto requestIdAuto) throws Exception{
        //WMS回传EBS交货数据
        EbsBack entityStr1 = serverVisitAddressService.requestServerCode(requestIdAuto.getRequestId(),
                TaskTypeConstant.WMS_DEV_PROC, TaskTypeConstant.SYNCHRONOUS_EXECUTE,null, lists);
        if (entityStr1 != null && entityStr1.getSuccess().equals("true") && entityStr1.getTotal() > 0) {
            JSONArray deliveryBackJsonArrays = JSONArray.parseArray(entityStr1.getRows());

            for (int i = 0; i < deliveryBackJsonArrays.size(); i++) {
                com.alibaba.fastjson.JSONObject deliveryBackjsonObject = deliveryBackJsonArrays.getJSONObject(i);
                // 处理状态
                String processStatus = MyUtils.backString(deliveryBackjsonObject.get("processStatus"));
                String lastUpdateDate = MyUtils.backString(deliveryBackjsonObject.get("lastUpdateDate"));
                String lastUpdatedBy = MyUtils.backString(deliveryBackjsonObject.get("lastUpdatedBy"));
                String errorMsg = MyUtils.backString(deliveryBackjsonObject.get("errorMsg"));
                String creationDate = MyUtils.backString(deliveryBackjsonObject.get("creationDate"));
                String createdBy = MyUtils.backString(deliveryBackjsonObject.get("createdBy"));
                Integer quantity = MyUtils.backDouble(deliveryBackjsonObject.get("quantity"));
                String subInventory = MyUtils.backString(deliveryBackjsonObject.get("subInventory"));
                Integer locatorId = MyUtils.backInteger(deliveryBackjsonObject.get("locatorId"));
                Integer organizationId = MyUtils.backInteger(deliveryBackjsonObject.get("organizationId"));
                String lotNumber = MyUtils.backString(deliveryBackjsonObject.get("lotNumber"));
                Integer transId = MyUtils.backInteger(deliveryBackjsonObject.get("transId"));
                String shipmentNum = MyUtils.backString(deliveryBackjsonObject.get("shipmentNum"));
                String transDate = MyUtils.backString(deliveryBackjsonObject.get("transDate"));
                Integer id = MyUtils.backInteger(deliveryBackjsonObject.get("id"));
                RequestId requestId = new RequestId(requestIdAuto.getRequestId(),processStatus,lastUpdateDate,lastUpdatedBy,
                        creationDate,createdBy,quantity,lotNumber,id,transDate,shipmentNum,errorMsg,subInventory,
                        locatorId,organizationId,transId,TaskTypeConstant.DELIVERY);
                if(processStatus.equals("SUCCESS")){
                    requestId.setState(TaskTypeConstant.SUCCESS);
                }
                else if(processStatus.equals("PROCESSING")){
                    requestId.setState(TaskTypeConstant.MANAGING);
                }
                else{
                    requestId.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                }
                requestIdService.save(requestId);
            }
        }else{
            for(RequestId requestId : requestIds){
                requestIdService.save(requestId);
            }
        }
    }

    /**
     * 采购接收请求EBS
     */
    @Override
    public String receive(List<BillInReceive> billInReceives,List<BillInRecordDto> billInRecordDtos,List<RequestId> requestIds, List<Map<String,String>> lists, RequestIdAuto requestIdAuto) throws Exception{
        //对接EBS采购接收
        EbsBack entityStr = serverVisitAddressService.requestServerCode(requestIdAuto.getRequestId(),
                TaskTypeConstant.WMS_RCV_PROC, TaskTypeConstant.SYNCHRONOUS_EXECUTE, null, lists);
        //判断是否请求到参数
        if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {

            JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
            String msg = "";
            for (int i = 0; i < jsonArrays.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                // 处理状态
                String processStatus = MyUtils.backString(jsonObject.get("processStatus"));
                String lastUpdateDate = MyUtils.backString(jsonObject.get("lastUpdateDate"));
                String lastUpdatedBy = MyUtils.backString(jsonObject.get("lastUpdatedBy"));
                String creationDate = MyUtils.backString(jsonObject.get("creationDate"));
                String createdBy = MyUtils.backString(jsonObject.get("createdBy"));
                Integer itemId = MyUtils.backInteger(jsonObject.get("itemId"));
                Integer quantity = MyUtils.backDouble(jsonObject.get("quantity"));
                String lotNumber = MyUtils.backString(jsonObject.get("lotNumber"));
                Integer poHeaderId = MyUtils.backInteger(jsonObject.get("poHeaderId"));
                Integer poLineId = MyUtils.backInteger(jsonObject.get("poLineId"));
                Integer lineLocationId = MyUtils.backInteger(jsonObject.get("lineLocationId"));
                Integer poDistributionId = MyUtils.backInteger(jsonObject.get("poDistributionId"));
                String receiptDate = MyUtils.backString(jsonObject.get("receiptDate"));
                String receiptNum = MyUtils.backString(jsonObject.get("receiptNum"));
                String dueDate = MyUtils.backString(jsonObject.get("dueDate"));
                Integer id = MyUtils.backInteger(jsonObject.get("id"));
                String errorMsg = MyUtils.backString(jsonObject.get("errorMsg"));
                String expirationDate = MyUtils.backString(jsonObject.get("expirationDate"));
                String originationDate = MyUtils.backString(jsonObject.get("OriginationDate"));
                RequestId requestId = new RequestId(requestIdAuto.getRequestId(),processStatus,lastUpdateDate,lastUpdatedBy,
                        creationDate,createdBy,itemId,quantity,lotNumber,poHeaderId,poLineId,lineLocationId,poDistributionId,
                        receiptDate,id,TaskTypeConstant.organizationId,TaskTypeConstant.RECEIVE,receiptNum,dueDate,expirationDate,originationDate) ;
                if (processStatus.equals("SUCCESS")) {
                    requestId.setState(TaskTypeConstant.SUCCESS);
                    if(billInRecordDtos != null) {
                        for (BillInRecordDto billInRecordDto : billInRecordDtos) {
                            if (billInRecordDto.getPoDistributionId().equals(poDistributionId)
                                    && billInRecordDto.getPoLineId().equals(poLineId)
                                    && billInRecordDto.getPoHeaderId().equals(poHeaderId)
                                    && billInRecordDto.getBatch().equals(lotNumber)
                                    && billInRecordDto.getLineLocationId().equals(lineLocationId)
                                    && billInRecordDto.getItemId().equals(itemId)
                            ) {
                                billInRecordDto.setState(3);
                                billInRecordService.update(billInRecordDto);
                            }
                        }
                        for (BillInReceive billInReceive : billInReceives) {
                            if (billInReceive.getPoDistributionId().equals(poDistributionId)
                                    && billInReceive.getPoLineId().equals(poLineId)
                                    && billInReceive.getPoHeaderId().equals(poHeaderId)
                                    && billInReceive.getBatch().equals(lotNumber)
                                    && billInReceive.getLineLocationId().equals(lineLocationId)
                                    && billInReceive.getItemId().equals(itemId)
                            ) {
                                billInReceive.setBillInReceiveId(null);
                                billInReceive.setReceiptNum(receiptNum);
                                billInReceiveService.save(billInReceive);
                            }
                        }
                    }
                }
                else if(processStatus.equals("PROCESSING")){
                    requestId.setState(TaskTypeConstant.MANAGING);
                    if(billInRecordDtos != null) {
                        for (BillInRecordDto billInRecordDto : billInRecordDtos) {
                            if (billInRecordDto.getPoDistributionId().equals(poDistributionId)
                                    && billInRecordDto.getPoLineId().equals(poLineId)
                                    && billInRecordDto.getPoHeaderId().equals(poHeaderId)
                                    && billInRecordDto.getLineLocationId().equals(lineLocationId)
                                    && billInRecordDto.getBatch().equals(lotNumber)
                                    && billInRecordDto.getItemId().equals(itemId)
                            ) {
                                billInRecordDto.setState(3);
                                billInRecordService.update(billInRecordDto);
                            }
                        }
                        for (BillInReceive billInReceive : billInReceives) {
                            if (billInReceive.getPoDistributionId().equals(poDistributionId)
                                    && billInReceive.getPoLineId().equals(poLineId)
                                    && billInReceive.getPoHeaderId().equals(poHeaderId)
                                    && billInReceive.getBatch().equals(lotNumber)
                                    && billInReceive.getLineLocationId().equals(lineLocationId)
                                    && billInReceive.getItemId().equals(itemId)
                            ) {
                                billInReceive.setBillInReceiveId(null);
                                billInReceiveService.save(billInReceive);
                            }
                        }
                    }
                }
                else{
                    requestId.setState(TaskTypeConstant.FAIL_NO_MANAGE);
                    requestId.setErrorMsg(errorMsg);
                    msg = "error"+errorMsg;
                }
                requestIdService.save(requestId);
            }
            if(msg.indexOf("error")!=(-1)){
                return msg.replace("error","");
            }
        }
        else {
            for(RequestId requestId : requestIds){
                requestIdService.save(requestId);
            }
            return "请求EBS失败，请联系管理员！";
        }
        return "success";
    }

    //账户别名出库
    @Override
    public void accountAliasOut(List<Map<String, String>> lists, List<RequestId> requestIds, RequestIdAuto requestIdAuto) throws Exception {
        EbsBack entityStr = serverVisitAddressService.requestServerCode(requestIdAuto.getRequestId(),
                TaskTypeConstant.WMS_OTHERS_PROC, TaskTypeConstant.SYNCHRONOUS_EXECUTE, null, lists);
        //判断是否请求到参数
        if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
            JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
            for (int i = 0; i < jsonArrays.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                // 处理状态
                String processStatus = MyUtils.backString(jsonObject.get("processStatus"));
                String lastUpdateDate = MyUtils.backString(jsonObject.get("lastUpdateDate"));
                String creationDate = MyUtils.backString(jsonObject.get("creationDate"));
                String errorMsg = MyUtils.backString(jsonObject.get("errorMsg"));
                String lastUpdatedBy = MyUtils.backString(jsonObject.get("lastUpdatedBy"));
                String createdBy = MyUtils.backString(jsonObject.get("createdBy"));
                Integer itemId = MyUtils.backDouble(jsonObject.get("itemId"));
                Integer ebsbackQuantity = MyUtils.backDouble(jsonObject.get("quantity"));
                String subInventory = MyUtils.backString(jsonObject.get("subInventory"));
                Integer locatorId = MyUtils.backInteger(jsonObject.get("locatorId"));
                Integer organizationId = MyUtils.backInteger(jsonObject.get("organizationId"));
                String transDate = MyUtils.backString(jsonObject.get("transDate"));
                String transUom = MyUtils.backString(jsonObject.get("transUom"));
                Integer transTypeId = MyUtils.backInteger(jsonObject.get("transTypeId"));
                String lotNumber = MyUtils.backString(jsonObject.get("transLotNumber"));
                Integer sourceHeaderId = MyUtils.backInteger(jsonObject.get("sourceHeaderId"));
                Integer sourceLineId = MyUtils.backInteger(jsonObject.get("sourceLineId"));
                String transSourceName = MyUtils.backString(jsonObject.get("transSourceName"));
                Integer transSourceId = MyUtils.backInteger(jsonObject.get("transSourceId"));
                Integer id = MyUtils.backInteger(jsonObject.get("id"));
                RequestId requestId = new RequestId(requestIdAuto.getRequestId(), processStatus, lastUpdateDate, lastUpdatedBy,
                        creationDate, createdBy, itemId, ebsbackQuantity, lotNumber, id, transDate, errorMsg, subInventory, locatorId,
                        organizationId, TaskTypeConstant.ACCOUNT_ALIAS, transTypeId.toString(), transUom, sourceHeaderId, sourceLineId,
                        transSourceName, transSourceId);
                if (processStatus.equals("SUCCESS")) {
                    requestId.setState(TaskTypeConstant.SUCCESS);
                } else if (processStatus.equals("PROCESSING")) {
                    requestId.setState(TaskTypeConstant.MANAGING);
                } else {
                    requestId.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                }
                requestIdService.save(requestId);
            }
        } else {
            for (RequestId requestId : requestIds) {
                requestIdService.save(requestId);
            }
        }
    }
}

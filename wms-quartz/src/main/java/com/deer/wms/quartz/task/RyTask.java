package com.deer.wms.quartz.task;

import com.alibaba.fastjson.JSONArray;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.bill.BillOutDetail;
import com.deer.wms.base.system.model.bill.BillOutMaster;
import com.deer.wms.base.system.model.bill.BillOutMasterCriteria;
import com.deer.wms.base.system.model.bill.BillOutMasterDto;
import com.deer.wms.base.system.model.box.*;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.PickTaskDto;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.task.TaskInfoCriteria;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.WareInfo;
import com.deer.wms.base.system.service.*;
import com.deer.wms.base.system.service.bill.IBillOutDetailService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.item.impl.ItemInfoServiceImpl;
import com.deer.wms.base.system.service.mailServer.MailService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.base.system.service.ware.IWareInfoService;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.GuidUtils;
import com.deer.wms.common.utils.poi.ExcelUtil;
import com.deer.wms.framework.util.MyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 定时任务调度测试
 * 
 * @author deer
 */
@Component("ryTask")
public class RyTask
{
    private static final Logger log = LoggerFactory.getLogger(RyTask.class);

    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private BillInCheckRecordService billInCheckRecordService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;
    @Autowired
    private SubInventoryService subInventoryService;
    @Autowired
    private IBillOutMasterService billOutMasterService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private PickTaskService pickTaskService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private MailService mailService;
    @Autowired
    private WarnInformationService warnInformationService;
    @Autowired
    private IBillOutDetailService billOutDetailService;
    @Autowired
    private SubinventoryTransferRecordService subinventoryTransferRecordService;
    @Autowired
    private SluggishOverdueService sluggishOverdueService;
    @Autowired
    private BillInReceiveService billInReceiveService;


    public void ryParams(String params)
    {
        System.out.println("执行有参方法：" + params);
    }

    public void ryNoParams()
    {
        System.out.println("执行无参方法");
    }

    /** 定时发送WMS未检验入库的物料给EBS，并回传给WMS */
//    @Scheduled(cron = "0 30 0/1 * * ? ")
    public void getCheckOutFromEBS(){
//        System.out.println("铁憨憨");
        try {
            List<BillInReceive> billInReceives = billInReceiveService.findList(new BillInReceiveCriteria(1,1001));
            /*List<BillInRecordDto> billInRecordDtoList = billInRecordService.findCheckRecordFromEBS(new BillInRecordCriteria(2,1006));
            if(billInRecordDtoList.size()>0) {
                List<Map<String, String>> selectCheckResultParams = new ArrayList<>();
                for (BillInRecordDto billInRecordDto : billInRecordDtoList) {
                    Map<String, String> map = new HashMap<>();
                    map.put("poLineLocationId", billInRecordDto.getLineLocationId().toString());
                    selectCheckResultParams.add(map);
                }*/
            if(billInReceives.size()>0) {
                List<Map<String, String>> selectCheckResultParams = new ArrayList<>();
                for (BillInReceive BillInReceive : billInReceives) {
                    Map<String, String> map = new HashMap<>();
                    map.put("poLineLocationId", BillInReceive.getLineLocationId().toString());
                    selectCheckResultParams.add(map);
                }

                //查询相关子库信息
                SubInventory qualifiedSubInventory = subInventoryService.findById(TaskTypeConstant.QUALIFIED);
                SubInventory unQualifiedSubInventory = subInventoryService.findById(TaskTypeConstant.UNQUALIFIED);
                //获取EBS的检验结果
                EbsBack entityStr = serverVisitAddressService.requestServerCode("2",
                        TaskTypeConstant.GET_TRANSACTIONS, TaskTypeConstant.QUERY,null, selectCheckResultParams);
                if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
                    JSONArray checkResultJsonArrays = JSONArray.parseArray(entityStr.getRows());
                    //创建回传结果
                    List<Map<String, String>> deliveryParams = new ArrayList<>();
                    List<RequestId> requestIds = new ArrayList<>();
                    List<Integer> checkIds = new ArrayList<>();
                    RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS交货数据写入EBS接口");

                    for (int i = 0; i < checkResultJsonArrays.size(); i++) {
                        com.alibaba.fastjson.JSONObject checkResultJsonObject = checkResultJsonArrays.getJSONObject(i);
                        // 接收库存组织ID
                        Integer organizationId = MyUtils.backInteger(checkResultJsonObject.get("organizationId"));
                        // 接收号
                        String receiptNum = MyUtils.backString(checkResultJsonObject.get("receiptNum"));
                        // 检验ID
                        Integer transactionId = MyUtils.backInteger(checkResultJsonObject.get("transactionId"));
                        // 采购订单头ID
                        Integer poHeaderId = MyUtils.backInteger(checkResultJsonObject.get("poHeaderId"));
                        //采购订单行ID
                        Integer poLineId = MyUtils.backInteger(checkResultJsonObject.get("poLineId"));
                        // 发运行ID
                        Integer lineLocationId = MyUtils.backInteger(checkResultJsonObject.get("poLineLocationId"));
                        //  分配行ID
                        Integer poDistributionId = MyUtils.backInteger(checkResultJsonObject.get("poDistributionId"));
                        // 物料Id
                        Integer itemId = MyUtils.backInteger(checkResultJsonObject.get("itemId"));
                        // 接收数量
                        Integer quantity = MyUtils.backDouble(checkResultJsonObject.get("quantity"));
                        // 检验结果
                        String transactionType = MyUtils.backString(checkResultJsonObject.get("transactionType"));
                        //批次
                        String batch = MyUtils.backString(checkResultJsonObject.get("vendorLotNum"));
                        //根据检验Id查找记录，没有则写入检验记录
                        BillInCheckRecord alreadyCheckRecord = billInCheckRecordService.findByTransactionId(new BillInCheckRecordCriteria(receiptNum,transactionId));
                        if(alreadyCheckRecord == null){
                            BillInCheckRecord billInCheckRecord = new BillInCheckRecord(organizationId, receiptNum, transactionId,
                                    poHeaderId, poLineId, lineLocationId, poDistributionId, itemId, quantity, transactionType,
                                    DateUtils.getTime(), batch, 1);
                            billInCheckRecordService.save(billInCheckRecord);

                            //根据接收单号查找记录
                            List<BillInReceive> billInReceiveList = billInReceiveService.findList(new BillInReceiveCriteria(1,receiptNum));
                            //如果能查到入库记录写入交货
                            if(billInReceiveList.size()>0) {
                                BillInReceive billInReceive =  billInReceiveList.get(0);
                                billInReceive.setState(2);
                                billInReceiveService.update(billInReceive);
                                checkIds.add(billInCheckRecord.getCheckId());
                                RequestId requestId = new RequestId(requestIdAuto.getRequestId(), quantity, batch, DateUtils.getTime(), receiptNum,
                                        qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : Integer.parseInt(qualifiedSubInventory.getSlotting()),
                                        organizationId, transactionId, TaskTypeConstant.DELIVERY, TaskTypeConstant.FAIL_WAIT_MANAGE,
                                        "WMS交货数据写入EBS接口失败", "ERROR");
                                requestIds.add(requestId);
                                //交货写入请求参数并修改入库记录检验状态
                                if (transactionType.equals("ACCEPT")) {
                                    deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                            qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : qualifiedSubInventory.getSlotting(),
                                            batch, quantity.toString(), receiptNum));
                                } else {
                                    deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                            unQualifiedSubInventory.getSubInventoryCode(), unQualifiedSubInventory.getSlotting() == null ? null : unQualifiedSubInventory.getSlotting(),
                                            batch, quantity.toString(), receiptNum));
                                }

                            }
                            //如果查不到根据料号，批次，订单号，数量查询
                            else{
                                /*List<BillInRecordDto> billInRecordDtos = billInRecordService.findList(new BillInRecordCriteria(
                                        1010,poHeaderId,poLineId,lineLocationId,poDistributionId,batch,itemId,quantity));*/
                                List<BillInReceive> billInReceiveLists = billInReceiveService.findList(new BillInReceiveCriteria(
                                        1,1002,poHeaderId,poLineId,lineLocationId,poDistributionId,batch,itemId,quantity));

                                //如果查到则此条需要交货
                                if(billInReceiveLists.size()>0){
                                    BillInReceive billInReceive = billInReceiveLists.get(0);
                                    billInReceive.setReceiptNum(receiptNum);
                                    billInReceive.setState(2);
                                    billInReceiveService.update(billInReceive);
                                    checkIds.add(billInCheckRecord.getCheckId());
                                    //交货写入请求参数并修改入库记录检验状态
                                    if(transactionType.equals("ACCEPT")){
                                        deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                                qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : qualifiedSubInventory.getSlotting(),
                                                batch, quantity.toString(), receiptNum));
                                    }
                                    else{
                                        deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                                unQualifiedSubInventory.getSubInventoryCode(), unQualifiedSubInventory.getSlotting() == null ? null : unQualifiedSubInventory.getSlotting(),
                                                batch, quantity.toString(), receiptNum));
                                    }
                                    RequestId requestId = new RequestId(requestIdAuto.getRequestId(), quantity, batch, DateUtils.getTime(), receiptNum,
                                            qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : Integer.parseInt(qualifiedSubInventory.getSlotting()),
                                            organizationId, transactionId, TaskTypeConstant.DELIVERY, TaskTypeConstant.FAIL_WAIT_MANAGE,
                                            "WMS交货数据写入EBS接口失败", "ERROR");
                                    requestIds.add(requestId);
                                }
                            }

                            /*//根据接收单号查找检验记录
                            List<BillInRecordDto> billInRecordDtoOnes = billInRecordService.findList(new BillInRecordCriteria(receiptNum,2));
                            //如果能查到入库记录写入交货
                            if(billInRecordDtoOnes.size()>0) {
                                BillInRecordDto billInRecordDto =  billInRecordDtoOnes.get(0);
                                billInRecordDto.setState(3);
                                billInRecordService.update(billInRecordDto);
                                checkIds.add(billInCheckRecord.getCheckId());
                                //交货写入请求参数并修改入库记录检验状态
                                if (transactionType.equals("ACCEPT")) {
                                    deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                            qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : qualifiedSubInventory.getSlotting(),
                                            batch, quantity.toString(), receiptNum));
                                } else {
                                    deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                            unQualifiedSubInventory.getSubInventoryCode(), unQualifiedSubInventory.getSlotting() == null ? null : unQualifiedSubInventory.getSlotting(),
                                            batch, quantity.toString(), receiptNum));
                                }
                                RequestId requestId = new RequestId(requestIdAuto.getRequestId(), quantity, batch, DateUtils.getTime(), receiptNum,
                                        qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : Integer.parseInt(qualifiedSubInventory.getSlotting()),
                                        organizationId, transactionId, TaskTypeConstant.DELIVERY, TaskTypeConstant.FAIL_WAIT_MANAGE,
                                        "WMS交货数据写入EBS接口失败", "ERROR");
                                requestIds.add(requestId);
                            }
                            //如果查不到根据料号，批次，订单号，数量查询
                            else{
                                List<BillInRecordDto> billInRecordDtos = billInRecordService.findList(new BillInRecordCriteria(
                                        1010,poHeaderId,poLineId,lineLocationId,poDistributionId,batch,itemId,quantity));
                                //如果查到则此条需要交货
                                if(billInRecordDtos.size()>0){
                                    BillInRecordDto billInRecord = billInRecordDtos.get(0);
                                    billInRecord.setReceiptNum(receiptNum);
                                    billInRecord.setState(3);
                                    billInRecordService.update(billInRecord);
                                    checkIds.add(billInCheckRecord.getCheckId());
                                    //交货写入请求参数并修改入库记录检验状态
                                    if (transactionType.equals("ACCEPT")) {
                                        deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                                qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : qualifiedSubInventory.getSlotting(),
                                                batch, quantity.toString(), receiptNum));
                                    } else {
                                        deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), transactionId.toString(), organizationId.toString(),
                                                unQualifiedSubInventory.getSubInventoryCode(), unQualifiedSubInventory.getSlotting() == null ? null : unQualifiedSubInventory.getSlotting(),
                                                batch, quantity.toString(), receiptNum));
                                    }
                                    RequestId requestId = new RequestId(requestIdAuto.getRequestId(), quantity, batch, DateUtils.getTime(), receiptNum,
                                            qualifiedSubInventory.getSubInventoryCode(), qualifiedSubInventory.getSlotting() == null ? null : Integer.parseInt(qualifiedSubInventory.getSlotting()),
                                            organizationId, transactionId, TaskTypeConstant.DELIVERY, TaskTypeConstant.FAIL_WAIT_MANAGE,
                                            "WMS交货数据写入EBS接口失败", "ERROR");
                                    requestIds.add(requestId);
                                }
                            }*/
                        }
                    }

                    //新增检验结果记录查询
                    if(checkIds.size()>0){
                        //查询未检验箱
                        List<BoxItem> boxItems = boxItemService.selectBoxItemList(new BoxItem(TaskTypeConstant.DESIRED));
                        List<String> boxCodes = new ArrayList<>();
                        if(boxItems.size()>0) {
                            for (BoxItem boxItem : boxItems) {
                                //根据批次料号，入库单Id查询检验结果
                                List<BillInCheckRecord> billInCheckRecords = billInCheckRecordService.findByBillInDetailIds(
                                        new BillInCheckRecordCriteria(boxItem.getBatch(),boxItem.getBillInDetailId(),boxItem.getItemCode()));
                                //如果查出来的检验结果大于2则是不合格
                                if (billInCheckRecords.size()>2) {
                                    boxItem.setSubInventoryId(TaskTypeConstant.UNQUALIFIED);
                                } else if(billInCheckRecords.size()<=0){

                                }
                                //如果检验结果是一条
                                else{
                                    //判断是合格还是不合格
                                    if(billInCheckRecords.get(0).getTransaction().equals("ACCEPT")){
                                        boxItem.setSubInventoryId(TaskTypeConstant.QUALIFIED);
                                    }else {
                                        boxItem.setSubInventoryId(TaskTypeConstant.UNQUALIFIED);
                                    }
                                }
                                boxCodes.add(boxItem.getBoxCode());
                                boxItemService.update(boxItem);
                            }
                            //WMS回传EBS交货数据
                            requestIdService.delivery(requestIds,deliveryParams,requestIdAuto);
                        }
                        //根据箱号查询IQC检验库存扣减（只出数据不出实物）
                        List<PickTaskDto> pickTaskDtos = pickTaskService.findList(new PickTaskCriteria(1,TaskTypeConstant.IQC_OUT,boxCodes));
                        if(pickTaskDtos.size()>0){
                            List<Map<String, String>> IQCParams = new ArrayList<>();
                            List<RequestId> requestIdsIQCOut = new ArrayList<>();
                            RequestIdAuto IQCRequestIdAuto = requestIdAutoService.backAutoId("WMS非工单出库IQC数据发放");
                            for(PickTaskDto pickTaskDto : pickTaskDtos){
                                BillOutMaster billOutMaster = billOutMasterService.findById(pickTaskDto.getBillId());
                                BillOutDetail billOutDetail = billOutDetailService.findById(pickTaskDto.getBillOutDetailId());
                                BoxItemDto boxItem = boxItemService.findList(new BoxItemCriteria(pickTaskDto.getBoxCode())).get(0);
                                IQCParams.add(MyUtils.accountAliasOut(TaskTypeConstant.TRANSACTION_OUT,TaskTypeConstant.organizationId.toString(),
                                        pickTaskDto.getInventoryItemId().toString(),boxItem.getSubInventoryCode(),
                                        boxItem.getSlotting()==null?null:boxItem.getSlotting(),pickTaskDto.getAccountAlias(),
                                        billOutMaster.getAccountAliasId().toString(),boxItem.getBatch(),(pickTaskDto.getPickQuantity()*(-1))+"",
                                        MyUtils.getNinetySecondsAgo(),boxItem.getUnit(),billOutMaster.getBillId().toString(),billOutDetail.getBillOutDetailId().toString()));

                                requestIdsIQCOut.add(new RequestId(IQCRequestIdAuto.getRequestId(),"ERROR",pickTaskDto.getInventoryItemId(),pickTaskDto.getPickQuantity()*(-1),
                                        boxItem.getBatch(), MyUtils.getNinetySecondsAgo(),"WMS请求EBS扣减库存失败",boxItem.getSubInventoryCode(),
                                        boxItem.getSlotting()==null?null:Integer.parseInt(boxItem.getSlotting()),TaskTypeConstant.organizationId,TaskTypeConstant.ACCOUNT_ALIAS,
                                        TaskTypeConstant.FAIL_WAIT_MANAGE,TaskTypeConstant.TRANSACTION_OUT,boxItem.getUnit(),billOutMaster.getBillId(),billOutDetail.getBillOutDetailId(),
                                        pickTaskDto.getAccountAlias(), billOutMaster.getAccountAliasId()));

                                boxItem.setQuantity(boxItem.getQuantity()-pickTaskDto.getPickQuantity());
                                boxItemService.update(boxItem);
                                pickTaskDto.setPickState(4);
                                pickTaskDto.setSubInventoryId(boxItem.getSubInventoryId());
                                pickTaskDto.setOutTime(DateUtils.getTime());
                                pickTaskService.update(pickTaskDto);

                                billOutMaster.setState(2);
                                billOutMasterService.update(billOutMaster);

                                billOutDetail.setAlreadyOutQuantity(billOutDetail.getQuantity());
                                billOutDetailService.update(billOutDetail);
                            }
                            requestIdService.accountAliasOut(IQCParams,requestIdsIQCOut,IQCRequestIdAuto);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE);
        }
    }

    /**
     * 每一个小时发一次工单发料
     */
    public void timeCalculate(){
        List<TaskInfo> taskInfos = taskInfoService.findByTypeAndState();
        String time = MyUtils.getOneHourAgo();
        if(taskInfos.size()>0){
            List<BillOutMasterDto> billOutMasters = billOutMasterService.findList(
                    new BillOutMasterCriteria(TaskTypeConstant.WORKER_ORDER_OUT,0));
            if(billOutMasters.size()>0) {
                TaskInfo taskInfo = taskInfos.get(0);
                String memo = "";
                if (taskInfo.getType() == TaskTypeConstant.CELL_TO_PAPER_COUNTERS ||
                        taskInfo.getType() == TaskTypeConstant.COUNT_TO_CARRIER ||
                        taskInfo.getType() == TaskTypeConstant.BOX_TO_CELL_FROM_PAPER_COUNTERS ||
                        taskInfo.getType() == TaskTypeConstant.CARRIER_TO_AVG_FROM_PAPER_COUNTERS
                ) {
                    memo = "." + DateUtils.getTime() + "一小时前工单出库任务暂未完成，当前工单延迟一小时执行。";
                }
                /*else if (taskInfo.getType() == TaskTypeConstant.CHECK_FROM_PAPER_COUNTERS) {
                    memo = "." + DateUtils.getTime() + "异常处理任务中，当前工单延迟一小时执行。";
                } */
                else {
                    memo = "." + DateUtils.getTime() + "正在盘点任务中，当前工单延迟一小时执行。";
                }
                for(BillOutMasterDto billOutMasterDto : billOutMasters){
                    billOutMasterDto.setCreateTime(MyUtils.backAddOneHour(billOutMasterDto.getCreateTime()));
                    billOutMasterDto.setMemo(billOutMasterDto.getMemo()+memo);
                    billOutMasterService.update(billOutMasterDto);
                }
            }
        }
        else {

            PickTaskCriteria criteria = new PickTaskCriteria();
            criteria.setStartTime(time);
            criteria.setEndTime(DateUtils.getTime());
            criteria.setState(0);
            criteria.setWorkOrderStockState(1);
            criteria.setPickState(1);
            criteria.setBillOutMasterType(1);
            String boxCode = "";
            List<PickTaskDto> pickTaskDtos = pickTaskService.findList(criteria);
            for (PickTaskDto pickTaskDto : pickTaskDtos) {
                if (!boxCode.equals(pickTaskDto.getBoxCode())) {
                    boxCode = pickTaskDto.getBoxCode();
                    //11-从货位上拿托盘到点数机
                    TaskInfo outCountDevice = new TaskInfo(new GuidUtils().toString(), MyUtils.connectShelfNameAndRowAndColumn(
                            pickTaskDto.getShelfName(), pickTaskDto.getsColumn(), pickTaskDto.getsRow()), "120", TaskTypeConstant.CELL_TO_PAPER_COUNTERS,
                            0, pickTaskDto.getQuantity(), boxCode, pickTaskDto.getSequence().toString(), pickTaskDto.getBillOutDetailId());
                    outCountDevice.setTaskStartTime(DateUtils.getTime());
                    taskInfoService.save(outCountDevice);
                    CellInfo cellInfo = cellInfoService.findById(pickTaskDto.getCellId());
                    cellInfo.setState(2);
                    cellInfoService.update(cellInfo);
                    BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(pickTaskDto.getBoxCode());
                    boxInfo.setBoxState(2);
                    boxInfoService.update(boxInfo);
                }
                pickTaskDto.setPickState(2);
                pickTaskService.update(pickTaskDto);
                BillOutMaster billOutMaster = billOutMasterService.findById(pickTaskDto.getBillId());
                billOutMaster.setState(1);
                billOutMasterService.update(billOutMaster);
            }
        }
    }

    /**
     * 定时查询不合格物料滞库时间进行对比，超期则报警
     */
    public void unqualifiedStorageSuggishOverdue(){
        try {
            WareInfo wareInfo = TaskTypeConstant.wareInfo;
//                    wareInfoService.findById(212);
            List<UnqualifiedOverTakeCanDelayDays> boxItemDtos = boxItemService.findUnqualifiedOverTakeCanDelayDays(
                    new BoxItemCriteria(TaskTypeConstant.UNQUALIFIED, wareInfo.getUnqualifiedStorageDay()));
//            if(boxItemDtos.size()>0) {
                String msg = "不合格库存存储时间超过设定时间";
                WarnInformation warnInformation = new WarnInformation(msg,
                        TaskTypeConstant.UNDEALT,TaskTypeConstant.UNQUALIFIED_OVERTAKE_CAN_DELAY_DAYS,DateUtils.getTime());
                warnInformationService.save(warnInformation);

                WebSocketServer.sendInfo(msg,TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());

            log.info("transferOverdue email send");
            ExcelUtil<UnqualifiedOverTakeCanDelayDays> util = new ExcelUtil<UnqualifiedOverTakeCanDelayDays>(UnqualifiedOverTakeCanDelayDays.class);
                mailService.analysisSendMail(util.exportExcel(boxItemDtos, "不合格库存超期明细"), "不合格库存超期明细",
                        "当前有不合格库存存储时间超过设定日期，附件为明细请查收!!");
//            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }

    /**
     * 每天检测物料是否过期，过期自动转移物料至过期库
     */
    public void transferOverdue(){
//        System.out.println("铁憨憨");
        try {
            WareInfo wareInfo = TaskTypeConstant.wareInfo;
//                    wareInfoService.findById(212);
            BoxItemCriteria boxItemCriteria = new BoxItemCriteria();
            boxItemCriteria.setWillOverdueDay(wareInfo.getStockWaring());
            List<BoxItemDto> boxItemDtos = boxItemService.findWillOverdue(boxItemCriteria);
            int count = 1;
            List<OverdueList> overdueLists = new ArrayList<>();
            OverdueList overdueList = null;
//            if(boxItemDtos.size()>0){
                //定义传给EBS的集合
                List<Map<String, String>> lists = new ArrayList<>();
                //获取转移到的子库信息
                SubInventory subInventory = subInventoryService.findById(TaskTypeConstant.OVER_DUE);
                //获取自增长请求Id
                RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS转库数据写入EBS接口");
                //定义请求接口记录集合
                List<RequestId> requestIds = new ArrayList<>();
                for(BoxItemDto boxItemDto : boxItemDtos){
                    overdueList = new OverdueList(count,boxItemDto.getItemCode(),boxItemDto.getItemName(),boxItemDto.getSupplierName(),
                            boxItemDto.getPd(),boxItemDto.getBatch(),boxItemDto.getUnit(),boxItemDto.getQuantity());
                    if(boxItemDto.getSubInventoryId().equals(TaskTypeConstant.QUALIFIED)){
                        overdueList.setExp(boxItemDto.getExp());
                    }else if(boxItemDto.getSubInventoryId().equals(TaskTypeConstant.POSTPONE)){
                        overdueList.setPostpone(boxItemDto.getExp());
                    }
                    overdueLists.add(overdueList);
                    //如果剩余天数小于1天
                    /*if(boxItemDto.getSurplusDay()<1){
                        //查找所有过期物料箱号
                        boxItemCriteria.setItemCode(boxItemDto.getItemCode());
                        boxItemCriteria.setExp(boxItemDto.getExp());
                        boxItemCriteria.setBatch(boxItemDto.getBatch());
                        List<BoxItemDto> boxItems = boxItemService.findBoxItemList(boxItemCriteria);
                        if(boxItems.size()>0) {
                            //循环箱号
                            for (BoxItem boxItem : boxItems) {
                                pickTaskCriteria.setBoxCode(boxItem.getBoxCode());
                                pickTaskCriteria.setPickState(1);
                                List<PickTaskDto> pickTaskDtos = pickTaskService.findList(pickTaskCriteria);
                                //查询未下发出库任务的记录
                                for (PickTaskDto pickTaskDto : pickTaskDtos) {
                                    BillOutMaster billOutMaster = billOutMasterService.findById(pickTaskDto.getBillId());
                                    //根据下发的工单Id查找涉及此工单的出库记录及箱号
                                    List<PickTaskDto> pickTaskDtoOnes = pickTaskService.findList(new PickTaskCriteria(1,pickTaskDto.getBillOutDetailId()));
                                    for(PickTaskDto pickTaskDtoOne : pickTaskDtoOnes){
                                        pickTaskDtoOne.setPickState(5);
                                        pickTaskService.update(pickTaskDtoOne);
//                                        BoxItem boxItemOne = boxItemService.getBoxItemByBoxCode(pickTaskD)
                                    }
                                    billOutMaster.setState(3);
                                    billOutMasterService.update(billOutMaster);
                                }
                                SubinventoryTransferRecord subinventoryTransferRecord = new SubinventoryTransferRecord(
                                        boxItem.getBoxCode(), boxItem.getItemCode(), boxItem.getBatch(),  boxItem.getQuantity(),
                                        DateUtils.getTime(), TaskTypeConstant.VIRTUAL_CARD_NO, boxItem.getSubInventoryId(),
                                        TaskTypeConstant.OVER_DUE, "过期"
                                );


                                boxItem.setSubInventoryId(TaskTypeConstant.OVER_DUE);
                                boxItemService.update(boxItem);
                            }
                        }
                    }*/
                }
                List<BoxItemDto> boxItemDtoList = boxItemService.findList(new BoxItemCriteria("",1007));
                if(boxItemDtoList.size()>0){
                    for(BoxItemDto boxItemDto : boxItemDtoList){
                        //写入转库原因
                        //保存转库记录
                        SubinventoryTransferRecord subinventoryTransferRecord = new SubinventoryTransferRecord(boxItemDto.getBoxCode(),
                                boxItemDto.getItemCode(), boxItemDto.getBatch(), boxItemDto.getQuantity(), DateUtils.getTime(),
                                null, boxItemDto.getSubInventoryId(),
                                subInventory.getSubInventoryId(), "当前物料超过有效期");
                        subinventoryTransferRecord.setForExp(boxItemDto.getExp());
                        subinventoryTransferRecord.setForExp(boxItemDto.getExp());
                        subinventoryTransferRecordService.save(subinventoryTransferRecord);
                        //修改当前箱子库
                        boxItemDto.setSubInventoryId(subInventory.getSubInventoryId());
                        boxItemService.update(boxItemDto);
                        //往传给EBS的集合添加数据
                        lists.add(MyUtils.subInventoryTransfer(TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, TaskTypeConstant.organizationId.toString(),
                                boxItemDto.getInventoryItemId().toString(), boxItemDto.getQuantity().toString(), boxItemDto.getSubInventoryCode(),
                                boxItemDto.getSlotting() == null ? null : boxItemDto.getSlotting(), MyUtils.getNinetySecondsAgo(), boxItemDto.getUnit(),
                                subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : subInventory.getSlotting(),
                                boxItemDto.getBatch(), boxItemDto.getBillId().toString(), boxItemDto.getBillInDetailId().toString()));
                        //往请求记录的集合中添加数据
                        RequestId requestId = new RequestId(requestIdAuto.getRequestId(), boxItemDto.getInventoryItemId(), boxItemDto.getQuantity(),
                                boxItemDto.getBatch(), DateUtils.getTime(), "WMS调用EBS子库转移接口失败", boxItemDto.getSubInventoryCode(),
                                boxItemDto.getSlotting() == null ? null : Integer.parseInt(boxItemDto.getSlotting()), TaskTypeConstant.organizationId, TaskTypeConstant.TRANSFER,
                                TaskTypeConstant.FAIL_WAIT_MANAGE, TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, boxItemDto.getUnit(),
                                subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : Integer.parseInt(subInventory.getSlotting()),
                                boxItemDto.getBillId(), boxItemDto.getBillInDetailId(), "ERROR");
                        requestIds.add(requestId);
                        //根据箱号查询当前箱锁定物料
                        List<PickTaskDto> pickTasks = pickTaskService.findList(new PickTaskCriteria(1,boxItemDto.getBoxCode(),TaskTypeConstant.WORKER_ORDER_OUT));
                        if(pickTasks.size()>0){
                            for(PickTaskDto pickTaskDto : pickTasks) {
                                //当前工单取消
                                BillOutMaster billOutMaster = billOutMasterService.findById(pickTaskDto.getBillId());
                                billOutMaster.setState(3);
                                billOutMaster.setMemo(billOutMaster.getMemo()+".取消原因：转移到过期库");
                                billOutMasterService.update(billOutMaster);
                                //根据查询到的出库单id查询当前出库单锁定哪些箱物料
                                List<PickTaskDto> pickTaskOnes = pickTaskService.findList(new PickTaskCriteria(1,pickTaskDto.getBillOutDetailId(),TaskTypeConstant.WORKER_ORDER_OUT));
                                for(PickTaskDto pickTaskDtoOne:pickTaskOnes){
                                    //一个工单不会生成两条锁定同一箱的出库任务，查询此箱并释放锁定库存
                                    BoxItem boxItem = boxItemService.getBoxItemByBoxCode(pickTaskDtoOne.getBoxCode());
                                    Integer surplus = boxItem.getForecastStockQuantity() - pickTaskDtoOne.getPickQuantity();
                                    if(surplus>0){
                                        boxItem.setForecastStockQuantity(surplus);
                                    }else {
                                        boxItem.setForecastStockQuantity(0);
                                        boxItem.setWorkOrderStockState(0);
                                    }
                                    boxItemService.update(boxItem);
                                    //锁定任务取消
                                    pickTaskDtoOne.setPickState(5);
                                    pickTaskService.update(pickTaskDtoOne);
                                }
                            }
                        }
                    }
                }
                //EBS子库转移
                requestIdService.subInventoryTransfer(requestIdAuto,requestIds,lists);
                log.info("transferOverdue email send");
//                if(overdueLists.size()>0) {
                    ExcelUtil<OverdueList> util = new ExcelUtil<OverdueList>(OverdueList.class);
                    mailService.analysisSendMail(util.exportExcel(overdueLists, "过期清单"), "过期清单", "附件为过期清单，请查收!!");
//                }
//            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //每月呆滞过期报表
    public void sluggishOverdue(){
//        System.out.println("铁憨憨");
        try {
//            WorkerOrderIssueTime workerOrderIssueTime = workerOrderIssueTimeService.findById(1);
            WorkerOrderIssueTime workerOrderIssueTime = TaskTypeConstant.workerOrderIssueTime;
//            if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == workerOrderIssueTime.getSluggishExportDate()) {
                BoxItemCriteria boxItemCriteria = new BoxItemCriteria();
                boxItemCriteria.setSluggishExportParam(workerOrderIssueTime.getSluggishExportParam());
                List<BoxItemDto> boxItems = boxItemService.findSluggishOverdue(boxItemCriteria);
                if (boxItems.size() > 0) {
                    //上次申报数量
                    Integer lastQuantity = 0;
                    String firstDeclareTime = null;
                    Integer firstDeclareQuantity = 0;
                    String lastOutTime = null;
                    SluggishOverdueCriteria sluggishOverdueCriteria = new SluggishOverdueCriteria();
                    TaskInfoCriteria taskInfoCriteria = new TaskInfoCriteria();
                    List<Integer> lists = new ArrayList<>();
                    lists.add(TaskTypeConstant.CELL_TO_OPERATOR_FLOOR);
                    lists.add(TaskTypeConstant.CELL_TO_PAPER_COUNTERS);
                    taskInfoCriteria.setTypes(lists);
                    for (BoxItem boxItem : boxItems) {
                        sluggishOverdueCriteria.setItemCode(boxItem.getItemCode());
                        sluggishOverdueCriteria.setBatch(boxItem.getBatch());
                        sluggishOverdueCriteria.setExp(boxItem.getExp());
                        List<SluggishOverdue> sluggishOverdueOne = sluggishOverdueService.findSluggishByParam(sluggishOverdueCriteria);
                        taskInfoCriteria.setItemCode(boxItem.getItemCode());
                        taskInfoCriteria.setBatch(boxItem.getBatch());
                        taskInfoCriteria.setExp(boxItem.getExp());
                        TaskInfo taskInfo = taskInfoService.findByItemCodeAndBatchAndExp(taskInfoCriteria);
                        if (taskInfo != null) {
                            lastOutTime = taskInfo.getTaskStartTime();
                        }
                        if (sluggishOverdueOne.size() == 1) {
                            lastQuantity = sluggishOverdueOne.get(0).getQuantity();
                            firstDeclareQuantity = sluggishOverdueOne.get(0).getQuantity();
                            firstDeclareTime = sluggishOverdueOne.get(0).getCreateTime();
                        } else if (sluggishOverdueOne.size() > 1) {
                            lastQuantity = sluggishOverdueOne.get(sluggishOverdueOne.size() - 1).getQuantity();
                            firstDeclareQuantity = sluggishOverdueOne.get(0).getQuantity();
                            firstDeclareTime = sluggishOverdueOne.get(0).getCreateTime();
                        }
                        SluggishOverdue sluggishOverTwo = new SluggishOverdue(boxItem.getItemCode(), boxItem.getBatch(),
                                lastQuantity, boxItem.getQuantity(), boxItem.getPd(), boxItem.getExp(), boxItem.getInTime(),
                                DateUtils.getTime(), lastOutTime, workerOrderIssueTime.getSluggishExportParam(),
                                firstDeclareTime, firstDeclareQuantity);
                        sluggishOverdueService.save(sluggishOverTwo);
                    }
//                }
                List<SluggishOverdueDto> sluggishOverdues = sluggishOverdueService.findList(new SluggishOverdueCriteria(DateUtils.getDate()));
                log.error("test email");
                log.error("size:" + sluggishOverdues.size());
//                if (sluggishOverdues.size()>0){
                    ExcelUtil<SluggishOverdueDto> util = new ExcelUtil<SluggishOverdueDto>(SluggishOverdueDto.class);
                    mailService.analysisSendMail(util.exportExcel(sluggishOverdues, "物料呆滞报表"), "物料呆滞报表", "附件为物料呆滞报表，请查收!!");
                    log.error("test email end");
//                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}

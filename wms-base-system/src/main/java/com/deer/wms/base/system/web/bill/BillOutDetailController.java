package com.deer.wms.base.system.web.bill;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.bill.*;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemCriteria;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.model.ware.Door;
import com.deer.wms.base.system.service.*;
import com.deer.wms.base.system.service.MESWebService.WebserviceResponse;
import com.deer.wms.base.system.service.bill.IBillInDetailService;
import com.deer.wms.base.system.service.bill.IBillInMasterService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.base.system.service.ware.IDoorService;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.base.system.web.box.BoxItemController;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.core.controller.BaseController;
import com.deer.wms.common.core.page.TableDataInfo;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.core.result.Result;
import com.deer.wms.common.core.result.ResultGenerator;
import com.deer.wms.base.system.service.bill.IBillOutDetailService;
import com.deer.wms.common.enums.BusinessType;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.GuidUtils;
import com.deer.wms.common.utils.Threads;
import com.deer.wms.framework.util.MyUtils;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/out/billOutDetail")
public class BillOutDetailController extends BaseController {

    @Autowired
    private IBillOutDetailService billOutDetailService;
    @Autowired
    private IBillOutMasterService billOutMasterService;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private IDoorService doorService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private SubinventoryTransferRecordService subinventoryTransferRecordService;

    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private SubInventoryService subInventoryService;
    @Autowired
    private AccountAliasService accountAliasService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;
    @Autowired
    private CallAgvService callAgvService;
    @Autowired
    private PickTaskService pickTaskService;
    @Autowired
    private WarnInformationService warnInformationService;

    /**
     * 根据BillOutDetailId删除BillOutDetail
     */
    @RequiresPermissions("out:billOutDetail:deleteBillOutDetailByBillOutDetailId")
    @GetMapping("/deleteBillOutDetailByBillOutDetailId")
    @ResponseBody
    public Result deleteBillOutDetailByBillOutDetailId(Integer billOutDetailId) {
        billOutDetailService.deleteBillOutDetailByBillOutDetailId(billOutDetailId);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 根据出库单ID查询出库单详情
     */
    @RequiresPermissions("out:billOutDetail:findListByBillId")
    @PostMapping("/findListByBillId")
    @ResponseBody
    public TableDataInfo list(Integer billId) {
        startPage();
        List<BillOutDetailDto> list = billOutDetailService.findListByBillId(billId);
        return getDataTable(list);
    }

    private int backState = 2;

    @PostMapping("/supplyWCSTransfer")
    @ResponseBody
    public Result selectInventory(HttpServletRequest request) {
        try {
            String wholeStr = MyUtils.analysisHttpServletRequest(request);
            JSONObject jsonObject = JSONObject.parseObject(wholeStr);
            //解析是否有报警信息
            if (jsonObject.get("Alarms") != null) {
                JSONArray jsonArrays = JSONArray.parseArray(jsonObject.get("Alarms").toString().trim());
                if (jsonArrays.size() > 0) {
                    for (int i = 0; i < jsonArrays.size(); i++) {
                        JSONObject alarms = jsonArrays.getJSONObject(i);
                        String code = alarms.get("Code").toString().trim();
                        Integer state = Integer.parseInt(alarms.get("State").toString().trim());
                        WarnInformation warnInformation = new WarnInformation(DateUtils.getTime(), code, state, TaskTypeConstant.UNDEALT);
                        if (code.equals("Alarm_Code")) {
                            warnInformation.setMemo("堆垛机硬件异常报警,请查看详细信息!");
                        } else if (code.equals("Count_Device_Alarm")) {
                            warnInformation.setMemo("分批机硬件异常报警,请查看详细信息!");
                        } else if (code.equals("Printer_Alarm")) {
                            warnInformation.setMemo("贴标机打印异常报警,请查看详细信息!");
                        } else if (code.equals("Paster_Alarm")) {
                            warnInformation.setMemo("贴标机贴标异常报警,请查看详细信息!");
                        } else if (code.equals("Scanner_Alarm")) {
                            warnInformation.setMemo("出库扫码校验异常,请查看详细信息!");
                        } else {
                            warnInformation.setMemo("输送线硬件异常报警,请查看详细信息!");
                        }

                        warnInformation.setType(TaskTypeConstant.HARDWARE_ALARM);

                        warnInformationService.save(warnInformation);
                    }
                    WebSocketServer.sendInfo("覆铜板库硬件异常，请查看异常信息明细表！", TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
                }
            }
            if (TaskTypeConstant.AUTO_EXECUTE.equals(1)) {

                //出库段入料口位置，AGVCacheAStatus 1-有载具 0-无载具，
                String AGVCacheAStatus = jsonObject.get("AGVCacheAStatus") == null ? "" : jsonObject.get("AGVCacheAStatus").toString().trim();
                //出库段入料口提升滚轮，LiftAStatus 1-升降机在下 0-升降机在上
                String LiftAStatus = jsonObject.get("LiftAStatus") == null ? "" : jsonObject.get("LiftAStatus").toString().trim();
                //顶升位置是否有货，RiseTurnStatus 1-有载具  0-无载具
                String RiseTurnStatus = jsonObject.get("RiseTurnStatus") == null ? "" : jsonObject.get("RiseTurnStatus").toString().trim();
                //125位置是否有货
                String MOneTwoFiveStatus = jsonObject.get("MOneTwoFiveStatus") == null ? "" : jsonObject.get("MOneTwoFiveStatus").toString().trim();
                //入点数机载具编码
                String QRCode = jsonObject.get("QRCode") == null ? "" : jsonObject.get("QRCode").toString().trim();

                //出库段出料口状态
                if (AGVCacheAStatus.equals("0") && LiftAStatus.equals("1") && RiseTurnStatus.equals("0") && MOneTwoFiveStatus.equals("0")) {
                    //立体库拿取空载具
                    if (TaskTypeConstant.GET_EMPTY_BOX_STATE == 1) {
                        EmptyShelfInReq();
                    }
//                } else if (AGVCacheAStatus.equals("0") && LiftAStatus.equals("1") && RiseTurnStatus.equals("0") && MOneTwoFiveStatus.equals("1")) {
                } else if (AGVCacheAStatus.equals("0") && RiseTurnStatus.equals("0") && MOneTwoFiveStatus.equals("1")) {
                    //立体库取空载具成功
                    if (backState == 1) {
                        StockTakeShelf();
                    }
                }
                //验证载具编码
                if (!QRCode.equals("")) {
                    /*Carrier carrier = carrierService.inValidate(QRCode);
                    if(carrier == null){
                        WebSocketServer.sendInfo("当前进入点数机中载具与实际排队不符！",TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
                    }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        } finally {
        }
        return ResultGenerator.genSuccessResult("OK");
    }

    /**
     * WCS调用此接口叫空载具
     *
     * @return
     */
    /*@GetMapping("/EmptyShelfInReq")
    @ResponseBody
    @Transactional*/
    private void EmptyShelfInReq() {
        TaskTypeConstant.GET_EMPTY_BOX_STATE = 2;
        CallMesGetCarrier callMesGetCarrier = new CallMesGetCarrier();
        callMesGetCarrier.start();
    }

    class CallMesGetCarrier extends Thread {
        @Override
        public void run() {
            synchronized (this) {
                WebserviceResponse webserviceResponse = null;
                CallAgv callAgv = new CallAgv();
                List<Door> lists = doorService.selectDoorList(new Door(null, null, null, 1, null, null));
                String code = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<EmptyShelfInReq\n" +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
                        "macCode=\"" + lists.get(0).getCode() + "\" \n" +
                        "berthCode=\"" + lists.get(0).getAddressCode() + "\" \n" +
                        "taskCode=\"" + new GuidUtils().toString() + "\">\n" +
                        "</EmptyShelfInReq >";
                callAgv.setMethodName("EmptyShelfInReq");
                callAgv.setCode(code);
                WarnInformation warnInformation = new WarnInformation();
                try {
                    while (!isInterrupted()) {
                        if (TaskTypeConstant.AUTO_EXECUTE.equals(0)) {
                            TaskTypeConstant.GET_EMPTY_BOX_STATE = 1;
                            interrupt();
                            System.out.println(Thread.currentThread().getName() + this.getState());
                        }
                        webserviceResponse = serverVisitAddressService.requestMesServer("EmptyShelfInReq", code);
                        callAgv.setId(null);
                        callAgv.setTaskCode(webserviceResponse.getTaskCode() == null ? null : webserviceResponse.getTaskCode());
                        callAgv.setErrorCode(webserviceResponse.getErrorCode());
                        callAgv.setErrorMsg("WMS请求MES取空载具接口。" + webserviceResponse.getErrorMsg());
                        callAgv.setCreateTime(DateUtils.getTime());
                        callAgvService.save(callAgv);
                        if (webserviceResponse.getErrorMsg().equals("OK") && webserviceResponse.getErrorCode().equals("0")) {
                            backState = 1;
                            TaskTypeConstant.GET_EMPTY_BOX_STATE = 3;
                            interrupt();
                            System.out.println(Thread.currentThread().getName() + this.getState());
                        } else {
                            warnInformation.setWarnId(null);
                            warnInformation.setType(TaskTypeConstant.CALL_AGV_ERROR);
                            warnInformation.setState(TaskTypeConstant.UNDEALT);
                            warnInformation.setMemo("呼叫AGV取空载具失败：" + webserviceResponse.getErrorMsg());
                            warnInformation.setCreateTime(DateUtils.getTime());
                            warnInformationService.save(warnInformation);
                            Thread.sleep(10 * 60 * 1000);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " (" + this.getState() + ") catch InterruptedException.");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }
    }

    /**
     * 立体库拿取空载具成功
     *
     * @return
     */
    /*@GetMapping("/StockTakeShelf")
    @ResponseBody*/
    public void StockTakeShelf() {
        backState = 2;
        CallMesGetEmptySuccessTread callMesGetEmptySuccessTread = new CallMesGetEmptySuccessTread();
        callMesGetEmptySuccessTread.start();
    }

    class CallMesGetEmptySuccessTread extends Thread {
        @Override
        public void run() {
            synchronized (this) {
                WebserviceResponse webserviceResponse = null;
                CallAgv callAgv = new CallAgv();
                List<Door> lists = doorService.selectDoorList(new Door(null, null, null, 1, null, null));
                String code = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<StockTakeShelf\n" +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
                        "macCode=\"" + lists.get(0).getCode() + "\" \n" +
                        "berthCode=\"" + lists.get(0).getAddressCode() + "\" \n" +
                        "taskCode=\"" + new GuidUtils().toString() + "\">\n" +
                        "</StockTakeShelf >";
                callAgv.setMethodName("StockTakeShelf");
                callAgv.setCode(code);
                try {
                    while (!isInterrupted()) {
                        if (TaskTypeConstant.GET_EMPTY_BOX_STATE == 4) {
                            webserviceResponse = serverVisitAddressService.requestMesServer("StockTakeShelf", code);
                            callAgv.setId(null);
                            callAgv.setTaskCode(webserviceResponse.getTaskCode() == null ? null : webserviceResponse.getTaskCode());
                            callAgv.setErrorCode(webserviceResponse.getErrorCode());
                            callAgv.setErrorMsg("WMS通知MES取空载具成功。" + webserviceResponse.getErrorMsg());
                            callAgv.setCreateTime(DateUtils.getTime());
                            callAgvService.save(callAgv);
                            if (webserviceResponse.getErrorMsg().equals("OK") && webserviceResponse.getErrorCode().equals("0")) {
                                TaskTypeConstant.GET_EMPTY_BOX_STATE = 1;
                                interrupt();
                                System.out.println(Thread.currentThread().getName() + this.getState());
                            } else {
                                Thread.sleep(30 * 1000);
                            }
                        } else {
                            Thread.sleep(10 * 1000);
                        }
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " (" + this.getState() + ") catch InterruptedException.");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }
    }

    /**
     * 手动呼叫agv
     */
    @GetMapping("/manualCallAgv")
    @ResponseBody
    @Transactional
    public Result manualCallAgv() {
        if(TaskTypeConstant.GET_EMPTY_BOX_STATE != 3){
            return ResultGenerator.genFailResult(CommonCode.SERVER_INERNAL_ERROR,TaskTypeConstant.GET_EMPTY_BOX_STATE+"自动呼叫中，请勿手动呼叫","");
        }
        WebserviceResponse webserviceResponse = null;
        CallAgv callAgv = new CallAgv();
        try {
            TaskTypeConstant.GET_EMPTY_BOX_STATE = 2;
            List<Door> lists = doorService.selectDoorList(new Door(null, null, null, 1, null, null));
            String code = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<EmptyShelfInReq\n" +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
                    "macCode=\"" + lists.get(0).getCode() + "\" \n" +
                    "berthCode=\"" + lists.get(0).getAddressCode() + "\" \n" +
                    "taskCode=\"" + new GuidUtils().toString() + "\">\n" +
                    "</EmptyShelfInReq >";
            webserviceResponse = serverVisitAddressService.requestMesServer("EmptyShelfInReq", code);
            if (webserviceResponse.getErrorMsg().equals("OK") && webserviceResponse.getErrorCode().equals("0")) {
                TaskTypeConstant.GET_EMPTY_BOX_STATE = 3;
                return ResultGenerator.genSuccessResult();
            } else {
                TaskTypeConstant.GET_EMPTY_BOX_STATE = 1;
            }
            callAgv.setCode(code);
        } catch (Exception e) {
            TaskTypeConstant.GET_EMPTY_BOX_STATE = 1;
            e.printStackTrace();
            webserviceResponse = new WebserviceResponse(null, "-1", "调用MES接口出错", null);
        } finally {
            callAgv.setMethodName("EmptyShelfInReq");
            callAgv.setTaskCode(webserviceResponse.getTaskCode() == null ? null : webserviceResponse.getTaskCode());
            callAgv.setErrorMsg("WMS请求MES取空载具接口。" + webserviceResponse.getErrorMsg());
            callAgv.setErrorCode(webserviceResponse.getErrorCode());
            callAgv.setCreateTime(DateUtils.getTime());
            callAgvService.save(callAgv);
        }
        return ResultGenerator.genFailResult(CommonCode.MANUAL_CALL_AGV_FAIL, webserviceResponse.getErrorMsg(), null);
    }

    /**
     * 勾选出指定箱（报废退货）
     */
    @PostMapping("/returnItem")
    @ResponseBody
    @Transactional
    public Result returnItem(@RequestBody BoxItemCriteria boxItemCriteria) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if (taskInfoService.judgeWhetherCheckTaskInfo()) {
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            boxItemCriteria.setOrderByState(1003);
            List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteria);
            if (boxItemDtos.size() <= 0 || boxItemDtos.get(0).getLockQuantity() > 0) {
                error = "当前箱有预测锁定库存，请解锁预测锁定库存或选择其他箱！";
                throw new RuntimeException();
            }
            String bool = cellInfoService.judgeBoxItemState(boxItemDtos);
            if (!bool.equals("success")) {
                error = bool;
                throw new RuntimeException();
            }
            cellInfoService.updateCellStateAndBoxStateAndSendTaskInfo(boxItemDtos.get(0), null, boxItemCriteria.getLoginPersonCardNo());
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE, error);
        }

    }

    /**
     * 确认退货
     */
    @PostMapping("/ensureReturnItem")
    @ResponseBody
    public Result ensureReturnItem(@RequestBody BoxItemCriteria boxItemCriteria) {
        if (taskInfoService.judgeWhetherCheckTaskInfo()) {
            return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "盘点中，请勿下发其他任务!");
        }
        Operator operator = operatorService.findByCard(boxItemCriteria.getLoginPersonCardNo());
        BoxItemDto boxItemDto = boxItemService.findList(boxItemCriteria).get(0);
        BillOutMaster billOutMaster = new BillOutMaster(null, MyUtils.getOutWarehouseNo(), null, DateUtils.getTime(), operator.getOperatorName(), operator.getOperatorId(),
                2, "退货出库", TaskTypeConstant.wareInfo.getWareId(), TaskTypeConstant.QUIT_WAREHOUSE_OUT);
        billOutMasterService.save(billOutMaster);
        BillOutDetail billOutDetail = new BillOutDetail(null, billOutMaster.getBillId(), boxItemDto.getItemCode(),
                boxItemDto.getQuantity(), null, null, null);
        billOutDetail.setAlreadyOutQuantity(boxItemDto.getQuantity());
        billOutDetailService.save(billOutDetail);
        //写入修改出库记录
        PickTask pickTask = new PickTask(boxItemDto.getBoxCode(), boxItemDto.getQuantity(), billOutDetail.getBillOutDetailId(),
                4, boxItemDto.getBatch(), boxItemDto.getSubInventoryId(), DateUtils.getTime(), DateUtils.getTime());
        pickTask.setPickType(billOutMaster.getType());
        pickTaskService.save(pickTask);
        String msg = cellInfoService.inNullBox(boxItemDto.getBoxCode(), boxItemCriteria.getLoginPersonCardNo(), null);
        if (!msg.equals("success")) {
            return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "无可用货位");
        }
        //盘点

        //EBS订单退货接口

        return ResultGenerator.genSuccessResult();
    }

    /**
     * 报废完成
     */
    @PostMapping("/scrapInBox")
    @ResponseBody
    @Transactional
    public Result scrapInBox(@RequestBody BoxItemCriteria boxItemCriteria) {
        try {
            if (taskInfoService.judgeWhetherCheckTaskInfo()) {
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "盘点中，请勿下发其他任务!");
            }
            BoxItemDto boxItemDto = boxItemService.findList(new BoxItemCriteria(boxItemCriteria.getBoxCode())).get(0);
            //修改单据状态及出库数量
            Operator operator = operatorService.findByCard(boxItemCriteria.getLoginPersonCardNo());
            BillOutMaster billOutMaster = new BillOutMaster(MyUtils.getOutWarehouseNo(), DateUtils.getTime(),
                    operator.getOperatorName(), operator.getOperatorId(), 2, "报废出库", TaskTypeConstant.wareInfo.getWareId(),
                    TaskTypeConstant.SCRAP_OUT, null, null);
            billOutMasterService.save(billOutMaster);
            BillOutDetail billOutDetail = new BillOutDetail(billOutMaster.getBillId(), boxItemDto.getItemCode(), boxItemDto.getQuantity());
            billOutDetail.setAlreadyOutQuantity(boxItemDto.getQuantity());
            billOutDetailService.save(billOutDetail);
            //写入记录单据
            PickTask pickTask = new PickTask(boxItemDto.getBoxCode(), boxItemDto.getQuantity(), billOutDetail.getBillOutDetailId(),
                    4, boxItemDto.getBatch(), boxItemDto.getSubInventoryId(), DateUtils.getTime(),
                    DateUtils.getTime(), TaskTypeConstant.SCRAP_OUT);
            pickTaskService.save(pickTask);
            //写入转库记录
            SubinventoryTransferRecord subinventoryTransferRecord = new SubinventoryTransferRecord(
                    boxItemDto.getBoxCode(), boxItemDto.getItemCode(), boxItemDto.getBatch(), boxItemDto.getQuantity(),
                    DateUtils.getTime(), boxItemCriteria.getLoginPersonCardNo(), boxItemDto.getSubInventoryId(), TaskTypeConstant.SCRAP, "报废原因:人工报废"
            );
            subinventoryTransferRecord.setToExp(boxItemDto.getExp());
            subinventoryTransferRecord.setForExp(boxItemDto.getExp());
            subinventoryTransferRecordService.save(subinventoryTransferRecord);

            List<Map<String, String>> lists = new ArrayList<>();
            SubInventory subInventory = subInventoryService.findById(TaskTypeConstant.SCRAP);
            RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS转库数据写入EBS接口");
            List<RequestId> requestIds = new ArrayList<>();
            //写入EBS交互参数
            lists.add(MyUtils.subInventoryTransfer(TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, TaskTypeConstant.organizationId.toString(),
                    boxItemDto.getInventoryItemId().toString(), boxItemDto.getQuantity().toString(), boxItemDto.getSubInventoryCode(),
                    boxItemDto.getSlotting() == null ? null : boxItemDto.getSlotting(), MyUtils.getNinetySecondsAgo(), boxItemDto.getUnit(),
                    subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : subInventory.getSlotting(),
                    boxItemDto.getBatch(), billOutMaster.getBillId().toString(), billOutDetail.getBillOutDetailId().toString()));
            RequestId requestId = new RequestId(requestIdAuto.getRequestId(), boxItemDto.getInventoryItemId(), boxItemDto.getQuantity(),
                    boxItemDto.getBatch(), DateUtils.getTime(), "WMS调用EBS子库转移接口失败", boxItemDto.getSubInventoryCode(),
                    boxItemDto.getSlotting() == null ? null : Integer.parseInt(boxItemDto.getSlotting()), TaskTypeConstant.organizationId, TaskTypeConstant.TRANSFER,
                    TaskTypeConstant.FAIL_WAIT_MANAGE, TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, boxItemDto.getUnit(),
                    subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : Integer.parseInt(subInventory.getSlotting()),
                    billOutMaster.getBillId(), billOutDetail.getBillOutDetailId(), "ERROR");
            requestIds.add(requestId);
            //EBS物料报废（子库转移）接口
            requestIdService.subInventoryTransfer(requestIdAuto, requestIds, lists);
            //下发出库任务
            cellInfoService.inNullBox(boxItemCriteria.getBoxCode(), boxItemCriteria.getLoginPersonCardNo(), null);
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }

    /**
     * IQC工单扣减
     */
    @PostMapping("/IQCOut")
    @ResponseBody
    @Transactional
    public Result IQCOut(@RequestBody BoxItemCriteria boxItemCriteria) {
        String error = "服务器内部错误，请联系管理员";
        try {
            BoxItem boxItem = boxItemService.getBoxItemByBoxCode(boxItemCriteria.getBoxCode());
            if (!boxItem.getSubInventoryId().equals(TaskTypeConstant.DESIRED)) {
                error = "当前物料已检验，请选择待检箱";
                throw new RuntimeException();
            }
            if (!boxItemCriteria.getType().equals(1)) {
                error = "请选择类型为IQC扣减数据";
            }
            Operator operator = operatorService.findByCard(boxItemCriteria.getLoginPersonCardNo());
            BillOutMaster billOutMaster = new BillOutMaster(MyUtils.getOutWarehouseNo(), DateUtils.getTime(),
                    operator.getOperatorName(), operator.getOperatorId(), 1, 212);
            billOutMaster.setType(TaskTypeConstant.IQC_OUT);
            billOutMaster.setMemo("IQC入库前检验出库");
            billOutMaster.setAccountAliasId(boxItemCriteria.getDispositionId());
            billOutMasterService.save(billOutMaster);
            BillOutDetail billOutDetail = new BillOutDetail(billOutMaster.getBillId(), boxItem.getItemCode(), boxItemCriteria.getQuantity());
            billOutDetail.setQuantity(boxItemCriteria.getQuantity());
            billOutDetailService.save(billOutDetail);
            PickTask pickTask = new PickTask(boxItem.getBoxCode(), boxItemCriteria.getQuantity(), billOutDetail.getBillOutDetailId(), 1,
                    boxItem.getBatch(), boxItem.getSubInventoryId(), DateUtils.getTime(), null, TaskTypeConstant.IQC_OUT);
            pickTaskService.save(pickTask);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE, error);
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 非工单出库
     */
    @PostMapping("/nonWorkerOrderItemOutBox")
    @ResponseBody
    @Transactional
    public Result nonWorkerOrderItemOutBox(@RequestBody BoxItemCriteria boxItemCriteria) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if (taskInfoService.judgeWhetherCheckTaskInfo()) {
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteria);
            BoxItemDto boxItemDto = boxItemDtos.get(0);
            String bool = cellInfoService.judgeBoxItemState(boxItemDtos);
            if (!bool.equals("success")) {
                error = bool;
                throw new RuntimeException();
            }
            /*if (boxItemDto.getSubInventoryId().equals(TaskTypeConstant.POSTPONE) ||
                    boxItemDto.getSubInventoryId().equals(TaskTypeConstant.QUALIFIED)) {
                error = "当前箱不符合出库条件，请选择延期或者合格库进行出库！";
                throw new RuntimeException();
            }*/
            if (boxItemCriteria.getType().equals(2)
                    && boxItemCriteria.getTransactionType().equals(TaskTypeConstant.TRANSACTION_IN)) {
                if ((boxItemCriteria.getQuantity() + boxItemDto.getQuantity()) > boxItemDto.getMaxPackQty()) {
                    error = "超过单箱最大存储数量！剩余可入数量为" + (boxItemDto.getMaxPackQty() - boxItemDto.getQuantity());
                    throw new RuntimeException();
                }
            } else {
                /*if (boxItemDto.getSubInventoryId().equals(TaskTypeConstant.POSTPONE) &&
                        TaskTypeConstant.workerOrderIssueTime.getDelayControl().equals(2)) {
                    error = "当前箱延期管控中，请重新选择！";
                    throw new RuntimeException();
                }*/
                Integer canOutQuantity = boxItemDto.getQuantity() - boxItemDto.getForecastStockQuantity() - boxItemDto.getLockQuantity() - boxItemCriteria.getQuantity();
                if (canOutQuantity < 0) {
                    error = "当前箱可出数量不足，可出数量为" + (boxItemDto.getQuantity() - boxItemDto.getForecastStockQuantity() - boxItemDto.getLockQuantity());
                    throw new RuntimeException();
                }
            }
            cellInfoService.updateCellStateAndBoxStateAndSendTaskInfo(boxItemDto, null, boxItemCriteria.getLoginPersonCardNo());
            return ResultGenerator.genSuccessResult(boxItemDto.getQuantity() - boxItemDto.getForecastStockQuantity() - boxItemDto.getLockQuantity());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE, error);
        }
    }

    /**
     * 非工单出库
     */
    @PostMapping("/nonWorkerOrderOutFinish")
    @ResponseBody
    @Transactional
    public Result nonWorkerOrderOutFinish(@RequestBody BoxItemCriteria boxItemCriteria) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if (taskInfoService.judgeWhetherCheckTaskInfo()) {
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            Operator operator = operatorService.findByCard(boxItemCriteria.getLoginPersonCardNo());
            BoxItemDto boxItemDto = boxItemService.findList(boxItemCriteria).get(0);
            if (boxItemCriteria.getType().equals(2) && boxItemCriteria.getTransactionType().equals(TaskTypeConstant.TRANSACTION_IN)) {
                if ((boxItemCriteria.getQuantity() + boxItemDto.getQuantity()) > boxItemDto.getMaxPackQty()) {
                    error = "超过单箱最大存储数量！剩余可入数量为" + (boxItemDto.getMaxPackQty() - boxItemDto.getQuantity());
                    throw new RuntimeException();
                }
            } else {
                if (boxItemCriteria.getQuantity() > boxItemDto.getQuantity() - boxItemDto.getLockQuantity() - boxItemDto.getForecastStockQuantity()) {
                    error = "超过可出数量，当前可出数量为" + (boxItemDto.getQuantity() - boxItemDto.getLockQuantity() - boxItemDto.getForecastStockQuantity()) + "张";
                    throw new RuntimeException();
                }
            }
            BillOutMaster billOutMaster = new BillOutMaster(null, DateUtils.getTime(),
                    operator.getOperatorName(), operator.getOperatorId(), 2, 212);

            BillOutDetail billOutDetail = new BillOutDetail(null, boxItemDto.getItemCode(), boxItemCriteria.getQuantity());
            billOutDetail.setAlreadyOutQuantity(boxItemCriteria.getQuantity());

            PickTask pickTask = new PickTask(boxItemDto.getBoxCode(), boxItemCriteria.getQuantity(), null,
                    4, boxItemDto.getBatch(), boxItemDto.getSubInventoryId(), DateUtils.getTime(), DateUtils.getTime(), null);

            //组织间转移  非工单出库
            if (boxItemCriteria.getType().equals(3)) {
                billOutMaster.setBillNo(MyUtils.getOutWarehouseNo());
                billOutMaster.setType(TaskTypeConstant.ORGANIZATION_TRANSFER);
                billOutMaster.setMemo("非工单出库");
                pickTask.setPickType(TaskTypeConstant.ORGANIZATION_TRANSFER);
                boxItemDto.setQuantity(boxItemDto.getQuantity() - boxItemCriteria.getQuantity());
                billOutMasterService.save(billOutMaster);
                billOutDetail.setBillId(billOutMaster.getBillId());
                billOutDetailService.save(billOutDetail);
            }
            //物料转卖出库
            else if (boxItemCriteria.getType().equals(4)) {
                billOutMaster.setBillNo(MyUtils.getOutWarehouseNo());
                billOutMaster.setType(TaskTypeConstant.ITEM_RESALE);
                billOutMaster.setMemo("物料转卖");
                pickTask.setPickType(TaskTypeConstant.ITEM_RESALE);
                boxItemDto.setQuantity(boxItemDto.getQuantity() - boxItemCriteria.getQuantity());
                billOutMasterService.save(billOutMaster);
                billOutDetail.setBillId(billOutMaster.getBillId());
                billOutDetailService.save(billOutDetail);
            }
            //账户别名
            else if (boxItemCriteria.getType().equals(2)) {
                List<Map<String, String>> accountAliasParams = new ArrayList<>();
                List<RequestId> requestIdsAccountAliasOut = new ArrayList<>();
                //账户别名发放
                if (boxItemCriteria.getTransactionType().equals(TaskTypeConstant.TRANSACTION_OUT)) {
                    billOutMaster.setBillNo(MyUtils.getOutWarehouseNo());
                    billOutMaster.setType(TaskTypeConstant.NO_WORKER_ORDER_OUT);
                    billOutMaster.setMemo("账户别名发放");
                    pickTask.setPickType(TaskTypeConstant.NO_WORKER_ORDER_OUT);
                    boxItemDto.setQuantity(boxItemDto.getQuantity() - boxItemCriteria.getQuantity());
                }
                //账户别名接收
                else if (boxItemCriteria.getTransactionType().equals(TaskTypeConstant.TRANSACTION_IN)) {
                    billOutMaster.setBillNo(MyUtils.getInWarehouseNo());
                    billOutMaster.setType(TaskTypeConstant.NO_WORKER_ORDER_IN);
                    billOutMaster.setMemo("账户别名接收");
                    pickTask.setPickType(TaskTypeConstant.NO_WORKER_ORDER_IN);
                    boxItemDto.setQuantity(boxItemDto.getQuantity() + boxItemCriteria.getQuantity());
                }
                billOutMaster.setAccountAliasId(boxItemCriteria.getDispositionId());
                billOutMasterService.save(billOutMaster);
                billOutDetail.setBillId(billOutMaster.getBillId());
                billOutDetailService.save(billOutDetail);
                AccountAlias accountAlias = accountAliasService.findByDispositionId(boxItemCriteria.getDispositionId());
                RequestIdAuto accountAliasRequestIdAuto = requestIdAutoService.backAutoId("WMS账户别名出库");
                accountAliasParams.add(MyUtils.accountAliasOut(boxItemCriteria.getTransactionType(),
                        TaskTypeConstant.organizationId.toString(),
                        boxItemDto.getInventoryItemId().toString(), boxItemDto.getSubInventoryCode(),
                        boxItemDto.getSlotting() == null ? null : boxItemDto.getSlotting(), accountAlias.getAccountAlias(),
                        accountAlias.getDispositionId().toString(), boxItemDto.getBatch(),
                        boxItemCriteria.getTransactionType().equals(TaskTypeConstant.TRANSACTION_OUT) ? ((pickTask.getPickQuantity() * (-1)) + "") : (pickTask.getPickQuantity() + ""),
                        MyUtils.getNinetySecondsAgo(), boxItemDto.getUnit(), billOutMaster.getBillId().toString(), billOutDetail.getBillOutDetailId().toString()));

                requestIdsAccountAliasOut.add(new RequestId(accountAliasRequestIdAuto.getRequestId(), "ERROR", boxItemDto.getInventoryItemId(),
                        boxItemCriteria.getTransactionType().equals(TaskTypeConstant.TRANSACTION_OUT) ? (pickTask.getPickQuantity() * (-1)) : pickTask.getPickQuantity(),
                        boxItemDto.getBatch(), MyUtils.getNinetySecondsAgo(), "WMS请求EBS账户别名事务处理失败", boxItemDto.getSubInventoryCode(),
                        boxItemDto.getSlotting() == null ? null : Integer.parseInt(boxItemDto.getSlotting()), TaskTypeConstant.organizationId, TaskTypeConstant.ACCOUNT_ALIAS,
                        TaskTypeConstant.FAIL_WAIT_MANAGE, boxItemCriteria.getTransactionType(),
                        boxItemDto.getUnit(), billOutMaster.getBillId(), billOutDetail.getBillOutDetailId(),
                        accountAlias.getAccountAlias(), accountAlias.getDispositionId()));

                requestIdService.accountAliasOut(accountAliasParams, requestIdsAccountAliasOut, accountAliasRequestIdAuto);

            }
            boxItemService.update(boxItemDto);
            pickTask.setBillOutDetailId(billOutDetail.getBillOutDetailId());
            pickTaskService.save(pickTask);

            if (boxItemDto.getQuantity() <= 0) {
                cellInfoService.inNullBox(boxItemDto.getBoxCode(), boxItemCriteria.getLoginPersonCardNo(), null);
            } else {
                cellInfoService.inAvailableBox(boxItemDto, boxItemCriteria.getLoginPersonCardNo(), billOutDetail.getBillOutDetailId(), boxItemDto.getItemName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE, error);
        }
        return ResultGenerator.genSuccessResult();
    }
}
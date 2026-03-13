package com.deer.wms.base.system.web.bill;

import com.alibaba.fastjson.JSONArray;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.bill.*;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.task.TaskInfoCriteria;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.model.ware.Supplier;
import com.deer.wms.base.system.model.ware.WareInfo;
import com.deer.wms.base.system.service.*;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.mailServer.MailService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.base.system.service.ware.IShelfInfoService;
import com.deer.wms.base.system.service.ware.ISupplierService;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.core.controller.BaseController;
import com.deer.wms.common.core.domain.AjaxResult;
import com.deer.wms.common.core.page.TableDataInfo;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.core.result.Result;
import com.deer.wms.common.core.result.ResultGenerator;
import com.deer.wms.common.enums.BusinessType;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.GuidUtils;
import com.deer.wms.common.utils.poi.ExcelUtil;
import com.deer.wms.framework.util.MyUtils;
import com.deer.wms.framework.util.ShiroUtils;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.service.bill.IBillInDetailService;
import com.deer.wms.base.system.service.bill.IBillInMasterService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入库单 信息操作处理
 *
 * @author guo
 * @date 2019-05-13
 */
@Controller
@RequestMapping("/in/billInMaster")
public class BillInMasterController extends BaseController {
    private String prefix = "in/billInMaster";

    @Autowired
    private IBillInMasterService billInMasterService;

    @Autowired
    private IBillInDetailService billInDetailService;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private IItemInfoService itemInfoService;
    @Autowired
    private ISupplierService supplierService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private BillInRecordService billInRecordService;
    @Autowired
    private OrderInformationService orderInformationService;
    @Autowired
    private SubInventoryService subInventoryService;
    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;
    @Autowired
    private MailService mailService;

    /**
     * 查询物料详情数据(点击物料详情)
     */
    @RequestMapping("/showBillInDetail")
    @ResponseBody
    public List<BillInDetail> selectBillInDetail() {
        List<BillInDetail> list = billInMasterService.selectBillInDetail();
        for (BillInDetail billInDetail : list) {
//            System.out.println(billInDetail.toString());
        }
        return list;
    }

    /**
     * 查询所有组盘(点击组盘信息)
     */
    @RequestMapping("/showBox")
    @ResponseBody
    public TableDataInfo selectBoxItem(Integer billId) {
        List<BoxItem> list = billInMasterService.selectBoxItem(billId);
        return getDataTable(list);
    }

    /**
     * 保存组盘
     */
    @RequestMapping("/saveBox")
    @ResponseBody
    public Result saveBox(BoxItem boxItem) {
        billInMasterService.saveBox(boxItem);
        return ResultGenerator.genSuccessResult();
    }

    @RequiresPermissions("in:billInMaster:view")
    @GetMapping("/page")
    public String billInMaster() {
        return prefix + "/billInMaster";
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public String detail() {
        return prefix + "/detail";
    }

    /**
     * 新增入库单
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 查询入库单列表
     */
    @RequiresPermissions("in:billInMaster:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BillInMaster billInMaster) {
        startPage();
        List<BillInMaster> list = billInMasterService.selectBillInMasterList(billInMaster);
        return getDataTable(list);
    }

    /**
     * 查询入库单列表
     */
//    @RequiresPermissions("in:billInMaster:findList")
    @PostMapping("/findList")
    @ResponseBody
    @Transactional
    public TableDataInfo findList(BillInMasterCriteria billInMaster) {
        startPage();
        List<BillInMasterDto> list = billInMasterService.findList(billInMaster);
        return getDataTable(list);
    }

    /**
     * 导出入库单列表
     */
    @RequiresPermissions("in:billInMaster:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BillInMaster billInMaster) {
        List<BillInMaster> list = billInMasterService.selectBillInMasterList(billInMaster);
        ExcelUtil<BillInMaster> util = new ExcelUtil<BillInMaster>(BillInMaster.class);
        return util.exportExcel(list, "billInMaster");
    }

    /**
     * 新增保存入库单
     */
    @RequiresPermissions("in:billInMaster:add")
    @Log(title = "入库单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public Result addSave(BillInMaster billInMaster) {
        billInMaster.setCreateUserName(ShiroUtils.getLoginName());
        billInMaster.setCreateUserId(ShiroUtils.getUserId());
        billInMasterService.save(billInMaster);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 修改入库单
     */
    @GetMapping("/edit/{billId}")
    public String edit(@PathVariable("billId") Integer billId, ModelMap mmap) {
        BillInMaster billInMaster = billInMasterService.selectBillInMasterById(billId);
        mmap.put("billInMaster", billInMaster);
        return prefix + "/edit";
    }

    /**
     * 修改保存入库单
     */
    @RequiresPermissions("in:billInMaster:edit")
    @Log(title = "入库单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BillInMaster billInMaster) {
        return toAjax(billInMasterService.updateBillInMaster(billInMaster));
    }

    /**
     * 删除入库单
     */
    @RequiresPermissions("in:billInMaster:remove")
    @Log(title = "入库单", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(billInMasterService.deleteBillInMasterByIds(ids));
    }

    /**
     * 保存入库信息
     */
    @RequiresPermissions("in:billInMaster:add")
    @Log(title = "入库单", businessType = BusinessType.INSERT)
    @PostMapping("/insert")
    @ResponseBody
    @Transactional
    public Result insert(@RequestBody InserData inserData) {
        String createUserName = ShiroUtils.getLoginName();
        Integer userId = ShiroUtils.getUserId();
        BillInMaster billInMaster = inserData.getBillInMaster();
        billInMaster.setCreateUserName(createUserName);
        billInMaster.setCreateUserId(userId);
        billInMasterService.save(billInMaster);
        List<BillInDetail> billInDetails = inserData.getBillInDetailList();
        for (BillInDetail billInDetail : billInDetails) {
            billInDetail.setBillId(billInMaster.getBillId());
//            billInDetailService.insertBillInDetail(billInDetail);
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 扫码入库验证
     * I am not responsible of this code
     * They made me write it, against my will
     */
    @PostMapping("/billInValidation")
    @ResponseBody
    @Transactional
    public Result billInValidation(@RequestBody BillInMasterCriteria criteria) {
        String error = "服务器内部错误，请联系管理员";
        ItemInfo itemInfo1 = new ItemInfo();
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            if(criteria.getBoxCode() == null || criteria.getBoxCode().equals("")){
                error = "请重新扫描箱号！";
                throw new RuntimeException();
            }
            if(criteria.getProductionDate().length()<6 || criteria.getProductionDate().length()>6){
                error = "二维码中生产日期格式不正确！";
                throw new RuntimeException();
            }
            if(criteria.getProductionDate().length()<6 || criteria.getProductionDate().length()>6){
                error = "二维码中失效日期格式不正确！";
                throw new RuntimeException();
            }
//            SubInventory subInventory = subInventoryService.findById(1);
            ItemInfo itemInfo = itemInfoService.findByItemCode(criteria.getItemCode());
            if (itemInfo == null) {
                EbsBack entityStr = serverVisitAddressService.requestItemId(criteria.getItemCode(), TaskTypeConstant.organizationId);
//			判断是否请求到参数
                if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
                    JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
                    for (int i = 0; i < jsonArrays.size(); i++) {
                        com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                        Integer inventoryItemId = jsonObject.get("inventoryItemId") != null ? Integer.parseInt(jsonObject.get("inventoryItemId").toString().trim()) : null;
                        String itemCode = jsonObject.get("itemCode") != null ? jsonObject.get("itemCode").toString() : null;
                        String primaryUnitOfMeasure = jsonObject.get("primaryUnitOfMeasure").toString().trim();
                        String itemDesc = jsonObject.get("itemDesc").toString().trim();
                        itemInfo = new ItemInfo(itemCode, itemDesc, primaryUnitOfMeasure, inventoryItemId);
                        itemInfoService.save(itemInfo);
                    }
                } else {
                    error = "同步EBS物料信息失败失败";
                    throw new RuntimeException();
                }
            }
            if(itemInfo.getMaxPackQty() == null){
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"请维护此物料厚度!");
            }
            //判断是否超过单箱存储最大张数
            BoxItem boxItem = boxItemService.getBoxItemByBoxCode(criteria.getBoxCode());
            if(boxItem != null){
                if((boxItem.getQuantity()+(criteria.getQuantitys()==null?0:criteria.getQuantitys())+criteria.getQuantity())>itemInfo.getMaxPackQty()){
                    error="超过单箱最大存储张数";
                    throw new RuntimeException();
                }
            }
            Operator operator = operatorService.findByCard(criteria.getOperatorCard());
            Integer canReceiveQuantity = 0;
            //根据物料Id查询订单
            BillInDetail billInDetailCriteria = new BillInDetail();
            billInDetailCriteria.setSegment(criteria.getBillNo());
            billInDetailCriteria.setItemId(itemInfo.getInventoryItemId());
            List<BillInDetail> billInDetailDtos = billInDetailService.selectBillInDetailList(billInDetailCriteria);
            //初始化入库
            if(criteria.getAutoverifyPermission() == 2){
                if (billInDetailDtos.size() > 0) {
                    saveBillInRecord(billInDetailDtos.get(0).getBillInDetailId(),criteria.getQuantity(),criteria.getBoxCode(),criteria.getBarCode(),
                            criteria.getProductionDate(),criteria.getEndDate(),criteria.getBatch(),3,criteria.getOperatorCard());
                }
                else {
                    BillInMaster billInMaster = new BillInMaster(criteria.getBillNo(),operator.getOperatorId(),
                            operator.getOperatorName(),2,"初始化入库",DateUtils.getTime(),criteria.getSupplierCode(),TaskTypeConstant.wareInfo.getWareId(),2);
                    billInMasterService.save(billInMaster);
                    BillInDetail billInDetail = new BillInDetail();
                    billInDetail.setBillId(billInMaster.getBillId());
                    billInDetail.setItemId(itemInfo.getInventoryItemId());
                    billInDetail.setSegment(criteria.getBillNo());
                    billInDetail.setItemDescription(itemInfo.getItemName());
                    billInDetail.setUnitMeasLookupCode(itemInfo.getUnit());
                    billInDetail.setExpectedArrivalDate(DateUtils.getTime());
                    billInDetail.setVendorCode(criteria.getSupplierCode());
                    billInDetailService.save(billInDetail);
                    saveBillInRecord(billInDetail.getBillInDetailId(),criteria.getQuantity(),criteria.getBoxCode(),criteria.getBarCode(),
                            criteria.getProductionDate(),criteria.getEndDate(),criteria.getBatch(),3,criteria.getOperatorCard());
                }
            }
            //正常入库
            else {
                //触发上一包接收（修改为点击入库时触发）
                /*if (!criteria.getLastBarCode().equals("")) {
                    String msg = returnEBSReceived(criteria.getLastBarCode());
                    if (!msg.equals("success")) {
                        return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"EBS采购接收失败!"+msg);
                    }
                }*/
                Integer oneBagQuantity = criteria.getQuantity();

                if (billInDetailDtos.size() > 0) {
                    Bloop:
                    for (int i = 0; i < billInDetailDtos.size(); i++) {
                        BillInDetail billInDetail = billInDetailDtos.get(i);
                        canReceiveQuantity = billInDetail.getSurplusReceivedQuantity() - oneBagQuantity;
                        oneBagQuantity = returnOneBagQuantity(billInDetail, oneBagQuantity, criteria.getBoxCode(), criteria.getBarCode(),
                                criteria.getProductionDate(), criteria.getEndDate(), criteria.getBatch(),operator.getOperatorCard());
                        if (oneBagQuantity <= 0) {
                            break Bloop;
                        }
                        /*else {
                            canReceiveQuantity += billInDetail.getSurplusReceivedQuantity();
                        }*/
                        if (oneBagQuantity > 0 && (billInDetailDtos.size() - 1) == i) {
                            error = "超过订单数量";
                            throw new RuntimeException();
                        }
                    }
                }
                else {
                    //对接EBS获取订单数据
                    EbsBack entityStr = getEBSOrderInformation(criteria.getBillNo(), itemInfo.getInventoryItemId());
//        			判断是否请求到参数
                    if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
                        JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
                        BillInMaster billInMaster = new BillInMaster(criteria.getBillNo(), operator.getOperatorId(),
                                operator.getOperatorName(), 0, "从EBS获取的订单", DateUtils.getTime(),
                                criteria.getSupplierCode(), 212, 1);
                        billInMasterService.save(billInMaster);
                        for (int i = 0; i < jsonArrays.size(); i++) {
                            com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                            BillInDetail billInDetail = spliteEBSParams(jsonObject,billInMaster.getBillId());
                        }
                        List<BillInDetail> billInDetailDto = billInDetailService.selectBillInDetailList(billInDetailCriteria);
                        blo:
                        for (int i = 0; i < billInDetailDto.size(); i++) {
                            BillInDetail billInDetail = billInDetailDto.get(i);
                            canReceiveQuantity = billInDetail.getSurplusReceivedQuantity() - oneBagQuantity;
                            oneBagQuantity = returnOneBagQuantity(billInDetail, oneBagQuantity, criteria.getBoxCode(), criteria.getBarCode(),
                                    criteria.getProductionDate(), criteria.getEndDate(), criteria.getBatch(),operator.getOperatorCard());
                            if (oneBagQuantity <= 0) {
                                break blo;
                            }
                            if (oneBagQuantity > 0 && (billInDetailDto.size() - 1) == i) {
                                error = "超过订单数量";
                                throw new RuntimeException();
                            }
                        }
                        /*canReceiveQuantity = billInDetailDto.get(0).getSurplusReceivedQuantity()-oneBagQuantity;
                        if (oneBagQuantity > 0) {
                            oneBagQuantity = returnOneBagQuantity(billInDetailDto.get(0), oneBagQuantity, criteria.getBoxCode(), criteria.getBarCode(),
                                    criteria.getProductionDate(), criteria.getEndDate(), criteria.getBatch());
                        }*/

                    } else {
                        error = "从EBS获取订单失败!";
                        throw new RuntimeException();
                    }
                }
            }
            itemInfo1.setDateWarning(canReceiveQuantity);
            itemInfo1.setUnit("请拆包，放入物料！");
            itemInfo1.setItemName(itemInfo.getItemName());
            itemInfo1.setMaxPackQty(itemInfo.getMaxPackQty());
        }
        catch(Exception e) {
            e.printStackTrace();
            if(error.equals("")){
                throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
            }else {
                throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR, error);
            }
        }
        return ResultGenerator.genSuccessResult(itemInfo1);
    }


    /**
     * 操作台入库
     */
    @PostMapping("/billInTaskInfo")
    @ResponseBody
    @Transactional
    public Result billInTaskInfo(@RequestBody BillInMasterCriteria criteria) {
        String error = "服务器内部错误，请联系管理员";
        String success = "";
        try {
            WareInfo wareInfo = TaskTypeConstant.wareInfo;
            Integer count = cellInfoService.count();
            Integer notItemCell = cellInfoService.notItemCell();
            Integer alreadyUseQuantity = count-notItemCell+1;
            if(alreadyUseQuantity > wareInfo.getExpectedWaring()
                    && alreadyUseQuantity <= wareInfo.getAlarm()){
                mailService.sendMail("覆铜板立体库爆仓预警","当前" +
                        "库位已超过预警设置值:"+wareInfo.getExpectedWaring()+"个,总货位"+
                        count+"个，剩余"+notItemCell+"个");
            }else if(alreadyUseQuantity > wareInfo.getAlarm()){
                mailService.sendMail("覆铜板立体库爆仓报警警","当前" +
                        "库位已超过爆仓报警设置值:"+wareInfo.getExpectedWaring()+"个,总货位"+
                        count+"个，剩余"+notItemCell+"个,不允许入库");
                WebSocketServer.sendInfo("库位爆仓报警，当前已用货位数"+alreadyUseQuantity,TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
            }
            ItemInfo itemInfo = itemInfoService.findByItemCode(criteria.getItemCode());
            if(itemInfo.getMaxPackQty() != null){
                if (criteria.getQuantity() > itemInfo.getMaxPackQty()) {
                    error = "数量超过单箱最大存储数量";
                    throw new RuntimeException();
                }
            }else{
                error = "请维护此物料厚度";
                throw new RuntimeException();
            }
            if(criteria.getBoxCode() == null || criteria.getBoxCode().equals("")){
                error = "请重新扫描箱号！";
                throw new RuntimeException();
            }

            //触发上一包接收
            /*if(!criteria.getAutoverifyPermission().equals(2)) {
                String msg = returnEBSReceived(criteria.getLastBarCode());
                if (!msg.equals("success")) {
                    return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"EBS采购接收失败!"+msg);
                }
            }*/
            //触发上一包接收修改为统计已扫数量
            if(!criteria.getAutoverifyPermission().equals(2)) {
                String msg = receviceTotal(criteria.getBoxCode(), criteria.getOperatorCard());
                if (!msg.equals("success")) {
                    return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "EBS采购接收失败!" + msg);
                }
            }


            CellInfoDto cellInfoDto = cellInfoService.getBestCell();
            cellInfoService.updateCellInfoState(cellInfoDto, 2);
            BoxItem boxItem = boxItemService.getBoxItemByBoxCode(criteria.getBoxCode());
            if(boxItem.getQuantity() > 0){
                boxItem.setQuantity(criteria.getQuantity()+boxItem.getQuantity());
            }else{
                boxItem.setQuantity(criteria.getQuantity());
                boxItem.setWorkOrderStockState(0);
                boxItem.setForecastStockQuantity(0);
                boxItem.setPd(MyUtils.stringFromatDate(criteria.getProductionDate()));
                boxItem.setExp(MyUtils.stringFromatDate(criteria.getEndDate()));
                boxItem.setBatch(criteria.getBatch());
                boxItem.setItemCode(criteria.getItemCode());
            }
            if(criteria.getAutoverifyPermission().equals(1)){
                boxItem.setSubInventoryId(TaskTypeConstant.DESIRED);
            }else{
                boxItem.setSubInventoryId(TaskTypeConstant.QUALIFIED);
            }
            List<BillInRecordDto> billInRecordDtos = null;
            if(criteria.getAutoverifyPermission().equals(2)) {
                billInRecordDtos = billInRecordService.findCheckRecordFromEBS(new BillInRecordCriteria(3, criteria.getLastBarCode()));
            }else {
                BillInRecordCriteria billInRecordCriteria = new BillInRecordCriteria();
                billInRecordCriteria.setBoxCode(criteria.getBoxCode());
                billInRecordCriteria.setState(3);
                billInRecordCriteria.setBarCode(criteria.getLastBarCode());
                billInRecordDtos = billInRecordService.findCheckRecordFromEBS(billInRecordCriteria);
            }
            boxItem.setInTime(DateUtils.getTime());
            boxItem.setBillInDetailId(billInRecordDtos.get(0).getBillInDetailId());
            boxItemService.update(boxItem);
            BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(criteria.getBoxCode());
            boxInfo.setBoxState(3);
            boxInfo.setHasGoods(1);
            boxInfo.setBoxCellId(cellInfoDto.getCellId());
            boxInfoService.update(boxInfo);
            TaskInfo taskInfo = new TaskInfo(
                    null, new GuidUtils().toString(), "105",
                    MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto.getShelfName(), cellInfoDto.getSColumn(), cellInfoDto.getSRow()),
                    TaskTypeConstant.IN_AVAILABLE_BOX, 0, boxItem.getQuantity(), criteria.getBoxCode());
            taskInfo.setCompleteQuantity(criteria.getQuantity());
            taskInfo.setCardNo(criteria.getOperatorCard());
            taskInfo.setBarCode(MyUtils.connectPrintString(boxItem.getItemCode(),boxItem.getQuantity(),boxItem.getExp(),boxItem.getBatch(),itemInfo.getItemName()));
            taskInfo.setBillInDetailId(billInRecordDtos.get(0).getBillInDetailId());
            taskInfoService.save(taskInfo);
        } catch (Exception e) {
            e.printStackTrace();
            if(!error.equals("")) {
                throw new ServiceException(CommonCode.GENERAL_WARING_CODE, error);
            }else{
                throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
            }
        }
        return ResultGenerator.genSuccessResult(success+"入库任务已下发!");
    }

    private EbsBack getEBSOrderInformation(String billNo,Integer itemId){
        Map<String, String> map = new HashMap<>();
        map.put("poOrder", billNo);
        map.put("itemId", itemId.toString());
        List<Map<String, String>> lists = new ArrayList<>();
        lists.add(map);
        EbsBack entityStr = serverVisitAddressService.requestServerCode("1",
                TaskTypeConstant.GET_LINES_ALL, TaskTypeConstant.QUERY, null, lists);
        return entityStr;
    }

    private void saveBillInRecord(Integer billInDetailId,Integer oneBagQuantity,String boxCode,String BarCode,
                               String pd,String exp,String batch,Integer state,String  cardNo){
        BillInRecord billInRecord = new BillInRecord(billInDetailId,oneBagQuantity, DateUtils.getTime(),
                boxCode,BarCode,MyUtils.stringFromatDate(pd), MyUtils.stringFromatDate(exp),batch,state);
        billInRecord.setOperatorCardNo(cardNo);
        billInRecordService.save(billInRecord);
    }

    private BillInDetail spliteEBSParams(com.alibaba.fastjson.JSONObject jsonObject,Integer billId){

        /** 物料编码 */
        Integer itemId = MyUtils.backInteger(jsonObject.get("itemId"));
        /**  分配行ID */
        Integer poDistributionId = MyUtils.backInteger(jsonObject.get("poDistributionId"));
        /**  分配行号 **/
        String distributionNum = MyUtils.backString(jsonObject.get("distributionNum"));
        /** 采购单号  **/
        String segment = MyUtils.backString(jsonObject.get("phaSegment1"));
        /** 采购订单头ID **/
        Integer poHeaderId = MyUtils.backInteger(jsonObject.get("poHeaderId"));
        /** 采购订单行号 **/
        String lineNum = MyUtils.backString(jsonObject.get("lineNum"));
        /** 采购订单行ID **/
        Integer poLineId = MyUtils.backInteger(jsonObject.get("poLineId"));
        /** 发运行号 **/
        String shipmentNum = MyUtils.backString(jsonObject.get("shipmentNum"));
        /** 发运行ID **/
        Integer lineLocationId = MyUtils.backInteger(jsonObject.get("lineLocationId"));
        /**  OU组织ID */
        Integer orgId = MyUtils.backInteger(jsonObject.get("orgId"));
        /** 接收库存组织ID  */
        Integer shipToOrganizationId = MyUtils.backInteger(jsonObject.get("shipToOrganizationId"));
        /** 物料描述 **/
        String itemDescription = MyUtils.backString(jsonObject.get("itemDescription"));
        /** 采购单位 **/
        String unitMeasLookupCode = MyUtils.backString(jsonObject.get("unitMeasLookupCode"));
        /**  采购单价 */
        Double unitPrice = jsonObject.get("unitPrice") != null ? Double.valueOf(jsonObject.get("unitPrice").toString().trim()) : null;
        /** 发运行数量 **/
        Integer quantity = MyUtils.backDouble(jsonObject.get("quantity"));
        /**  已接收数量  **/
        Integer quantityReceived = MyUtils.backDouble(jsonObject.get("quantityReceived"));
        /**  发运行状态 **/
        String closedCode = MyUtils.backString(jsonObject.get("closedCode"));
        /**  接收类型 */
        String supplyTypeCode = MyUtils.backString(jsonObject.get("supplyTypeCode"));
        /**  可接收数量 */
        Integer surplusReceivedQuantity = MyUtils.backDouble(jsonObject.get("msQuantity"));
        /**  供应商Id **/
        Integer vendorId = MyUtils.backInteger(jsonObject.get("vendorId"));;
        /** 供应商编码 */
        String vendorCode = MyUtils.backString(jsonObject.get("segment1"));
        /**  供应商名称 */
        String vendorName = MyUtils.backString(jsonObject.get("vendorName"));
        //到货日期
        String dueDate = MyUtils.backString(jsonObject.get("dueDate"));
        OrderInformation orderInformation = new OrderInformation(itemId, poDistributionId, distributionNum,
                segment, poHeaderId, lineNum, poLineId, shipmentNum, lineLocationId, orgId, shipToOrganizationId, itemDescription,
                unitMeasLookupCode, unitPrice, quantity, quantityReceived, closedCode, supplyTypeCode, surplusReceivedQuantity,
                vendorId, vendorCode, vendorName,dueDate);
        orderInformationService.save(orderInformation);
        BillInDetail billInDetail = new BillInDetail(billId, itemId, poDistributionId, distributionNum,
                segment, poHeaderId, lineNum, poLineId, shipmentNum, lineLocationId, orgId, shipToOrganizationId, itemDescription,
                unitMeasLookupCode, unitPrice, quantity, quantityReceived, closedCode, supplyTypeCode, surplusReceivedQuantity,
                vendorId, vendorCode, vendorName, 1,dueDate);
        billInDetailService.save(billInDetail);
        Supplier supplier = supplierService.findBySupplierCode(vendorCode);
        if (supplier == null) {
            supplier = new Supplier(vendorName, vendorCode, vendorId);
            supplierService.save(supplier);
        }
        return billInDetail;
    }

    private Integer returnOneBagQuantity(BillInDetail billInDetail,Integer oneBagQuantity,String boxCode,String BarCode,
            String pd,String exp,String batch,String cardNo){
        BillInRecord billInRecord = new BillInRecord(billInDetail.getBillInDetailId(),0,
                DateUtils.getTime(),boxCode,BarCode,MyUtils.stringFromatDate(pd), MyUtils.stringFromatDate(exp),batch,1);
        billInRecord.setOperatorCardNo(cardNo);
        if(oneBagQuantity<=billInDetail.getSurplusReceivedQuantity()) {
            billInRecord.setAcceptQuantity(oneBagQuantity);
            billInDetail.setQuantityReceived(billInDetail.getQuantityReceived()+oneBagQuantity);
            if(oneBagQuantity == billInDetail.getSurplusReceivedQuantity()){
                billInDetail.setSurplusReceivedQuantity(0);
                billInDetail.setBillInState(2);
            }else{
                billInDetail.setSurplusReceivedQuantity(billInDetail.getSurplusReceivedQuantity() - oneBagQuantity);
            }
            oneBagQuantity = 0;
        }else{
            billInRecord.setAcceptQuantity(billInDetail.getSurplusReceivedQuantity());
            billInDetail.setQuantityReceived(billInDetail.getQuantityReceived()+billInDetail.getSurplusReceivedQuantity());
            billInDetail.setSurplusReceivedQuantity(0);
            oneBagQuantity = oneBagQuantity-billInDetail.getSurplusReceivedQuantity();
        }
        billInRecordService.save(billInRecord);
        billInDetailService.update(billInDetail);
        return oneBagQuantity;
    }

    private String returnEBSReceived(String lastBarCode){
        List<BillInRecordDto> billInRecordDtos = billInRecordService.findCheckRecordFromEBS(new BillInRecordCriteria(1,lastBarCode));
        List<Map<String, String>> lists = new ArrayList<>();
        RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS采购接收数据写入EBS接口");
        List<RequestId> requestIds = new ArrayList<>();
        for(BillInRecordDto billInRecordDto : billInRecordDtos) {
            lists.add(MyUtils.receive(TaskTypeConstant.organizationId.toString(),billInRecordDto.getPoHeaderId().toString(),
                    billInRecordDto.getPoLineId().toString(),billInRecordDto.getLineLocationId().toString(),
                    billInRecordDto.getPoDistributionId().toString(),billInRecordDto.getItemId().toString(),
                    billInRecordDto.getAcceptQuantity().toString(),billInRecordDto.getAcceptTime(),
                    billInRecordDto.getBatch(),billInRecordDto.getExp(),billInRecordDto.getPd()
            ));

            RequestId requestId = new RequestId(requestIdAuto.getRequestId(),billInRecordDto.getItemId(),billInRecordDto.getAcceptQuantity(),
                    billInRecordDto.getBatch(),billInRecordDto.getPoHeaderId(),billInRecordDto.getPoLineId(),billInRecordDto.getLineLocationId(),
                    billInRecordDto.getPoDistributionId(),billInRecordDto.getAcceptTime(),TaskTypeConstant.organizationId,TaskTypeConstant.RECEIVE,
                    TaskTypeConstant.FAIL_NO_MANAGE,"WMS采购接收数据写入EBS接口失败","ERROR",billInRecordDto.getExp(),billInRecordDto.getPd());
//            requestIdService.save(requestId);
            requestIds.add(requestId);
        }
        try {
            return requestIdService.receive(null,billInRecordDtos,requestIds, lists, requestIdAuto);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }

    private String receviceTotal(String boxCode,String operatorNo){
        BillInRecordCriteria billInRecordCriteria = new BillInRecordCriteria();
        billInRecordCriteria.setState(1);
        billInRecordCriteria.setBoxCode(boxCode);
        List<BillInRecordDto> billInRecordDtoOnes = billInRecordService.findCheckRecordFromEBS(billInRecordCriteria);
        billInRecordCriteria.setOrderParam(1010);
        List<BillInRecordDto> billInRecordDtos = billInRecordService.findCheckRecordFromEBS(billInRecordCriteria);

        List<Map<String, String>> lists = new ArrayList<>();
        RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS采购接收数据写入EBS接口");
        List<RequestId> requestIds = new ArrayList<>();
        //创建接收总计集合
        List<BillInReceive> billInReceives = new ArrayList<>();

        for(BillInRecordDto billInRecordDto : billInRecordDtos) {
            RequestId requestId = new RequestId(requestIdAuto.getRequestId(),billInRecordDto.getItemId(),billInRecordDto.getAcceptQuantity(),
                    billInRecordDto.getBatch(),billInRecordDto.getPoHeaderId(),billInRecordDto.getPoLineId(),billInRecordDto.getLineLocationId(),
                    billInRecordDto.getPoDistributionId(),billInRecordDto.getAcceptTime(),TaskTypeConstant.organizationId,TaskTypeConstant.RECEIVE,
                    TaskTypeConstant.FAIL_NO_MANAGE,"WMS采购接收数据写入EBS接口失败","ERROR",billInRecordDto.getExp(),billInRecordDto.getPd());
            requestIds.add(requestId);

            lists.add(MyUtils.receive(TaskTypeConstant.organizationId.toString(),billInRecordDto.getPoHeaderId().toString(),
                    billInRecordDto.getPoLineId().toString(),billInRecordDto.getLineLocationId().toString(),
                    billInRecordDto.getPoDistributionId().toString(),billInRecordDto.getItemId().toString(),
                    billInRecordDto.getAcceptQuantity().toString(),billInRecordDto.getAcceptTime(),
                    billInRecordDto.getBatch(),billInRecordDto.getExp(),billInRecordDto.getPd()
            ));

            BillInReceive billInReceive = new BillInReceive(billInRecordDto.getBillInDetailId(), billInRecordDto.getAcceptQuantity(),
                    boxCode,DateUtils.getTime(),billInRecordDto.getPd(),billInRecordDto.getExp(),billInRecordDto.getBatch(),
                    operatorNo,1,billInRecordDto.getItemId(),billInRecordDto.getPoDistributionId(),billInRecordDto.getPoHeaderId(),
                    billInRecordDto.getPoLineId(),billInRecordDto.getLineLocationId());
            billInReceives.add(billInReceive);
        }
        try {
            return requestIdService.receive(billInReceives,billInRecordDtoOnes,requestIds, lists, requestIdAuto);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }

    @PostMapping("/refreshOrder")
    @ResponseBody
    @Transactional
    public Result refreshOrder(@RequestBody BillInMasterCriteria criteria) {
        BillInDetail billInDetail = billInDetailService.findById(criteria.getBillInDetailId());
        if(billInDetail.getPoDistributionId()==null){
            return ResultGenerator.genFailResult(CommonCode.SERVER_INERNAL_ERROR,"当前订单为初始化入库订单，无法获取");
        }
        List<BillInRecordDto> billInRecordDtos = billInRecordService.findList(new BillInRecordCriteria(1));
        if(billInRecordDtos.size()>0){
            return ResultGenerator.genFailResult(CommonCode.SERVER_INERNAL_ERROR,"当前有未接收数据，请处理完成后再次刷新");
        }
        EbsBack entityStr = getEBSOrderInformation(billInDetail.getSegment(),billInDetail.getItemId());
        if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
            JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
            for (int i = 0; i < jsonArrays.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                /**  已接收数量  **/
                Integer quantityReceived = MyUtils.backDouble(jsonObject.get("quantityReceived"));
                /**  可接收数量 */
                Integer surplusReceivedQuantity = MyUtils.backDouble(jsonObject.get("msQuantity"));
                Integer poDistributionId = MyUtils.backInteger(jsonObject.get("poDistributionId"));
                /** 采购订单头ID **/
                Integer poHeaderId = MyUtils.backInteger(jsonObject.get("poHeaderId"));
                /** 采购订单行ID **/
                Integer poLineId = MyUtils.backInteger(jsonObject.get("poLineId"));
                /** 发运行ID **/
                Integer lineLocationId = MyUtils.backInteger(jsonObject.get("lineLocationId"));
                if(billInDetail.getPoDistributionId().equals(poDistributionId)
                        && billInDetail.getPoHeaderId().equals(poHeaderId)
                        && billInDetail.getPoLineId().equals(poLineId)
                        && billInDetail.getLineLocationId().equals(lineLocationId)
                ){
                    billInDetail.setQuantityReceived(quantityReceived);
                    billInDetail.setSurplusReceivedQuantity(surplusReceivedQuantity);
                    billInDetailService.update(billInDetail);
                }
            }
        }else{
            return ResultGenerator.genFailResult(CommonCode.SERVER_INERNAL_ERROR,"EBS订单信息获取失败");
        }
        return ResultGenerator.genSuccessResult();
    }
}

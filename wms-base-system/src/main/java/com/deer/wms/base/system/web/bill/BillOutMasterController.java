package com.deer.wms.base.system.web.bill;

import com.deer.wms.base.system.model.Operator;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.bill.*;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemCriteria;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.PickTaskDto;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.service.MESWebService.BillOutWorkerOrder;
import com.deer.wms.base.system.service.OperatorService;
import com.deer.wms.base.system.service.bill.IBillOutDetailService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
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
import com.deer.wms.framework.util.MyUtils;
import com.deer.wms.framework.util.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 出库单 信息操作处理
 *
 * @author cai
 * @date 2019-05-13
 */
@Controller
@RequestMapping("/out/billOutMaster")
public class BillOutMasterController extends BaseController {

    private String prefix = "out/billOutMaster";

    @Autowired
    private IBillOutMasterService billOutMasterService;

    @Autowired
    private IBillOutDetailService billOutDetailService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private IItemInfoService itemInfoService;
    @Autowired
    private PickTaskService pickTaskService;

    /**
     * 保存出库信息 , 出库单与出库详情
     */
    @RequiresPermissions("out:billOutMaster:add")
    @Log(title = "出库单", businessType = BusinessType.INSERT)
    @PostMapping("/insert")
    @ResponseBody
    @Transactional
    public Result insert(@RequestBody InserData inserData){
        //判断库存货物数量是否足够出库 不够就返回“false”
       List<BillOutDetail> billOutDetailList = inserData.getBillOutDetailList();
        for(BillOutDetail billOutDetail : billOutDetailList){
            List<BoxItemDto> boxItemDtos = boxItemService.getBoxItemDtoByitemCode(billOutDetail.getItemCode());
            Double quantitys = 0.0;
            for(BoxItemDto boxItemDto : boxItemDtos){
                quantitys += boxItemDto.getQuantity();
                if(billOutDetail.getQuantity() > quantitys){
                    return ResultGenerator.genSuccessResult("false");
                }
            }
        }
        String createUserName = ShiroUtils.getLoginName();
        Integer userId = ShiroUtils.getUserId();
        BillOutMaster billOutMaster = inserData.getBillOutMaster();
        billOutMaster.setCreateUserName(createUserName);
        billOutMaster.setCreateUserId(userId.intValue());
        billOutMasterService.save(billOutMaster);
        Integer billId = billOutMaster.getBillId();
        List<BillOutDetail> billOutDetails = inserData.getBillOutDetailList();
        for(BillOutDetail billOutDetail : billOutDetails ){
            billOutDetail.setBillId(billId);
            billOutDetailService.saveBillOutDetail(billOutDetail);
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 删除入库单
     */
    @RequiresPermissions("out:billOutMaster:remove")
    @Log(title = "出库单", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        return toAjax(billOutMasterService.deleteBillOutMasterByIds(ids));
    }

    /**
     * 修改保存入库单
     */
    @RequiresPermissions("out:billOutMaster:edit")
    @Log(title = "出库单", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BillOutMaster billOutMaster)
    {
        return toAjax(billOutMasterService.updateBillOutMaster(billOutMaster));
    }

    /**
     * 修改入库单
     */
    @GetMapping("/edit/{billId}")
    public String edit(@PathVariable("billId") Integer billId, ModelMap mmap)
    {
        BillOutMaster billOutMaster = billOutMasterService.selectBillOutMasterById(billId);
        mmap.put("billOutMaster", billOutMaster);
        return prefix + "/edit";
    }

    /**
     * 查看详情
     */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    /**
     * 新增出库单
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    @RequiresPermissions("out:billOutMaster:view")
    @GetMapping("/page")
    public String billInMaster()
    {
        return prefix + "/billOutMaster";
    }

    /**
     * 根据billId查询出库单信息
     */
    @RequiresPermissions("out:billOutMaster:findBillOutMasterDtoByBillId")
    @PostMapping("/findBillOutMasterDtoByBillId")
    @ResponseBody
    public BillOutMasterDto findBillOutMasterDtoByBillId(Integer billId){
        BillOutMasterDto billOutMasterDto = billOutMasterService.findBillOutMasterDtoByBillId(billId);
        return billOutMasterDto;
    }

    /**
     * 查询出库单列表
     */
//    @RequiresPermissions("out:billOutMaster:list")
    @PostMapping("/findList")
    @ResponseBody
    public TableDataInfo findList(BillOutMasterCriteria criteria) {
        startPage();
        List<BillOutMasterDto> list = billOutMasterService.selectBillOutMasterList(criteria);
        return getDataTable(list);
    }

    @PostMapping("/findList1")
    @ResponseBody
    public TableDataInfo list1(BillOutMasterCriteria criteria) {
        startPage();
        criteria.setType(TaskTypeConstant.WORKER_ORDER_OUT);
        criteria.setOrderByState(101);
        List<BillOutMasterDto> list = billOutMasterService.findList(criteria);
        return getDataTable(list);
    }

    /**
     * 工单管理
     * @return
     */
    @RequiresPermissions("out:billOutMaster:workerOrderManage")
    @GetMapping("/workerOrderManage")
    public String workerOrderManage()
    {
        return  "manage/workerOrderCancel/workerOrderCancel";
    }

    @PostMapping("/findListTwo")
    @ResponseBody
    public TableDataInfo findListTwo(BillOutMasterCriteria criteria) {
        startPage();
        List<BillOutMasterDto> list = billOutMasterService.findListTwo(criteria);
        return getDataTable(list);
    }

    /**
     * 手动工单取消
     * @param criteria
     * @return
     */
    @PostMapping("/manualCancel")
    @ResponseBody
    @Transactional
    public Result manualCancel(@RequestBody BillOutMasterCriteria criteria) {
        String error = "服务器内部错误，请联系管理员";
        try{
            Operator operator = operatorService.findByCard(criteria.getLoginPersonCardNo());
            List<BillOutMasterDto> billOutMasterDtos = billOutMasterService.findList(criteria);
            for(BillOutMasterDto billOutMasterDto : billOutMasterDtos) {

                //查找领料任务
                cancelLockInventory(billOutMasterDto.getBillOutDetailId());

                billOutMasterDto.setCreateUserId(operator.getOperatorId());
                billOutMasterDto.setCreateTime(DateUtils.getTime());
                billOutMasterDto.setCreateUserName(operator.getOperatorName());
                billOutMasterDto.setMemo(billOutMasterDto.getMemo()+"取消原因:硬件异常取消");
                billOutMasterDto.setState(3);
                billOutMasterService.update(billOutMasterDto);
            }
            return ResultGenerator.genSuccessResult();
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }
    }

    /**
     * 手动工单取消并重新下发
     * @param criteria
     * @return
     */
    @PostMapping("/manualCancelAgainIssue")
    @ResponseBody
    @Transactional
    public Result manualCancelAgainIssue(@RequestBody BillOutMasterCriteria criteria) {
        String error = "服务器内部错误，请联系管理员";
        try{
            Operator operator = operatorService.findByCard(criteria.getLoginPersonCardNo());
            List<BillOutMasterDto> billOutMasterDtos = billOutMasterService.findList(criteria);
            for(BillOutMasterDto billOutMasterDto : billOutMasterDtos) {
                //查找领料任务
                if(billOutMasterDto.getState().equals(0)){
                    error="当前工单暂未下发，请勿选中！";
                    throw new RuntimeException();
                }
                cancelLockInventory(billOutMasterDto.getBillOutDetailId());
                billOutMasterDto.setCreateUserId(operator.getOperatorId());
                billOutMasterDto.setCreateUserName(operator.getOperatorName());
                billOutMasterDto.setMemo(billOutMasterDto.getMemo()+"取消并重新下发，取消原因:硬件异常取消");
                billOutMasterDto.setState(3);
                billOutMasterService.update(billOutMasterDto);
            }
            for(BillOutMasterDto billOutMasterDto : billOutMasterDtos) {
                billOutDetailService.lockInventoryManage(new BillOutWorkerOrder(billOutMasterDto.getTaskId(),
                        billOutMasterDto.getBillNo(),billOutMasterDto.getFinishedCode(),billOutMasterDto.getQuantity(),
                        billOutMasterDto.getPriority(),billOutMasterDto.getItemCode()));
            }
            return ResultGenerator.genSuccessResult();
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }
    }

    private void cancelLockInventory(Integer billOutDetailId){
        List<PickTaskDto> pickTaskDtos = pickTaskService.findListTwo(new PickTaskCriteria(billOutDetailId));
        if (pickTaskDtos.size() > 0) {
            for (PickTaskDto pickTaskDto : pickTaskDtos) {
                if (pickTaskDto.getPickState().equals(4)) {
                    continue;
                } else {
                    //如果有料没领完
                    pickTaskDto.setPickState(5);
                    pickTaskService.update(pickTaskDto);
                    BoxItem boxItem = boxItemService.getBoxItemByBoxCode(pickTaskDto.getBoxCode());
//                            BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxItem.getBoxCode());
                    Integer surplus = boxItem.getForecastStockQuantity() - pickTaskDto.getLockPickQuantity();
                    if (surplus > 0) {
                        boxItem.setForecastStockQuantity(surplus);
                    } else {
                        boxItem.setForecastStockQuantity(0);
                        boxItem.setWorkOrderStockState(0);
                    }
                    boxItemService.update(boxItem);
                }
            }
        }
    }

    @GetMapping("/toNonWorkOrderOut")
    public String toNonWorkOrderOut()
    {
        return prefix + "/nonWorkOrderOut";
    }

    @RequiresPermissions("out:manualOutPage:view")
    @GetMapping("/manualOutPage")
    public String manualOutPage()
    {
        return "out/manualOut/manualOut";
    }

    /**
     * 手动出库
     * @param criteria
     * @return
     */
    @PostMapping("/manualOut")
    @ResponseBody
    @Transactional
    public Result manualOut(@RequestBody BillOutMasterCriteria criteria) {
        String error = "服务器内部错误，请联系管理员";
        try{
            //查找可出料
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            BoxItemCriteria boxItemCriteriaOne = new BoxItemCriteria();
            boxItemCriteriaOne.setItemCode(criteria.getItemCode());
            boxItemCriteriaOne.setOrderByState(1006);
            List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteriaOne);
            int canOutQuantity = 0;
            if(boxItemDtos.size()>0){
                Bloop:
                for(int i=0;i<boxItemDtos.size();i++) {
                    BoxItemDto boxItemDto = boxItemDtos.get(i);
                    if (boxItemDto.getSubInventoryId().equals(TaskTypeConstant.POSTPONE) &&
                            TaskTypeConstant.workerOrderIssueTime.getDelayControl().equals(2)) {
                        continue;
                    }
                    if (boxItemDto.getQuantity() - boxItemDto.getLockQuantity() > 0) {
                        canOutQuantity = boxItemDto.getQuantity() - boxItemDto.getLockQuantity();
                        cellInfoService.updateCellStateAndBoxStateAndSendTaskInfo(boxItemDto, criteria.getBillOutDetailId(), criteria.getLoginPersonCardNo());
                        break Bloop;
                    }
                    else if(i==(boxItemDtos.size()-1)){
                        error = "当前无可出箱！";
                        throw new RuntimeException();
                    }
                }
            }else{
                error = "当前无可出箱！";
                throw new RuntimeException();
            }
            return ResultGenerator.genSuccessResult(canOutQuantity);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }
    }

    /**
     * 手动出库完成后入库
     * @param criteria
     * @return
     */
    @PostMapping("/backWareAfterManualOut")
    @ResponseBody
    @Transactional
    public Result backWareAfterManualOut(@RequestBody BillOutMasterCriteria criteria) {
        String error = "服务器内部错误，请联系管理员";
        try{
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            String msg = cellInfoService.inAvailableBoxAfterManualOut(criteria.getBoxCode(),criteria.getOutQuantity(),
                    criteria.getLoginPersonCardNo(),criteria.getBillNo());
            //盘点
            if(!msg.equals("success")){
                error = msg;
                throw new RuntimeException();
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }finally{

        }
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 退库入库
     */
//	@RequiresPermissions("in:boxItem:checkOut")
    @Log(title = "退库入库", businessType = BusinessType.OTHER)
    @PostMapping( "/returnItemOutBox")
    @ResponseBody
    @Transactional
    public Result qualityAbnormalCheck(@RequestBody BoxItemCriteria boxItemCriteria)
    {
        String error = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteria);
            String bool = cellInfoService.judgeBoxItemState(boxItemDtos);
            if (!bool.equals("success")) {
                error = bool;
                throw new RuntimeException();
            }
            ItemInfo itemInfo = itemInfoService.findByItemCode(boxItemDtos.get(0).getItemCode());
            if((boxItemCriteria.getQuantity()+boxItemDtos.get(0).getQuantity())> itemInfo.getMaxPackQty()){
                error = "总数已超过最大可入数量，剩余可入数量为"+(itemInfo.getMaxPackQty()-boxItemDtos.get(0).getQuantity());
                throw new RuntimeException();
            }
            cellInfoService.updateCellStateAndBoxStateAndSendTaskInfo(boxItemDtos.get(0), null, boxItemCriteria.getLoginPersonCardNo());
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 退库入库  物料进箱后入库
     */
    @PostMapping( "/backWareHouseIn")
    @ResponseBody
    @Transactional
    public Result backWareHouseIn(@RequestBody BoxItemCriteria boxItemCriteria)
    {
        String error = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            Operator operator = operatorService.findByCard(boxItemCriteria.getLoginPersonCardNo());
            Operator operatorOne = operatorService.findByCard(boxItemCriteria.getCardOne());
            if(operatorOne == null){
                error = "第二次刷卡操作员未录入系统，请联系管理员录入后再操作";
                throw new RuntimeException();
            }
            if(operatorOne.getBackWarePermission().equals(1)){
                error = "第二次刷卡操作员无退库权限，请联系管理员开启";
                throw new RuntimeException();
            }
            BoxItem boxItem = boxItemService.getBoxItemByBoxCode(boxItemCriteria.getBoxCode());
            ItemInfo itemInfo = itemInfoService.findByItemCode(boxItem.getItemCode());
            if(itemInfo.getMaxPackQty()<(boxItem.getQuantity()+boxItemCriteria.getQuantity())){
                error = "超过单箱最大存储数量，当前箱数量为"+boxItem.getQuantity()+"张，最大存储数量为"+itemInfo.getMaxPackQty()+"张！";
                throw new RuntimeException();
            }
            boxItem.setQuantity(boxItem.getQuantity()+boxItemCriteria.getQuantity());
            boxItemService.update(boxItem);

            BillOutMaster billOutMaster = new BillOutMaster(MyUtils.getInWarehouseNo(),DateUtils.getTime(),operator.getOperatorName(),
                    operator.getOperatorId(),2,"退库出库",212,TaskTypeConstant.BACK_WARE_HOUSE,
                    operatorOne.getOperatorName(),operatorOne.getOperatorId());
            billOutMasterService.save(billOutMaster);
            BillOutDetail billOutDetail = new BillOutDetail(billOutMaster.getBillId(),boxItem.getItemCode(),boxItemCriteria.getQuantity());
            billOutDetail.setAlreadyOutQuantity(boxItemCriteria.getQuantity());
            billOutDetailService.save(billOutDetail);

            PickTask pickTask = new PickTask(boxItem.getBoxCode(),boxItemCriteria.getQuantity(),
                    billOutDetail.getBillOutDetailId(),4,boxItem.getBatch(),boxItem.getSubInventoryId(),
                    DateUtils.getTime(),DateUtils.getTime(),TaskTypeConstant.BACK_WARE_HOUSE);
            pickTaskService.save(pickTask);

            cellInfoService.inAvailableBox(boxItem,boxItemCriteria.getLoginPersonCardNo(),billOutDetail.getBillOutDetailId(),itemInfo.getItemName());

        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }
        return ResultGenerator.genSuccessResult();
    }

    //调整时间
    @GetMapping( "/adjustTime")
    @ResponseBody
    @Transactional
    public Result adjustTime()
    {
        String error = "服务器内部错误，请联系管理员";
        try {
            List<BillOutMasterDto> billOutMasterDtos = billOutMasterService.findList(new BillOutMasterCriteria(1,0));
            if(billOutMasterDtos.size()>0){
                for(BillOutMaster billOutMaster : billOutMasterDtos){
                    billOutMaster.setCreateTime(DateUtils.getTime());
                    billOutMasterService.update(billOutMaster);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
        }
        return ResultGenerator.genSuccessResult();
    }
}












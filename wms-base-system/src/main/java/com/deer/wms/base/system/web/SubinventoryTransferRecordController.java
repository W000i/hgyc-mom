package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.bill.BillInDetail;
import com.deer.wms.base.system.model.bill.BillInMaster;
import com.deer.wms.base.system.model.bill.BillOutMaster;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemCriteria;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.PickTaskDto;
import com.deer.wms.base.system.model.transferReason.TransferReason;
import com.deer.wms.base.system.model.transferReason.TransferReasonCriteria;
import com.deer.wms.base.system.service.*;
import com.deer.wms.base.system.service.bill.IBillInDetailService;
import com.deer.wms.base.system.service.bill.IBillInMasterService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.impl.SubinventoryTransferRecordServiceImpl;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.framework.util.MyUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.deer.wms.common.core.result.Result;
import com.deer.wms.common.core.result.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import com.deer.wms.common.core.controller.BaseController;
import com.deer.wms.common.core.page.TableDataInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* Created by  on 2019/10/31.
*/
@Controller
@RequestMapping("/subinventoryTransferRecord")
public class SubinventoryTransferRecordController  extends BaseController{

    private String prefix = "manage/transferRecord";

    @Autowired
    private SubinventoryTransferRecordService subinventoryTransferRecordService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private BillInRecordService billInRecordService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private SubInventoryService subInventoryService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;
    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private TransferReasonService transferReasonService;
    @Autowired
    private PickTaskService pickTaskService;
    @Autowired
    private IBillOutMasterService billOutMasterService;
    @Autowired
    private IBillInDetailService billInDetailService;
    @Autowired
    private IBillInMasterService billInMasterService;

    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("subinventoryTransferRecord:view")
    @GetMapping()
    public String subinventoryTransferRecord()
    {
        return prefix + "/transferRecord";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
    SubinventoryTransferRecord subinventoryTransferRecord = subinventoryTransferRecordService.findById(id);
        mmap.put("subinventoryTransferRecord", subinventoryTransferRecord);
        return prefix + "/edit";
    }

    /**
    * 新增
    */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }


    @PostMapping("/insert")
    @ResponseBody
    public Result add(@RequestBody SubinventoryTransferRecord subinventoryTransferRecord) {
        subinventoryTransferRecordService.save(subinventoryTransferRecord);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        subinventoryTransferRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody SubinventoryTransferRecord subinventoryTransferRecord) {
        subinventoryTransferRecordService.update(subinventoryTransferRecord);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        SubinventoryTransferRecord subinventoryTransferRecord = subinventoryTransferRecordService.findById(id);
        return ResultGenerator.genSuccessResult(subinventoryTransferRecord);
    }

    @PostMapping("/findList")
    @ResponseBody
    public  TableDataInfo findList(SubinventoryTransferRecordCriteria criteria) {
        startPage();
        List<SubinventoryTransferRecordDto> list = subinventoryTransferRecordService.findList(criteria);
        return getDataTable(list);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(SubinventoryTransferRecordCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<SubinventoryTransferRecord> list = subinventoryTransferRecordService.findAll();
        return getDataTable(list);
    }

    /**
     * 初始化库存不合格
     * @param subinventoryTransferRecordCriteria
     * @return
     */
    @PostMapping("/initializeUnqualified")
    @ResponseBody
    public Result initializeUnqualified(@RequestBody SubinventoryTransferRecordCriteria subinventoryTransferRecordCriteria) {
        List<BoxItemDto> boxItemDtos = boxItemService.findList(new BoxItemCriteria(subinventoryTransferRecordCriteria.getIds()));
        for(BoxItemDto boxItemDto : boxItemDtos){
            BillInDetail billInDetail = billInDetailService.findById(boxItemDto.getBillInDetailId());
            BillInMaster billInMaster = billInMasterService.findById(billInDetail.getBillId());
            if(billInMaster.getType().equals(1)){
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"请选择初始化库存箱");
            }
            SubinventoryTransferRecord subinventoryTransferRecord = new SubinventoryTransferRecord(
                boxItemDto.getBoxCode(),boxItemDto.getItemCode(), boxItemDto.getBatch(),boxItemDto.getQuantity(), DateUtils.getTime(),
                    subinventoryTransferRecordCriteria.getLoginPersonCardNo(),boxItemDto.getSubInventoryId(),
                    TaskTypeConstant.UNQUALIFIED,"初始化库存转移至不合格库");
            subinventoryTransferRecord.setToExp(boxItemDto.getExp());
            subinventoryTransferRecord.setForExp(boxItemDto.getExp());
            subinventoryTransferRecordService.save(subinventoryTransferRecord);
            boxItemDto.setSubInventoryId(TaskTypeConstant.UNQUALIFIED);
            boxItemService.update(boxItemDto);
        }
        return ResultGenerator.genSuccessResult();
    }

    //子库转移
    @PostMapping("/subInventoryTransfer")
    @ResponseBody
    @Transactional
    public Result subInventoryTransfer(@RequestBody SubinventoryTransferRecordCriteria subinventoryTransferRecordCriteria) {
        String errMsg = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                errMsg = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            if((subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.QUALIFIED) ||
                subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.UNQUALIFIED))) {
                if (subinventoryTransferRecordCriteria.getTransferReason().trim().equals("")) {
                    errMsg = "请输入或者选择原因！";
                    throw new RuntimeException();
                } else {
                    List<TransferReason> transferReasons = transferReasonService.findList(new TransferReasonCriteria(subinventoryTransferRecordCriteria.getTransferReason().trim()));
                    if (transferReasons.size() <= 0) {
                        TransferReason transferReason = new TransferReason(subinventoryTransferRecordCriteria.getTransferReason(),
                                DateUtils.getTime(), subinventoryTransferRecordCriteria.getLoginPersonCardNo());
                        transferReasonService.save(transferReason);
                    }
                }
            }
            //判断延期是否超过一天
            if(subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.POSTPONE) &&
                    MyUtils.calculateDateDiffer(
                            new SimpleDateFormat("yyyy-MM-dd").parse(subinventoryTransferRecordCriteria.getPostpone()),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtils.getTime())) <= 1l
            ){
                errMsg = "请选择时间或选择时间至今间隔1天及以上";
                throw new RuntimeException();
            }
            List<BoxItemDto> boxItemDtos = boxItemService.findBoxItemList(new BoxItemCriteria(subinventoryTransferRecordCriteria.getIds()));
            String bool = cellInfoService.judgeBoxItemState(boxItemDtos);
            for(BoxItemDto boxItemDto : boxItemDtos){
                if(1 != boxItemDto.getCellState()){
                    errMsg =  "选中箱不在货位";
                    throw new RuntimeException();
                }
                if(!boxItemDto.getBoxState().equals(1)){
                    errMsg =  "选中箱任务中";
                    throw new RuntimeException();
                }
                /*if(boxItemDto.getWorkOrderStockState().equals(1)){
                    errMsg =  "选中箱工单锁定中";
                    throw new RuntimeException();
                }*/
            }


            //定义传给EBS的集合
            List<Map<String, String>> lists = new ArrayList<>();
            //获取转移到的子库信息
            SubInventory subInventory = subInventoryService.findById(subinventoryTransferRecordCriteria.getToSubInventoryId());
            //获取自增长请求Id
            RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS转库数据写入EBS接口");
            //定义请求接口记录集合
            List<RequestId> requestIds = new ArrayList<>();

            for (BoxItemDto boxItemDto : boxItemDtos) {
                //保存转库记录
                SubinventoryTransferRecord subinventoryTransferRecord = new SubinventoryTransferRecord(boxItemDto.getBoxCode(),
                        boxItemDto.getItemCode(), boxItemDto.getBatch(), boxItemDto.getQuantity(), DateUtils.getTime(),
                        subinventoryTransferRecordCriteria.getLoginPersonCardNo(), boxItemDto.getSubInventoryId(),
                        subinventoryTransferRecordCriteria.getToSubInventoryId(), null);
                subinventoryTransferRecord.setForExp(boxItemDto.getExp());
                //往传给EBS的集合添加数据
                lists.add(MyUtils.subInventoryTransfer(TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, TaskTypeConstant.organizationId.toString(),
                        boxItemDto.getInventoryItemId().toString(), boxItemDto.getQuantity().toString(), boxItemDto.getSubInventoryCode(),
                        boxItemDto.getSlotting() == null ? null : boxItemDto.getSlotting(), MyUtils.getNinetySecondsAgo(), boxItemDto.getUnit(),
                        subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : subInventory.getSlotting(),
                        boxItemDto.getBatch(),"1","1"
//                        boxItemDto.getBillId().toString(), boxItemDto.getBillInDetailId().toString()
                ));
                //往请求记录的集合中添加数据
                RequestId requestId = new RequestId(requestIdAuto.getRequestId(), boxItemDto.getInventoryItemId(), boxItemDto.getQuantity(),
                        boxItemDto.getBatch(), DateUtils.getTime(), "WMS调用EBS子库转移接口失败", boxItemDto.getSubInventoryCode(),
                        boxItemDto.getSlotting() == null ? null : Integer.parseInt(boxItemDto.getSlotting()), subInventory.getOrganizationId(), TaskTypeConstant.TRANSFER,
                        TaskTypeConstant.FAIL_WAIT_MANAGE, TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, boxItemDto.getUnit(),
                        subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : Integer.parseInt(subInventory.getSlotting()),
                        1,1,
//                        boxItemDto.getBillId(), boxItemDto.getBillInDetailId(),
                        "ERROR");
                requestIds.add(requestId);
                //转到合格库 2
                if (subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.QUALIFIED)) {
                    //判断当前箱是否是不合格库 合格就执行 不合格提示操作人员重新选择
                    if(boxItemDto.getSubInventoryId().equals(TaskTypeConstant.UNQUALIFIED)){
                        //写入转库原因
                        subinventoryTransferRecord.setToExp(boxItemDto.getExp());
                        subinventoryTransferRecord.setTransferMemo(subinventoryTransferRecordCriteria.getTransferReason());
                    }else{
                        errMsg = "当前选择不符合规则，如需转至合格库，请选择不合格物料";
                        throw new RuntimeException();
                    }
                    //修改单箱绑定的子库
                    boxItemDto.setSubInventoryId(subInventory.getSubInventoryId());
                    boxItemService.update(boxItemDto);
                }
                //转到延期库 4
                else if (subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.POSTPONE)) {
                    if(boxItemDto.getSubInventoryId().equals(TaskTypeConstant.QUALIFIED)
                        || boxItemDto.getSubInventoryId().equals(TaskTypeConstant.OVER_DUE)) {
                        //转到延期库须填写延期日期
                        boxItemDto.setExp(subinventoryTransferRecordCriteria.getPostpone());
                        //写入延期工单号
//                        boxItemDto.setWorkerOrderNo(subinventoryTransferRecordCriteria.getDelayWorkerOrderId());
                        //转库原因
                        subinventoryTransferRecord.setTransferMemo("延期.");
                        subinventoryTransferRecord.setToExp(subinventoryTransferRecordCriteria.getPostpone());
                    }else{
                        errMsg = "当前选择不符合规则，如需转至延期库，请选择合格箱或者过期物料";
                        throw new RuntimeException();
                    }
                    //修改单箱绑定的子库
                    boxItemDto.setSubInventoryId(subInventory.getSubInventoryId());
                    boxItemService.update(boxItemDto);
                }
                //转到不合格库 5
                else if (subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.UNQUALIFIED)) {
                    //转库原因
                    if(boxItemDto.getSubInventoryId().equals(TaskTypeConstant.QUALIFIED)){
                        //写入转库原因
                        subinventoryTransferRecord.setTransferMemo(subinventoryTransferRecordCriteria.getTransferReason());
                        subinventoryTransferRecord.setToExp(boxItemDto.getExp());
                        //修改当前箱子库
                        boxItemDto.setSubInventoryId(subInventory.getSubInventoryId());
                        boxItemService.update(boxItemDto);
                        //根据箱号查询当前箱锁定物料
                        List<PickTaskDto> pickTasks = pickTaskService.findList(new PickTaskCriteria(1,boxItemDto.getBoxCode(),TaskTypeConstant.WORKER_ORDER_OUT));
                        if(pickTasks.size()>0){
                            for(PickTaskDto pickTaskDto : pickTasks) {
                                //当前工单取消
                                BillOutMaster billOutMaster = billOutMasterService.findById(pickTaskDto.getBillId());
                                billOutMaster.setState(3);
                                billOutMaster.setMemo(billOutMaster.getMemo()+".取消原因：转移到不合格库");
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
                    }else{
                        errMsg = "当前选择不符合规则，如需转至不合格库，请选择合格物料";
                        throw new RuntimeException();
                    }
                }
                /*//转到预测备料库 7(修改为锁定库存)
                else if (subinventoryTransferRecordCriteria.getToSubInventoryId().equals(TaskTypeConstant.FORECAST_PREPARATION)) {
                    //转库原因
                    if(boxItemDto.getSubInventoryId().equals(TaskTypeConstant.QUALIFIED)
                        || boxItemDto.getSubInventoryId().equals(TaskTypeConstant.POSTPONE)) {
                        //输入工单号

                        subinventoryTransferRecord.setTransferMemo("此处工单号"+"预测备料");
                    }else{
                        errMsg = "当前选择不符合规则，如需转至预测备料库，请选择合格或者延期物料";
                        throw new RuntimeException();
                    }
                }*/
                //保存转库记录
                subinventoryTransferRecordService.save(subinventoryTransferRecord);

            }
            requestIdService.subInventoryTransfer(requestIdAuto,requestIds,lists);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,errMsg);
        }
        return ResultGenerator.genSuccessResult();
    }


//    锁定释放库存
    @PostMapping("/lockOrRelease")
    @ResponseBody
    @Transactional
    public Result lockOrRelease(@RequestBody SubinventoryTransferRecordCriteria subinventoryTransferRecordCriteria) {
        String errMsg = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                errMsg = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            BoxItem boxItem = boxItemService.getBoxItemByBoxCode(subinventoryTransferRecordCriteria.getBoxCode());
            if(boxItem.getSubInventoryId().equals(TaskTypeConstant.POSTPONE) &&
                    TaskTypeConstant.workerOrderIssueTime.getDelayControl().equals(2)){
                errMsg = "当前箱延期管控中，请重新选择！";
                throw new RuntimeException();
            }
            if(boxItem.getSubInventoryId().equals(TaskTypeConstant.POSTPONE) || boxItem.getSubInventoryId().equals(TaskTypeConstant.QUALIFIED)){

            }else{
                errMsg = "请选择延期或合格子库库存进行出库！";
                throw new RuntimeException();
            }
            if(subinventoryTransferRecordCriteria.getLock().equals(1)){
                Integer canLockQuantity = boxItem.getQuantity() - boxItem.getForecastStockQuantity() - boxItem.getLockQuantity();
                if (canLockQuantity < subinventoryTransferRecordCriteria.getQuantity()) {
                    errMsg = "超过当前可锁定数量，当前可锁定数量为"+canLockQuantity+"张";
                    throw new RuntimeException();
                }else{
                    boxItem.setLockQuantity(boxItem.getLockQuantity()+subinventoryTransferRecordCriteria.getQuantity());
                }
            }
            else {
                if(subinventoryTransferRecordCriteria.getQuantity()>boxItem.getLockQuantity()){
                    errMsg = "超过当前可释放数量，当前可锁定数量为"+boxItem.getLockQuantity()+"张";
                    throw new RuntimeException();
                }else {
                    boxItem.setLockQuantity(boxItem.getLockQuantity() - subinventoryTransferRecordCriteria.getQuantity());
                }
            }
            boxItemService.update(boxItem);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE,errMsg);
        }
        return ResultGenerator.genSuccessResult();
    }

}

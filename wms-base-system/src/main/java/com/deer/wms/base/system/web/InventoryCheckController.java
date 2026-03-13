package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.box.*;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.*;
import com.deer.wms.base.system.model.threeDimensional.Task;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.CellInfoCriteria;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.service.*;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.GuidUtils;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* Created by  on 2019/12/31.
*/
@Controller
@RequestMapping("/inventoryCheck")
public class InventoryCheckController  extends BaseController{

    private String prefix = "manage/inventoryCheck";

    @Autowired
    private InventoryCheckService inventoryCheckService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private AccountAliasService accountAliasService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;
    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private IItemInfoService itemInfoService;
    @Autowired
    private SubInventoryService subInventoryService;
    @Autowired
    private PickTaskService pickTaskService;


    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("inventoryCheck:view")
    @GetMapping()
    public String inventoryCheck()
    {
        return prefix + "/inventoryCheck";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
    InventoryCheck inventoryCheck = inventoryCheckService.findById(id);
        mmap.put("inventoryCheck", inventoryCheck);
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
    public Result add(@RequestBody InventoryCheck inventoryCheck) {
        inventoryCheckService.save(inventoryCheck);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        inventoryCheckService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody InventoryCheck inventoryCheck) {
        inventoryCheckService.update(inventoryCheck);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        InventoryCheck inventoryCheck = inventoryCheckService.findById(id);
        return ResultGenerator.genSuccessResult(inventoryCheck);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(InventoryCheckCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<InventoryCheck> list = inventoryCheckService.findAll();
        return getDataTable(list);
    }

    @PostMapping("/findList")
    @ResponseBody
    public  TableDataInfo findList(InventoryCheckCriteria criteria) {
        startPage();
        List<InventoryCheckDto> list = inventoryCheckService.findList(criteria);
        return getDataTable(list);
    }

    //1-异常处理货位有货  2-异常处理货位无货
    private static int state = 2;
    private static String boxCode = "";
    private static int inventoryCheckId = 0;

    @PostMapping( "/wareHouseMove")
    @ResponseBody
    @Transactional
    public Result wareHouseMove(@RequestBody InventoryCheckCriteria criteria)
    {
        String error = "服务器内部错误，请联系管理员！";
        try {
            //出箱至指定异常处理货位
            if(criteria.getType().equals(1)) {
//                List<BoxInfoDto> boxInfoDtos = boxInfoService.findList(new BoxInfoCriteria(4));
//                if(boxInfoDtos.size()>0){
//                    error="锁定位有箱，请勿下发此任务";
//                    throw new RuntimeException();
//                }
                if(state == 1){
                    error="锁定位有箱，请勿下发此任务";
                    throw new RuntimeException();
                }
                BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(criteria.getBoxCode());
                boxInfo.setBoxState(3);
                boxInfoService.update(boxInfo);
                List<BoxItemDto> boxItemDtos = boxItemService.findListTwo(new BoxItemCriteria(criteria.getBoxCode()));
                BoxItemDto boxItemDto = boxItemDtos.get(0);
                InventoryCheck inventoryCheck = null;
                        TaskInfo taskInfo1 = taskInfoService.getTaskInfoByTaskId(criteria.getTaskId());
                if(!taskInfo1.getType().equals(TaskTypeConstant.CHECK_LEFT_BOX_TO_CELL_FROM_PAPERS_COUNTERS)) {
                    inventoryCheck = new InventoryCheck(boxItemDto.getBoxCode(), boxItemDto.getBoxCode(), boxItemDto.getQuantity(),
                            0, 0, TaskTypeConstant.MANUAL_CHECK,
                            TaskTypeConstant.RUNNING, null, null, null,
                            criteria.getLoginPersonCardNo());
                    inventoryCheck.setCreateTime(DateUtils.getTime());
                    inventoryCheckService.save(inventoryCheck);
                    inventoryCheckId = inventoryCheck.getInventoryCheckId();
                }
                CellInfo cellInfo = cellInfoService.findById(boxItemDto.getBoxCellId());
                cellInfo.setState(2);
                cellInfoService.update(cellInfo);
                TaskInfo taskInfo = new TaskInfo(new GuidUtils().toString(),
                        MyUtils.connectShelfNameAndRowAndColumn(boxItemDto.getShelfName(),boxItemDto.getsColumn(),boxItemDto.getsRow()),
                        TaskTypeConstant.EXCEPTION_MANAGE_CELL,TaskTypeConstant.CHECK_FROM_PAPER_COUNTERS,0,boxItemDto.getQuantity(),
                        boxItemDto.getBoxCode(),"0",criteria.getLoginPersonCardNo(), DateUtils.getTime());
                taskInfo.setInventoryCheckId(inventoryCheck.getInventoryCheckId());
                taskInfoService.save(taskInfo);
                state = 1;
                boxCode = boxInfo.getBoxCode();
            }
            //异常处理货位移动到原货位
            else if(criteria.getType().equals(2)){
                if(state == 2){
                    error="异常处理货位无货，请勿下发此任务";
                    throw new RuntimeException();
                }
                if(!boxCode.equals(criteria.getBoxCode())){
                    error= "回原位箱号与异常处理货位箱号不符，请重新选择！异常处理货位箱号为"+boxCode;
                    throw new RuntimeException();
                }
                List<BoxItemDto> boxItemDtos = boxItemService.findListTwo(new BoxItemCriteria(criteria.getBoxCode()));
                BoxItemDto boxItemDto = boxItemDtos.get(0);
                TaskInfo taskInfo = new TaskInfo(new GuidUtils().toString(), TaskTypeConstant.EXCEPTION_MANAGE_CELL,
                        MyUtils.connectShelfNameAndRowAndColumn(boxItemDto.getShelfName(),boxItemDto.getsColumn(),boxItemDto.getsRow()),
                        TaskTypeConstant.CHECK_FROM_PAPER_COUNTERS,0,boxItemDto.getQuantity(),
                        boxItemDto.getBoxCode(),"0",criteria.getLoginPersonCardNo(), DateUtils.getTime());
                taskInfo.setInventoryCheckId(inventoryCheckId);
                taskInfoService.save(taskInfo);
                state =2;
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping( "/inventoryCheck")
    @ResponseBody
    @Transactional
    public Result inventoryCheck(@RequestBody InventoryCheckCriteria criteria)
    {
        String error = "服务器内部错误，请联系管理员！";
        try {
            InventoryCheck inventoryCheck = inventoryCheckService.findById(criteria.getInventoryCheckId());
            if(inventoryCheck.getState().equals(TaskTypeConstant.FINISH)){
                error = "此单已盘点完成！";
                throw new RuntimeException();
            }
            //手动盘点
            if(criteria.getType().equals(TaskTypeConstant.MANUAL_CHECK)){
                ItemInfo itemInfo = itemInfoService.findByItemCode(criteria.getItemCode());
                if(itemInfo == null){
                    error = "当前料号不存在，请重新输入!";
                    throw new RuntimeException();
                }
                Integer quantity = criteria.getCheckQuantity()-inventoryCheck.getQuantity();
                inventoryCheck.setItemCode(criteria.getItemCode());
                inventoryCheck.setBatch(criteria.getBatch());
                inventoryCheck.setDispositionId(criteria.getDispositionId());
                inventoryCheck.setSubInventoryId(criteria.getSubInventoryId());
                inventoryCheck.setState(TaskTypeConstant.FINISH);
                inventoryCheck.setAfterCheckQuantity(criteria.getCheckQuantity());
                inventoryCheck.setCheckQuantity(quantity);
                inventoryCheck.setCommitTime(DateUtils.getTime());
                inventoryCheckService.update(inventoryCheck);
                if(quantity != 0) {
                    BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(inventoryCheck.getBoxCode());
                    BoxItem boxItem = boxItemService.getBoxItemByBoxCode(inventoryCheck.getBoxCode());
                    //盘亏
                    if (quantity < 0) {
                        boxInfo.setHasGoods(0);
                        boxItem.setQuantity(0);
                        boxItem.setLockQuantity(0);
                        boxItem.setForecastStockQuantity(0);
                        //1-已下发点数任务  2-未下发点数任务
                        List<PickTaskDto> pickTaskDtoOnes = pickTaskService.findList(new PickTaskCriteria(1, boxItem.getBoxCode(), TaskTypeConstant.WORKER_ORDER_OUT));
                        if (pickTaskDtoOnes.size() > 0) {
                            for (PickTaskDto pickTaskDto : pickTaskDtoOnes) {
                                pickTaskDto.setPickState(5);
                                pickTaskService.update(pickTaskDto);
                            }
                        }
                    }
                    //盘盈
                    else if (quantity > 0) {
                        boxInfo.setHasGoods(1);
                        boxItem.setQuantity(quantity);
                        boxItem.setInTime(DateUtils.getTime());
                        boxItem.setSubInventoryId(criteria.getSubInventoryId());
                    }
                    boxItemService.update(boxItem);
                    boxInfoService.update(boxInfo);
                    SubInventory subInventory = subInventoryService.findById(criteria.getSubInventoryId());
                    AccountAlias accountAlias = accountAliasService.findByDispositionId(criteria.getDispositionId());
                    RequestIdAuto accountAliasRequestIdAuto = requestIdAutoService.backAutoId("WMS手动盘点");
                    List<Map<String, String>> accountAliasParams = new ArrayList<>();
                    List<RequestId> requestIdsAccountAliasOut = new ArrayList<>();
                    accountAliasParams.add(MyUtils.accountAliasOut(quantity < 0 ? TaskTypeConstant.TRANSACTION_OUT : TaskTypeConstant.TRANSACTION_IN,
                            TaskTypeConstant.organizationId.toString(),
                            itemInfo.getInventoryItemId().toString(), subInventory.getSubInventoryCode(),
                            subInventory.getSlotting() == null ? null : subInventory.getSlotting(), accountAlias.getAccountAlias(),
                            accountAlias.getDispositionId().toString(), criteria.getBatch(), quantity.toString(),
                            MyUtils.getNinetySecondsAgo(), itemInfo.getUnit(), inventoryCheck.getInventoryCheckId().toString(),
                            inventoryCheck.getInventoryCheckId().toString()));

                    requestIdsAccountAliasOut.add(new RequestId(accountAliasRequestIdAuto.getRequestId(), "ERROR", itemInfo.getInventoryItemId(),
                            quantity, criteria.getBatch(), MyUtils.getNinetySecondsAgo(), "WMS请求EBS账户别名事务处理失败",
                            subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : Integer.parseInt(subInventory.getSlotting()),
                            TaskTypeConstant.organizationId, TaskTypeConstant.ACCOUNT_ALIAS, TaskTypeConstant.FAIL_WAIT_MANAGE,
                            quantity < 0 ? TaskTypeConstant.TRANSACTION_OUT : TaskTypeConstant.TRANSACTION_IN,
                            itemInfo.getUnit(), inventoryCheck.getInventoryCheckId(), inventoryCheck.getInventoryCheckId(),
                            accountAlias.getAccountAlias(), accountAlias.getDispositionId()));

                    requestIdService.accountAliasOut(accountAliasParams, requestIdsAccountAliasOut, accountAliasRequestIdAuto);
                }
            }
            //自动盘点
            else if(criteria.getType().equals(TaskTypeConstant.AUTO_CHECK)){
                if(inventoryCheck.getAfterCheckQuantity() <= 0){
                    error = "请等待盘点任务完成";
                    throw new RuntimeException();
                }
                //计算盘盈还是盘亏
                Integer quantity = (criteria.getCheckQuantity()+inventoryCheck.getAfterCheckQuantity())-inventoryCheck.getQuantity();
                inventoryCheck.setDispositionId(criteria.getDispositionId());
                inventoryCheck.setState(TaskTypeConstant.FINISH);
                inventoryCheck.setCheckQuantity(quantity);
                inventoryCheck.setAfterCheckQuantity(criteria.getCheckQuantity()+inventoryCheck.getAfterCheckQuantity());
                inventoryCheck.setCommitTime(DateUtils.getTime());
                inventoryCheckService.update(inventoryCheck);
                if(quantity != 0) {
                    //盘盈
                    if (quantity > 0) {
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(inventoryCheck.getBoxCode());
                        BoxItem boxItem = boxItemService.getBoxItemByBoxCode(inventoryCheck.getBoxCode());
                        boxInfo.setHasGoods(1);
                        boxItem.setQuantity(quantity);
                        boxItem.setInTime(DateUtils.getTime());
                        boxItem.setSubInventoryId(inventoryCheck.getSubInventoryId());
                        boxItemService.update(boxItem);
                        boxInfoService.update(boxInfo);
                    }
                    ItemInfo itemInfo = itemInfoService.findByItemCode(inventoryCheck.getItemCode());
                    SubInventory subInventory = subInventoryService.findById(inventoryCheck.getSubInventoryId());
                    AccountAlias accountAlias = accountAliasService.findByDispositionId(criteria.getDispositionId());
                    RequestIdAuto accountAliasRequestIdAuto = requestIdAutoService.backAutoId("WMS手动盘点");
                    List<Map<String, String>> accountAliasParams = new ArrayList<>();
                    List<RequestId> requestIdsAccountAliasOut = new ArrayList<>();
                    accountAliasParams.add(MyUtils.accountAliasOut(quantity < 0 ? TaskTypeConstant.TRANSACTION_OUT : TaskTypeConstant.TRANSACTION_IN,
                            TaskTypeConstant.organizationId.toString(),
                            itemInfo.getInventoryItemId().toString(), subInventory.getSubInventoryCode(),
                            subInventory.getSlotting() == null ? null : subInventory.getSlotting(), accountAlias.getAccountAlias(),
                            accountAlias.getDispositionId().toString(), inventoryCheck.getBatch(), quantity.toString(),
                            MyUtils.getNinetySecondsAgo(), itemInfo.getUnit(), inventoryCheck.getInventoryCheckId().toString(),
                            inventoryCheck.getInventoryCheckId().toString()));

                    requestIdsAccountAliasOut.add(new RequestId(accountAliasRequestIdAuto.getRequestId(), "ERROR", itemInfo.getInventoryItemId(),
                            quantity, inventoryCheck.getBatch(), MyUtils.getNinetySecondsAgo(), "WMS请求EBS账户别名事务处理失败",
                            subInventory.getSubInventoryCode(), subInventory.getSlotting() == null ? null : Integer.parseInt(subInventory.getSlotting()),
                            TaskTypeConstant.organizationId, TaskTypeConstant.ACCOUNT_ALIAS, TaskTypeConstant.FAIL_WAIT_MANAGE,
                            quantity < 0 ? TaskTypeConstant.TRANSACTION_OUT : TaskTypeConstant.TRANSACTION_IN,
                            itemInfo.getUnit(), inventoryCheck.getInventoryCheckId(), inventoryCheck.getInventoryCheckId(),
                            accountAlias.getAccountAlias(), accountAlias.getDispositionId()));

                    requestIdService.accountAliasOut(accountAliasParams, requestIdsAccountAliasOut, accountAliasRequestIdAuto);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping( "/sendInventoryCheck")
    @ResponseBody
    @Transactional
    public Result sendInventoryCheck(@RequestBody InventoryCheckCriteria criteria)
    {
        String error = "服务器内部错误，请联系管理员！";
        try {
            List<Task> taskInfos = taskInfoService.findByStateAndType(new TaskInfoCriteria());
            if(taskInfos.size()>0){
                error="当前有任务执行中，请勿下发自动盘点任务！";
                throw new RuntimeException();
            }else {
                List<BoxItemDto> boxItemDtos = boxItemService.findList(new BoxItemCriteria(criteria.getBoxCode()));
                if (boxItemDtos.size() > 0) {
                    BoxItemDto boxItemDto = boxItemDtos.get(0);
                    if(boxItemDto.getQuantity()<=0){
                        error = "当前箱无物料，请勿盘点或手动盘点";
                        throw new RuntimeException();
                    }
                    if(boxItemDto.getWorkOrderStockState().equals(1)){
                        error = "当前箱已工单锁定，请勿盘点";
                        throw new RuntimeException();
                    }
                    if(boxItemDto.getLockQuantity() > 0){
                        error = "当前箱物料部分锁定，请勿盘点";
                        throw new RuntimeException();
                    }
                    if(!boxItemDto.getBoxState().equals(1)){
                        error = "当前箱任务中，请勿盘点";
                        throw new RuntimeException();
                    }
                    BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxItemDto.getBoxCode());
                    boxInfo.setBoxState(2);
                    boxInfoService.update(boxInfo);
                    CellInfo cellInfo = cellInfoService.findById(boxInfo.getBoxCellId());
                    cellInfo.setState(2);
                    cellInfoService.update(cellInfo);
                    //出空箱
                    CellInfoCriteria cellInfoCriteria = new CellInfoCriteria();
                    cellInfoCriteria.setTypeAndState(1002);
                    List<CellInfoDto> cellInfoDtos = cellInfoService.findList(cellInfoCriteria);
                    if(cellInfoDtos.size()>0) {
                        CellInfoDto cellInfoDto1 = cellInfoDtos.get(0);
                        cellInfoDto1.setState(2);
                        cellInfoService.update(cellInfoDto1);
                        BoxInfo boxInfo1 = boxInfoService.getBoxInfoByBoxCode(cellInfoDto1.getBoxCode());
                        boxInfo1.setBoxState(2);
                        boxInfoService.update(boxInfo1);
                        //写入盘点单
                        InventoryCheck inventoryCheck = new InventoryCheck(boxItemDto.getBoxCode(),cellInfoDto1.getBoxCode(),boxItemDto.getQuantity(),
                                0,0 ,TaskTypeConstant.AUTO_CHECK,
                                TaskTypeConstant.RUNNING,boxItemDto.getItemCode(),boxItemDto.getBatch(),null,
                                criteria.getLoginPersonCardNo());
                        inventoryCheck.setSubInventoryId(boxItemDto.getSubInventoryId());
                        inventoryCheck.setCreateTime(DateUtils.getTime());
                        inventoryCheckService.save(inventoryCheck);
                        //下发出半箱到点数机
                        TaskInfo taskInfo = new TaskInfo(new GuidUtils().toString(),
                                MyUtils.connectShelfNameAndRowAndColumn(boxItemDto.getShelfName(),boxItemDto.getsColumn(),boxItemDto.getsRow()),
                                "120",TaskTypeConstant.CHECK_CELL_TO_PAPER_COUNTERS_LEFT,0,boxItemDto.getQuantity(),boxItemDto.getBoxCode(),
                                "0",criteria.getLoginPersonCardNo(),DateUtils.getTime());
                        taskInfo.setInventoryCheckId(inventoryCheck.getInventoryCheckId());
                        taskInfoService.save(taskInfo);
                        //下发出空箱到点数机右边
                        TaskInfo taskInfo1 = new TaskInfo(new GuidUtils().toString(),
                                MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto1.getShelfName(),cellInfoDto1.getSColumn(),cellInfoDto1.getSRow()),
                                "128", TaskTypeConstant.CHECK_NULL_BOX_TO_PAPER_COUNTERS_RIGHT,0,0, boxInfo1.getBoxCode(),
                                "1",criteria.getLoginPersonCardNo(),DateUtils.getTime());
                        taskInfo1.setInventoryCheckId(inventoryCheck.getInventoryCheckId());
                        taskInfoService.save(taskInfo1);
                    }else{
                        error = "无空箱可自动盘点！";
                        throw new RuntimeException();
                    }
                } else {
                    error = "系统中无此箱号";
                    throw new RuntimeException();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

}

package com.deer.wms.base.system.web.ware;

import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxInfoDto;
import com.deer.wms.base.system.model.box.BoxItemCriteria;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.task.TaskInfoCriteria;
import com.deer.wms.base.system.model.task.TaskInfoDto;
import com.deer.wms.base.system.model.threeDimensional.*;
import com.deer.wms.base.system.model.ware.*;
import com.deer.wms.base.system.service.BillInRecordService;
import com.deer.wms.base.system.service.WarnInformationService;
import com.deer.wms.base.system.service.bill.IBillInDetailService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.base.system.service.ware.IShelfInfoService;
import com.deer.wms.base.system.service.ware.IWareInfoService;
import com.deer.wms.base.system.web.WarnInformationController;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.constant.Constants;
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
import com.sun.javafx.collections.MappingChange;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.management.RuntimeErrorException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 货位设置 信息操作处理
 *
 * @author deer
 * @date 2019-05-08
 */
@Api("货位查询")
@Controller
@RequestMapping("/system/cellInfo")
public class CellInfoController extends BaseController {
    private String prefix = "system/cellInfo";

    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private IShelfInfoService shelfInfoService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private WarnInformationService warnInformationService;
    @Autowired
    private PickTaskService pickTaskService;
    @Autowired
    private BillInRecordService billInRecordService;
    @Autowired
    private IBillInDetailService billInDetailService;
    @Autowired
    private IBillOutMasterService billOutMasterService;

    /**
     * 生成任务:根据托盘编码 ， 将托盘放置空货位上（任务4）
     * @param boxCode
     * @param billInDetailId
     * @return
     */
    //TODO url  注解
    public Result getBoxInfoToFreeCellInfoOfTaskInfo(String boxCode, Integer billInDetailId) {
        //查询没有托盘的货位  货位表state状态为0，  排序取第一个
        CellInfo cellInfo = cellInfoService.getCellInfoHasNoBoxInfo();
        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxCode);
        if (cellInfo != null && boxInfo != null) {
            int shellfId = cellInfo.getShelfId();
            ShelfInfo shelfInfo = shelfInfoService.selectShelfInfoById(shellfId);
            TaskInfo taskInfo = new TaskInfo();
            String start = "108";
            String end = shelfInfo.getShelfName() + ":" + cellInfo.getSColumn() + ":" + cellInfo.getSRow();
            String newEnd = cellInfoService.toStringForWcs(end);
            taskInfo.setId(null);
            taskInfo.setTaskId(new GuidUtils().toString());
            taskInfo.setStartPosition(start);
            taskInfo.setEndPosition(newEnd);
            taskInfo.setBoxCode(boxCode);
            taskInfo.setBillInDetailId(billInDetailId);
            taskInfo.setType(4);//根据托盘编码 ， 将托盘放置空货位上
            taskInfoService.save(taskInfo);
            //设置托盘状态为2，锁定状态
            boxInfo.setBoxState(2);
        }
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("根据货物编码查询出所有在货位上的托盘中的货物信息")
    @ResponseBody
    @RequestMapping("/getBoxItemInCellInfoByItemCode")
    public Result getBoxItemInCellInfoByItemCode(String itemCode){
        List<BoxItemDto> boxItemDtos = boxItemService.getBoxItemDtoByitemCode(itemCode);
        return  ResultGenerator.genSuccessResult(boxItemDtos);
    }

//    /**
//     * 根据货物编码  以及需要出库的数量，生成需要的任务信息
//     * @param itemCode
//     * @param quantity
//     * @return
//     */
//    //todo 注解URL
//    public Result getFullCellInfoForOutOfTaskinfo(String itemCode, Double quantity, Integer billInDetailId) {
//        List<BoxItemDto> boxItemDtos = boxItemService.getFullCellInfoForOutOfStockForSaveTaskInfo(itemCode, quantity);
//        if (boxItemDtos != null) {
//            TaskInfo task = new TaskInfo();
//            for(BoxItemDto boxItemDto : boxItemDtos) {
//                String start = boxItemDto.getShelfName() + ":" + boxItemDto.getsRow() + ":" + boxItemDto.getsColumn();
//                String end = "0,0";
//                task.setId(null);
//                task.setTaskId(new GuidUtils().toString());
//                task.setStartPosition(start);
//                task.setEndPosition(end);
//                task.setBoxCode(boxItemDto.getBoxCode());
//                task.setBillInDetailId(billInDetailId);
//                //3-出库任务(根据货物编码  以及需要出库的数量，生成需要的N条任务信息，将合适的托盘移到固定位置/卸货位置)
//                task.setType(3);
//                taskInfoService.save(task);
//            }
//        }
//        return ResultGenerator.genSuccessResult();
//    }

    /**
     * 入库任务， 当托盘已经到达装货点，装货完毕后   调用此方法寻找合适的货位返回  生成任务（任务2）
     *
     * @return
     */
    //TODO 写url  当托盘到达装货点并装货完毕后，调用此接口
    public Result getFreeCellInfoForBackOfTaskInfo(Integer billInDetailId) {
        BoxInfoDto boxInfoDto = boxInfoService.getFreeCellInfoForBack();
        if (boxInfoDto != null) {
            TaskInfo taskInfo = new TaskInfo();
            String end = boxInfoDto.getShelfName() + ":" + boxInfoDto.getsColumn() + ":" + boxInfoDto.getsRow();
            String start = "108";

            String newEnd = cellInfoService.toStringForWcs(end);

            taskInfo.setId(null);
            taskInfo.setTaskId(new GuidUtils().toString());
            taskInfo.setStartPosition(start);
            taskInfo.setEndPosition(newEnd);
            taskInfo.setBoxCode(boxInfoDto.getBoxCode());
            taskInfo.setBillInDetailId(billInDetailId);
            taskInfo.setType(2);//2-入库任务(当托盘已经到达装货点，装货完毕后   寻找合适的货位返回)
            taskInfoService.save(taskInfo);
            //设置托盘状态为2，锁定状态
            BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxInfoDto.getBoxCode());
            boxInfo.setBoxState(2);
        }
        return ResultGenerator.genSuccessResult();
    }

    @ApiOperation("页面遍历货位信息")
    @ResponseBody
    @RequestMapping("/findCellInfoDto")
    public List<CellInfoDto> findCellInfoDto
            (String itemName, String itemCode, String batch) {
        List<CellInfoDto> cellInfoDtos =
                cellInfoService.findCellInfoDtoByItemNameAndItemCodeAndBatch(itemName, itemCode, batch);
        return cellInfoDtos;
    }

    /**
     * 保存任务信息（任务1）
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveTaskinfo")
    public AjaxResult saveTaskinfo(@RequestBody Map map) {
        if (map != null) {
            Map boxInfoDto = (Map) map.get("boxInfoDto");
            Integer billDetailId = (Integer) map.get("billDetailId");
            String isTop = (String) map.get("isTop");
            if (boxInfoDto != null) {
                Integer cellId = (Integer) boxInfoDto.get("boxCellId");
                String shelfName = (String) boxInfoDto.get("shelfName");
                Integer sRow = (Integer) boxInfoDto.get("sRow");
                Integer sColumn = (Integer) boxInfoDto.get("sColumn");
                String boxCode = (String) boxInfoDto.get("boxCode");
                String start = shelfName + ":" + sColumn + ":" + sRow;
                String newStart = cellInfoService.toStringForWcs(start);

                String end = "104";
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setId(null);
                taskInfo.setTaskId(new GuidUtils().toString());
                taskInfo.setStartPosition(newStart);
                taskInfo.setEndPosition(end);
                taskInfo.setBoxCode(boxCode);
                taskInfo.setBillInDetailId(billDetailId);
                taskInfo.setIsTop(isTop);
                taskInfo.setType(1);//1-入库任务(将空/半空托盘从货位上移到入库口)
                taskInfoService.insertTaskInfo(taskInfo);

                //设置托盘状态为2，锁定状态(任务中)
                BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxCode);
                boxInfo.setBoxState(2);
                boxInfoService.update(boxInfo);

                CellInfo cellInfo = cellInfoService.findBy("cellId",cellId);
                cellInfo.setState(1);
                cellInfoService.update(cellInfo);
            }
        }
        return success();
    }

    /**
     * 查询为了任务而生成的托盘信息
     * @param itemCode 物料编码
     * @param batch    批次
     * @param quantity 数量
     * @return
     */
    //@RequiresPermissions("system:cellInfo:getTaskinfo")
    @ResponseBody
    @RequestMapping("/findBoxInfoForBillIn")   //("/saveTaskinfo")
    public BoxInfoDto findBoxInfoForBillIn(String itemCode, String batch, Double quantity) {
        BoxInfoDto boxInfoDto = null;
        //TODO
        //入半框
        if (1 == 2) {
            boxInfoDto = boxInfoService.getHalfCellInfoForBillIn(itemCode, batch, quantity);
        }
        //入空框
        if (1 == 1) {
            boxInfoDto = boxInfoService.getFreeCellInfoForBillIn();
        }
        return boxInfoDto;
    }

    /**
     *  根据货架ID查询所有货位
     * @param shelfId
     * @return
     */
    @RequiresPermissions("system:cellInfo:selectCellInfoByShelfId")
    @PostMapping("/selectCellInfoByShelfId")
    @ResponseBody
    public List<CellInfo> selectCellInfoByShelfId(Integer shelfId){
        List<CellInfo> cellInfos = cellInfoService.selectCellInfoByShelfId(shelfId);
        return cellInfos;
    }

    /**
     * @param areaId
     * @return
     */
//    @RequiresPermissions("system:cellInfo:findcellList")
    @PostMapping("/findcellList")
    @ResponseBody
    public List<List<CellInfo>> getCellListByAreaId(Integer areaId) {
        List<List<CellInfo>> cellLists = new ArrayList<List<CellInfo>>();
        List<ShelfInfo> shelfInfos = shelfInfoService.selectShelfInfoByAreaId(areaId);
        for (ShelfInfo shelfInfo : shelfInfos) {
            List<CellInfo> cellInfos = cellInfoService.selectCellInfoByShelfId(shelfInfo.getShelfId());
            cellLists.add(cellInfos);
        }
           /* List<CellInfo> lists = cellInfoService.selectCellInfoListByAreaId(areaId);*/
        return cellLists;
    }

    @RequiresPermissions("system:cellInfo:view")
    @GetMapping()
    public String cellInfo() {
        return prefix + "/cellInfo";
    }

    /**
     * 查询货位设置列表
     */
    @RequiresPermissions("system:cellInfo:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(CellInfo cellInfo) {
        startPage();
        List<CellInfo> list = cellInfoService.selectCellInfoList(cellInfo);
        return getDataTable(list);
    }

    @PostMapping("/findList")
    @ResponseBody
    public Result findList(CellInfo cellInfo) {
        List<CellInfoDto> list = cellInfoService.findListTwo();
        return ResultGenerator.genSuccessResult(list);
    }

    /**
     * 导出货位设置列表
     */
    @RequiresPermissions("system:cellInfo:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(CellInfo cellInfo) {
        List<CellInfo> list = cellInfoService.selectCellInfoList(cellInfo);
        ExcelUtil<CellInfo> util = new ExcelUtil<CellInfo>(CellInfo.class);
        return util.exportExcel(list, "cellInfo");
    }

    /**
     * 新增货位设置
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存货位设置
     */
    @RequiresPermissions("system:cellInfo:add")
    @Log(title = "货位设置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(CellInfo cellInfo) {
        return toAjax(cellInfoService.insertCellInfo(cellInfo));
    }

    /**
     * 修改货位设置
     */
    @GetMapping("/edit/{cellId}")
    public String edit(@PathVariable("cellId") Integer cellId, ModelMap mmap) {
        CellInfo cellInfo = cellInfoService.selectCellInfoById(cellId);
        mmap.put("cellInfo", cellInfo);
        return prefix + "/edit";
    }

    /**
     * 修改保存货位设置
     */
    @RequiresPermissions("system:cellInfo:edit")
    @Log(title = "货位设置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(CellInfo cellInfo) {
        return toAjax(cellInfoService.updateCellInfo(cellInfo));
    }

    /**
     * 删除货位设置
     */
    @RequiresPermissions("system:cellInfo:remove")
    @Log(title = "货位设置", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(cellInfoService.deleteCellInfoByIds(ids));
    }

    private  DecimalFormat df = new DecimalFormat("0.00%");
    /**
     * 外墙大屏显示数据
     *
     * @return
     */
    @GetMapping("/screenShow")
    @ResponseBody
    public Result screenShow(@Param("type") Integer type) {
        try {
            Map<String,Object> map = new HashMap<>();
            WarnInformationCriteria warnInformationCriteria= new WarnInformationCriteria();
            TaskInfoCriteria taskInfoCriteria = new TaskInfoCriteria();
            WareInfo wareInfo = TaskTypeConstant.wareInfo;
            int totalCell = cellInfoService.count();
//            Integer availableCell = cellInfoService.available();
            int notItemCell = cellInfoService.notItemCell();
            double a = (double)(totalCell-notItemCell)/(double)totalCell;
            String b = df.format(a);
            System.out.println(b);
            //货位利用率
            map.put("cellOccupyRatio", b);
            //剩余货位
            map.put("notItemCell", notItemCell);
            //总货位
            map.put("totalCell", totalCell);
            //已用货位有货（库值）
            map.put("availableCell", totalCell-notItemCell);//
            //爆仓预警
            map.put("expectedWaring", wareInfo.getExpectedWaring());
            //爆仓报警
            map.put("alarm", wareInfo.getAlarm());
            //报警信息
            warnInformationCriteria.setPageNum(1);
            warnInformationCriteria.setPageSize(10);
            warnInformationCriteria.setState(1);
            List<WarnInformationDto> warnInformations = warnInformationService.findList(warnInformationCriteria);
            map.put("warnInformation",warnInformations);
            //入库展示
            List<TaskInfoDto> taskInfoDtos = null;
            if(type == 1) {
                taskInfoCriteria.setPageNum(1);
                taskInfoCriteria.setPageSize(10);
                taskInfoCriteria.setType(TaskTypeConstant.IN_AVAILABLE_BOX);
                taskInfoDtos = taskInfoService.findByType(taskInfoCriteria);
            }
            //出库展示
            else if (type == 2) {
                taskInfoCriteria.setPageNum(1);
                taskInfoCriteria.setPageSize(10);
                taskInfoCriteria.setType(TaskTypeConstant.COUNT_TO_CARRIER);
                taskInfoDtos = taskInfoService.findByType(taskInfoCriteria);
            }
            //出库或者入库任务信息
            map.put("taskInformation",taskInfoDtos);
            return ResultGenerator.genSuccessResult(map);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }finally{
        }
    }
    @GetMapping("/totalIn")
    @ResponseBody
    public Result totalIn(@Param("type") Integer type) {
        try {
            Map<String,Object> map1 = new HashMap<>();
            //库存周转天数
            map1.put("turnoverDays",pickTaskService.totalSevenQuantity());
            //统计前七天入库信息
            if(type == 1) {
                map1.put("totalSevenDays", taskInfoService.totalSevenDays());
            }
            return ResultGenerator.genSuccessResult(map1);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }



    @GetMapping("/cellOccupyRatio")
    @ResponseBody
    public Result cellOccupyRatio() {
        try {
            MainTotal mainTotal = new MainTotal();
            Integer totalCell = cellInfoService.count();
            Integer notItemCell = cellInfoService.notItemCell();
            double a = (double)(totalCell-notItemCell)/(double)totalCell;
            mainTotal.setCellOccupyRatio(a);
            mainTotal.setTotalCell(totalCell);
            mainTotal.setNotItemCell(notItemCell);
            mainTotal.setTotalIn(billInDetailService.totalAll());
            mainTotal.setTotalNoIn(billInDetailService.totalNoFinish());
            mainTotal.setTotalOut(billOutMasterService.totalAll());
            mainTotal.setTotalNoOut(billOutMasterService.totalNoFinish());
            return ResultGenerator.genSuccessResult(mainTotal);
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }finally{
        }
    }

    private  ResultData resultData = new ResultData();

    @GetMapping("/threeDimensional")
    @ResponseBody
    public Result threeDimensional(@Param("type") Integer type,@Param("warnId") Integer warnId,
                                   @Param("pageSize") Integer pageSize,@Param("pageNum") Integer pageNum,
                                   @Param("warnIds") Integer[] warnIds, @Param("boxCode")String boxCode) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if(type != null) {
                resultData.setData(null);
                resultData.setTotal(null);
                //日入库
                if (type == 1) {
                    totalIn(type,pageSize,pageNum);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                //日出库
                else if (type == 2) {
                    totalPickTask(type,pageSize,pageNum);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                //查找所有有货货位
                else if (type == 3) {
                    List<Cell> cells = cellInfoService.findStateEqualsOne();
                    return ResultGenerator.genSuccessResult(cells);
                }
                //根据货位Id查询此箱信息
                else if (type == 4) {
                    if(boxCode == null || boxCode.equals("")){
                        error = "请传箱号";
                        throw new RuntimeException();
                    }
                    BoxItemCriteria boxItemCriteria = new BoxItemCriteria();
                    boxItemCriteria.setPageNum(pageNum);
                    boxItemCriteria.setPageSize(pageSize);
                    boxItemCriteria.setBoxCode(boxCode);
                    List<Box> boxs = boxItemService.findByCellId(boxItemCriteria);
                    return ResultGenerator.genSuccessResult(boxs);
                }
                //查询入库数据
                else if (type == 5) {
                    totalIn(type,pageSize,pageNum);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                else if (type == 6) {
                    totalPickTask(type,pageSize,pageNum);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                //查询正在执行任务
                else if (type == 7) {
                    totalTaskInfo(pageSize,pageNum);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                //查询报警信息
                else if (type == 8) {
                    totalWarnInformation(pageSize,pageNum);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                //处理报警
                else if (type == 9) {
                    if(warnId == null){
                        error = "缺少报警信息Id！";
                        throw new RuntimeException();
                    }
                    WarnInformation warnInformation = warnInformationService.findById(warnId);
                    if(!warnInformation.getState().equals(TaskTypeConstant.UNDEALT)){
                        error = "只能处理未处理异常，请勿勾选已处理异常！";
                        throw new RuntimeException();
                    }
                    warnInformation.setFinishTime(DateUtils.getTime());
                    warnInformation.setState(TaskTypeConstant.ALREADY_MANGE_ALARM);
                    warnInformationService.update(warnInformation);
                    return ResultGenerator.genSuccessResult();
                }
                //查询可用货位及货位利用率
                else if(type == 10){
                    MainTotal mainTotal = new MainTotal();
                    Integer totalCell = cellInfoService.count();
                    Integer notItemCell = cellInfoService.notItemCell();
                    double a = (double)(totalCell-notItemCell)/(double)totalCell;
                    mainTotal.setCellOccupyRatio(a);
                    mainTotal.setTotalCell(totalCell);
                    mainTotal.setNotItemCell(notItemCell);
                    setResultData(mainTotal,1);
                    return ResultGenerator.genSuccessResult(resultData);
                }
                //批量处理报警信息
                else if(type == 11){
                    WarnInformationCriteria warnInformationCriteria= new WarnInformationCriteria();
                    warnInformationCriteria.setPageNum(null);
                    warnInformationCriteria.setPageSize(null);
                    warnInformationCriteria.setState(null);
                    warnInformationCriteria.setIds(Arrays.asList(warnIds));
                    List<WarnInformationDto> warnInformationDtos = warnInformationService.findList(warnInformationCriteria);
                    for(WarnInformationDto warnInformationDto : warnInformationDtos){
                        warnInformationDto.setFinishTime(DateUtils.getTime());
                        warnInformationDto.setState(TaskTypeConstant.ALREADY_MANGE_ALARM);
                        warnInformationService.update(warnInformationDto);
                    }
                    return ResultGenerator.genSuccessResult();
                }
            }
            else{
                error = "请传类型！";
                throw new RuntimeException();
            }
            return ResultGenerator.genSuccessResult();
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }finally{
        }
    }

    private void setResultData(Object data,Integer total){
        resultData.setTotal(total);
        resultData.setData(data);
    }

    private void totalIn(Integer type,Integer pageSize,Integer pageNum){
        BillInRecordCriteria billInRecordCriteria = new BillInRecordCriteria();
        billInRecordCriteria.setPageNum(pageNum);
        billInRecordCriteria.setPageSize(pageSize);
        billInRecordCriteria.setOrderParam(type);
        billInRecordCriteria.setState(null);
        List<InTotal> inTotals = billInRecordService.findToday(billInRecordCriteria);
        billInRecordCriteria.setPageNum(null);
        billInRecordCriteria.setPageSize(null);
        billInRecordCriteria.setState(1);
        setResultData(inTotals,billInRecordService.findToday(billInRecordCriteria).get(0).getBillId());
    }

    private void totalPickTask(Integer type,Integer pageSize,Integer pageNum){
        PickTaskCriteria pickTaskCriteria = new PickTaskCriteria();
        pickTaskCriteria.setPageNum(pageNum);
        pickTaskCriteria.setPageSize(pageSize);
        pickTaskCriteria.setPickType(type);
        pickTaskCriteria.setState(null);
        List<OutTotal> outTotals = pickTaskService.selectList(pickTaskCriteria);
        pickTaskCriteria.setPageNum(null);
        pickTaskCriteria.setPageSize(null);
        pickTaskCriteria.setState(1);
        setResultData(outTotals,pickTaskService.selectList(pickTaskCriteria).get(0).getBillId());
    }

    private void totalTaskInfo(Integer pageSize,Integer pageNum){
        TaskInfoCriteria taskInfoCriteria = new TaskInfoCriteria();
        taskInfoCriteria.setPageSize(pageSize);
        taskInfoCriteria.setPageNum(pageNum);
        taskInfoCriteria.setWhetherNullBox(null);
        List<Task> tasks = taskInfoService.findByStateAndType(taskInfoCriteria);
        taskInfoCriteria.setPageSize(null);
        taskInfoCriteria.setPageNum(null);
        taskInfoCriteria.setWhetherNullBox(1);
        setResultData(tasks,taskInfoService.findByStateAndType(taskInfoCriteria).get(0).getQuantity());
    }

    private void totalWarnInformation(Integer pageSize,Integer pageNum){
        WarnInformationCriteria warnInformationCriteria= new WarnInformationCriteria();
        warnInformationCriteria.setIds(null);
        warnInformationCriteria.setPageNum(pageNum);
        warnInformationCriteria.setPageSize(pageSize);
        warnInformationCriteria.setState(null);
        List<Warn> warnInformation = warnInformationService.findUntreated(warnInformationCriteria);
        warnInformationCriteria.setPageNum(pageNum);
        warnInformationCriteria.setPageSize(pageSize);
        warnInformationCriteria.setState(1);
        setResultData(warnInformation,warnInformationService.findUntreated(warnInformationCriteria).get(0).getWarnId());
    }
}

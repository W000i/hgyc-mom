package com.deer.wms.base.system.web.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.bill.BillOutDetail;
import com.deer.wms.base.system.model.bill.BillOutMaster;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemCriteria;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.item.BaseQueryParams2;
import com.deer.wms.base.system.model.item.Body2;
import com.deer.wms.base.system.model.task.*;
import com.deer.wms.base.system.model.threeDimensional.Task;
import com.deer.wms.base.system.model.ware.*;
import com.deer.wms.base.system.service.*;
import com.deer.wms.base.system.service.MESWebService.WebserviceResponse;
import com.deer.wms.base.system.service.bill.IBillInDetailService;
import com.deer.wms.base.system.service.bill.IBillInMasterService;
import com.deer.wms.base.system.service.bill.IBillOutDetailService;
import com.deer.wms.base.system.service.bill.IBillOutMasterService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.mailServer.MailService;
import com.deer.wms.base.system.service.rabbitMQ.MsgProducer;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.task.PickTaskService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.base.system.service.ware.IDoorService;
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
import com.deer.wms.system.service.ISysUserService;
import io.swagger.annotations.ApiOperation;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 任务 信息操作处理
 *
 * @author guo
 * @date 2019-06-03
 */
@Controller
@RequestMapping("/in/taskInfo")
public class TaskInfoController extends BaseController {
    private String prefix = "task";

    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private IDoorService doorService;
    @Autowired
    private IBillOutDetailService billOutDetailService;
    @Autowired
    private IBillOutMasterService billOutMasterService;
    @Autowired
    private CarrierService carrierService;
    @Autowired
    private PickTaskService pickTaskService;
    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;

    @Autowired
    private InventoryCheckService inventoryCheckService;
    @Autowired
    private WarnInformationService warnInformationService;
    @Value("${deer.profile}")
    private String from;

    @Autowired
    private CallAgvService callAgvService;

     /**
     * 用于接收WCS执行任务后返回的信息  并更新任务表中的状态值
     *
     * @param taskId 任务id
     * @param state  任务完成状态 2-报错   3-完成
     * @return
     */
    @ApiOperation("给WCS完成任务后回调此接口并传回任务id与任务状态")
    @ResponseBody
    @GetMapping("/getWcsMessage")
    public Result getWcsMessage(String taskId, Integer state) throws Exception{
           /* MThread myThread = new MThread();
            myThread.start();*/
           TaskInfo taskInfo = new TaskInfo();
           taskInfo.setBillInDetailId(1);
            taskInfo.setBarCode(12321+"");
            String a = "dasfa";
        return ResultGenerator.genSuccessResult();
    }

    private static TaskInfo taskInfo = new TaskInfo();

    class  MThread extends Thread{
        public void run(){
            synchronized(this) {
                int i = 0;
                try {
                    while (!isInterrupted()) {
                        if (i == 1000) {
                            interrupt();
                        }
                        String a = "12321421";
                        Thread.sleep(1000);
                        i++;
                        System.out.println(i);
                    }
                }catch(InterruptedException e){
                    Thread.currentThread().isInterrupted();
                }finally{
                    System.out.println("憨憨");
                }
            }
        }
    }


    @ApiOperation("给WCS获得任务列表")
    @ResponseBody
    @GetMapping("/getTaskinfoForWcs")
    @Async
    public List<TaskInfoWcs> selectTaskInfoForWcs() {
//        List<TaskInfoWcs> taskInfoWcs = null;
        try {
//            taskInfoWcs = taskInfoService.selectTaskInfoForWcsByState();
        /*List<TaskInfoWcs> taskInfoWcss = null;
        if (taskInfos != null) {
            taskInfoWcss = new ArrayList<>();
            TaskInfoWcs taskInfoWcs = null;
            for (TaskInfo taskInfo : taskInfos) {
                taskInfoWcs = new TaskInfoWcs();
                //taskInfoService.updateTaskInfo(task);
                taskInfoWcs.setTaskNo(taskInfo.getTaskId());
                taskInfoWcs.setFromStation(taskInfo.getStartPosition());
                taskInfoWcs.setToStation(taskInfo.getEndPosition());
                taskInfoWcs.setType(taskInfo.getType().toString());
                taskInfoWcs.setState(taskInfo.getState().toString());
                taskInfoWcs.setLevel(taskInfo.getIsTop());
                taskInfoWcs.setBarcode(taskInfo.getBarCode());
                taskInfoWcs.setNumber(taskInfo.getQuantity());
                taskInfoWcs.setCreateTime(DateUtils.getTime());
                taskInfoWcss.add(taskInfoWcs);
            }
        }*/

            return taskInfoService.selectTaskInfoForWcsByState();

        }catch(Exception e){

        }finally{
//            taskInfoWcs = null;
        }
        return null;
    }

    @ApiOperation("方便在出库单页面遍历任务列表")
    @ResponseBody
    @RequestMapping("/getTaskinfoByBillOutMasterId")
    public Result selectTaskinfoByBillOutMasterId(Integer billId) {

        List<TaskInfo> taskInfos = taskInfoService.selectTaskInfoByBillOutMasterId(billId);

        return ResultGenerator.genSuccessResult(taskInfos);
    }

    @ApiOperation("方便在入库单页面遍历任务列表")
    @ResponseBody
    @RequestMapping("/getTaskinfoByBillInMasterId")
    public Result selectTaskinfoByBillInMasterId(Integer billId) {
        List<TaskInfo> taskInfos = taskInfoService.selectTaskInfoByBillInMasterId(billId);
        return ResultGenerator.genSuccessResult(taskInfos);
    }

    @RequiresPermissions("in:task:view")
    @GetMapping()
    public String taskInfo() {
        return prefix + "/taskInfo";
    }

    //任务管理界面
    @RequiresPermissions("manage:unfinishedTaskInfo:view")
    @GetMapping("/unfinishedTaskInfo")
    public String unfinishedTaskInfo() {
        return "manage/taskInfo/unfinishedTaskInfo";
    }

    //堆垛机任务下发界面
    @RequiresPermissions("manage:hayStackerControl:view")
    @GetMapping("/hayStackerControl")
    public String hayStackerControl() {
        return "manage/hayStackerControl/hayStackerControl";
    }

    @PostMapping("/hayStackerTaskProduce")
    @ResponseBody
    @Transactional
    public Result hayStackerTaskProduce(@RequestBody HayStackerLocation hayStackerLocation) {
        List<Task> tasks = taskInfoService.findByStateAndType(new TaskInfoCriteria());
        if(tasks.size()>0){
            return ResultGenerator.genFailResult(CommonCode.SERVER_INERNAL_ERROR,"当前有其他任务执行中，请等待所有任务执行完成后再调用。");
        }
        TaskInfo taskInfo = new TaskInfo(new GuidUtils().toString(),
                MyUtils.connectShelfNameAndRowAndColumn(hayStackerLocation.getFromShelf().toString(),hayStackerLocation.getFromColumn(),hayStackerLocation.getFromRow()),
                MyUtils.connectShelfNameAndRowAndColumn(hayStackerLocation.getToShelf().toString(),hayStackerLocation.getToColumn(),hayStackerLocation.getToRow()),
                TaskTypeConstant.CHECK_FROM_PAPER_COUNTERS,0,0,null,"0",
                hayStackerLocation.getCardNo(),DateUtils.getTime()
                );
        taskInfoService.save(taskInfo);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 查询任务列表
     */
    @PostMapping("/findList")
    @ResponseBody
    public TableDataInfo findList(TaskInfoCriteria taskInfoCriteria) {
        startPage();
        List<TaskInfoDto> list = taskInfoService.findList(taskInfoCriteria);
        return getDataTable(list);
    }
    /**
     * 查询任务列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TaskInfoCriteria taskInfoCriteria) {
        startPage();
        List<Task> list = taskInfoService.findByStateAndType(taskInfoCriteria);
        return getDataTable(list);
    }

    /**
     * 手动完成任务
     * @param criteria
     * @return
     */
    @PostMapping("/manualFinish")
    @ResponseBody
    @Transactional
    public Result manualFinish(@RequestBody TaskInfoCriteria criteria) {
        TaskInfo taskInfo = taskInfoService.findById(criteria.getId());
        /*if(taskInfo.getState().equals(0)){
            return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"请选择执行中或者已下发任务！",null);
        }*/
        taskInfo.setState(3);
        taskInfo.setCardNo(criteria.getLoginPersonCardNo());
        taskInfoService.update(taskInfo);
        return ResultGenerator.genSuccessResult();
    }



    /**
     * 新增任务
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存任务
     */
    @RequiresPermissions("in:task:add")
    @Log(title = "任务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TaskInfo taskInfo) {
        return toAjax(taskInfoService.insertTaskInfo(taskInfo));
    }

    /**
     * 修改任务
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap) {
        TaskInfo taskInfo = taskInfoService.selectTaskInfoById(id);
        mmap.put("task", taskInfo);
        return prefix + "/edit";
    }

    /**
     * 修改保存任务
     */
    @RequiresPermissions("in:task:edit")
    @Log(title = "任务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TaskInfo taskInfo) {
        return toAjax(taskInfoService.updateTaskInfo(taskInfo));
    }

    /**
     * 删除任务
     */
    @RequiresPermissions("in:task:remove")
    @Log(title = "任务", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(taskInfoService.deleteTaskInfoByIds(ids));
    }


    /**
     * 修改保存任务
     */
    @RequiresPermissions("in:task:edit")
    @Log(title = "入库任务", businessType = BusinessType.INSERT)
    @PostMapping("/inWareTaskSave")
    @ResponseBody
    public Result inWareTaskSave(InTask inTask) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setBillInDetailId(inTask.getBillInDetailId());
        taskInfo.setState(0);
        taskInfo.setType(2);
        taskInfo.setBoxCode(inTask.getBoxCode());
        taskInfo.setTaskId(new GuidUtils().toString());
        taskInfo.setStartPosition("0.0");
        taskInfo.setStartPosition(cellInfoService.getPositionByCellId(inTask.getCellId()));
        taskInfoService.save(taskInfo);

        return ResultGenerator.genSuccessResult();
    }

    /**
     * 叫托盘(入料口出半框或者出空框)
     */
    @PostMapping("/getBox")
    @ResponseBody
    @Transactional
    public Result getBox(@RequestBody TaskInfoCriteria criteria) {
        String message = "";
        try {
            List<Task> tasks = taskInfoService.findByStateAndType(new TaskInfoCriteria());
            if(tasks.size()>0){
                for(Task task : tasks){
                    if(task.getType().equals(TaskTypeConstant.CHECK_CELL_TO_PAPER_COUNTERS_LEFT)
                            || task.getType().equals(TaskTypeConstant.CHECK_NULL_BOX_TO_PAPER_COUNTERS_RIGHT)
                            || task.getType().equals(TaskTypeConstant.CHECK_COUNT)
                            || task.getType().equals(TaskTypeConstant.CHECK_LEFT_BOX_TO_CELL_FROM_PAPERS_COUNTERS)
                            || task.getType().equals(TaskTypeConstant.CHECK_RIGHT_BOX_TO_LABELER_FROM_PAPERS_COUNTERS)
                            || task.getType().equals(TaskTypeConstant.CHECK_TO_CELL_FROM_LABELER)
                    ){
                        return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"盘点中，请勿下发其他任务");
                    }
                    else if(task.getType().equals(TaskTypeConstant.CELL_TO_OPERATOR_FLOOR)){
                        return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"当前已有出空筐或半筐到到入库口任务未完成，请等待任务完成后再次下发！");

                    }
                }
            }

            if(criteria.getWhetherNullBox().equals(1)){
                //叫空框
                message = cellInfoService.findOutBox(1002,null,null,criteria.getLoginPersonCardNo(),0,criteria.getBoxType());
            }else {
                //叫半框
                message = cellInfoService.findOutBox(1001,criteria.getItemCode(),criteria.getBatch(),criteria.getLoginPersonCardNo(),criteria.getQuantity(),null);
            }
            if(!message.equals("success")){
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 入空框
     */
    @PostMapping("/inNullBox")
    @ResponseBody
    @Transactional
    public Result inNullBox(@Param("boxCode") String boxCode,@Param("loginPersonCardNo") String loginPersonCardNo,@Param("boxType") Integer boxType) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            String msg = cellInfoService.inNullBox(boxCode, loginPersonCardNo,boxType);
            if (!msg.equals("success")) {
                error = "无可用货位";
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.PARAMETER_ERROR, error);
        }
        return ResultGenerator.genSuccessResult();
    }
    /**
     * alarm检测有无货报警
     */
    @PostMapping("/wcsAlarm")
    @ResponseBody
    public Result wcsAlarm(HttpServletRequest request) {
        String wholeStr = null;
        JSONObject jsonObject = null;
        try {
            wholeStr = MyUtils.analysisHttpServletRequest(request);
            jsonObject = JSON.parseObject(wholeStr);
            String taskNo = jsonObject.get("TaskNo").toString().trim();
            Integer state = Integer.parseInt(jsonObject.get("State").toString().trim());
            String code = MyUtils.backString(jsonObject.get("Code"));
            WarnInformation warnInformation = new WarnInformation(
                    DateUtils.getTime(),code,state,TaskTypeConstant.UNDEALT);
            warnInformation.setType(TaskTypeConstant.DETECT_ALARM);
            if(code.equals("InWare_InAlarm")){
                warnInformation.setMemo("入库段入库检测有货或者无货,请查看详细信息!");
            }
            else if(code.equals("InWare_OutAlarm")){
                warnInformation.setMemo("入库段出库检测有货或者无货,请查看详细信息!");
            }
            else if(code.equals("OutWare_InAlarm")){
                warnInformation.setMemo("出库段入库检测有货或者无货,请查看详细信息!");
            }
            TaskInfo taskInfo = taskInfoService.getTaskInfoByTaskId(taskNo);
            if(taskInfo == null) {
                warnInformation.setTaskId(null);
            }else{
                warnInformation.setTaskId(taskNo);
                warnInformation.setBoxCode(taskInfo.getBoxCode());
            }
            warnInformationService.save(warnInformation);
            WebSocketServer.sendInfo(warnInformation.getMemo(),TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }finally{
            wholeStr = null;
            jsonObject = null;
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 当堆垛机放托盘完成任务时调用此接口
     */
    @PostMapping("/updateTaskInfoState")
    @ResponseBody
    @Transactional
    public Result updateTaskInfoState(HttpServletRequest request) {
        try {
            String wholeStr = MyUtils.analysisHttpServletRequest(request);
            JSONArray jsonArrays = JSONArray.parseArray(wholeStr);
            for(int i=0;i<jsonArrays.size();i++){
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                String taskId = jsonObject.get("TaskNo").toString().trim();
                Integer state = Integer.parseInt(jsonObject.get("State").toString().trim());
                Integer quantity = MyUtils.backInteger(jsonObject.get("quantityResponse"));
//                System.out.println(wholeStr);
//                System.out.println(taskId+"a   a"+state+"a   a"+quantity);
                TaskInfo taskInfo = taskInfoService.getTaskInfoByTaskId(taskId);
                if (taskInfo != null && !taskInfo.getState().equals(state)) {
                    taskInfo.setState(state);
                    //获得该任务的类型(可参考数据库注释)
                    int type = taskInfo.getType();
                    //1-入库任务(将空/半空托盘从货位上移到固定位置)完成
                    if (state == 3 && type == TaskTypeConstant.CELL_TO_OPERATOR_FLOOR) {
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        //将货位状态改成无货
                        updateCellInfoStateOne(0,taskId);
                        //将托盘与货位解绑
                        updateBoxInfoState(3,taskInfo.getBoxCode());
                    }
                    //2-入空箱
                    else if (state == 3 && type == TaskTypeConstant.IN_NULL_BOX) {
                        //将货位状态改成1
                        updateCellInfoStateOne(1, taskId);
                        //将容器表中的boxCellId改为cellId
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByTaskId(taskId);
                        //设置托盘状态为1，托盘进入货位
                        boxInfo.setBoxState(1);
                        boxInfoService.update(boxInfo);
                    }
                    //3-入有货箱
                    else if (state == 3 && type == TaskTypeConstant.IN_AVAILABLE_BOX) {
                        //将货位状态改成1
                        updateCellInfoStateOne(1, taskId);
                        //将容器表中的boxCellId改为cellId
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByTaskId(taskId);
                        //设置托盘状态为1，托盘进入货位
                        boxInfo.setBoxState(1);
                        boxInfoService.update(boxInfo);
                    }
                    //11-出库任务完成(货位出库到点数机)
                    else if (state == 3 && type == TaskTypeConstant.CELL_TO_PAPER_COUNTERS) {
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());
                        CellInfo cellInfo = cellInfoService.findById(boxInfo.getBoxCellId());
                        cellInfoService.updateCellInfoState(cellInfo,0);
                        boxInfo.setBoxCellId(null);
                        boxInfo.setBoxState(2);
                        boxInfoService.updateBoxInfo(boxInfo);
                        PickTaskCriteria criteria = new PickTaskCriteria();
                        criteria.setBillOutDetailId(taskInfo.getBillOutDetailId());
                        criteria.setPickState(2);
                        criteria.setBoxCode(taskInfo.getBoxCode());
                        List<PickTaskDto> pickTaskDtos = pickTaskService.findByState(criteria);
                        if(pickTaskDtos.size()>0){
                            //12-下发点数任务
                            PickTaskDto pickTaskDto = pickTaskDtos.get(0);
                            updatePickTaskState(pickTaskDto);
                            TaskInfo count = new TaskInfo(new GuidUtils().toString(), "120",
                                    "128", TaskTypeConstant.COUNT_TO_CARRIER, 0, pickTaskDto.getLockPickQuantity(),
                                    pickTaskDto.getBoxCode(), pickTaskDto.getSequence().toString(), pickTaskDto.getBillOutDetailId());
                            taskInfoService.save(count);
                        }
                    }
                    //12-点数任务完成
                    else if (state == 3 && type == TaskTypeConstant.COUNT_TO_CARRIER) {
                        taskInfo.setCompleteQuantity(quantity);
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        if(quantity<taskInfo.getQuantity()){
                            WarnInformation warnInformation = new WarnInformation();
                            warnInformation.setTaskId(taskInfo.getTaskId());
                            warnInformation.setBoxCode(taskInfo.getBoxCode());
                            warnInformation.setState(TaskTypeConstant.UNDEALT);
                            warnInformation.setType(TaskTypeConstant.COUNT_EXILE);
                            warnInformation.setCreateTime(DateUtils.getTime());
                            warnInformation.setMemo("点数箱覆铜板数量缺失，请盘点！");
                            warnInformationService.save(warnInformation);
                            WebSocketServer.sendInfo(warnInformation.getMemo()+"任务编号为"+taskInfo.getTaskId(),TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
                        }
                        //根据状态箱号及出库单Id查询
                        PickTaskCriteria pickTaskCriteriaOne = new PickTaskCriteria();
                        pickTaskCriteriaOne.setPickState(3);
                        pickTaskCriteriaOne.setBoxCode(taskInfo.getBoxCode());
                        pickTaskCriteriaOne.setBillOutDetailId(taskInfo.getBillOutDetailId());
                        List<PickTaskDto> pickTaskDtos6 = pickTaskService.findList(pickTaskCriteriaOne);
                        //根据箱号查询此箱详细信息
                        BoxItem boxItem = boxItemService.getBoxItemByBoxCode(taskInfo.getBoxCode());
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());

                        if(pickTaskDtos6.size()>0){
                            //查到领料任务写入点数数量并修改此领料状态
                            PickTaskDto pickTaskDto1 = pickTaskDtos6.get(0);
                            pickTaskDto1.setOutTime(DateUtils.getTime());
                            pickTaskDto1.setPickQuantity(quantity);
                            pickTaskDto1.setSubInventoryId(boxItem.getSubInventoryId());
                            //判断是否点数，如果没点数则不改变此点数任务数量，等到工单取消的时候改变此状态
                            if(quantity>0) {
                                pickTaskDto1.setPickState(4);
                            }
                            pickTaskService.update(pickTaskDto1);

                            //剩余数量
                            Integer surplus = boxItem.getQuantity()-quantity <= 0 ? 0 : boxItem.getQuantity()-quantity;
                            //剩余锁定数量根据实际点数数量进行确认
                            Integer forecastStockQuantity = 0;
                            //如果点数数量等于任务数量或者点数数量小于等于0
                            if(quantity == taskInfo.getQuantity() || quantity<=0){
                                forecastStockQuantity = boxItem.getForecastStockQuantity() - quantity <= 0 ? 0 : boxItem.getForecastStockQuantity() - quantity;
                            }
                            //如果点数数量缺失并且大于0
                            else if(quantity>0){
                                forecastStockQuantity = boxItem.getForecastStockQuantity() - taskInfo.getQuantity() <= 0 ? 0 : boxItem.getForecastStockQuantity() - taskInfo.getQuantity();
                            }
                            //如果
                            //完成此箱数量扣减
                            if(surplus <= 0){
                                boxItem.setQuantity(0);
                                boxItem.setForecastStockQuantity(0);
                                boxItem.setWorkOrderStockState(0);
                                boxItem.setWorkerOrderNo(null);
                                boxItem.setLockQuantity(0);
                                boxInfo.setHasGoods(0);
                            }else{
                                boxItem.setQuantity(surplus);
                                boxItem.setForecastStockQuantity(forecastStockQuantity);
                                if(forecastStockQuantity<=0){
                                    boxItem.setWorkOrderStockState(0);
                                }else {
                                    boxItem.setWorkOrderStockState(1);
                                }
                                boxInfo.setHasGoods(1);
                            }
                            boxInfoService.update(boxInfo);
                            boxItemService.update(boxItem);
                        }
                        //根据出库Id及状态为2已下发任务未点数的点数任务
                        PickTaskCriteria criteria = new PickTaskCriteria();
                        criteria.setBillOutDetailId(taskInfo.getBillOutDetailId());
                        criteria.setPickState(2);
                        List<PickTaskDto> pickTaskDtos = pickTaskService.findByState(criteria);
                        //如果有则是下一箱，当前箱回库
                        if(pickTaskDtos.size()>0) {
                            //13-点数机中托盘寻找合适的货位返回
                            CellInfoDto cellInfoDto = cellInfoService.getBestCell();
                            cellInfoService.updateCellInfoState(cellInfoDto,2);
                            boxInfo.setBoxCellId(cellInfoDto.getCellId());
                            boxInfoService.update(boxInfo);
                            TaskInfo boxBackCellInfo = new TaskInfo(new GuidUtils().toString(), "120",MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto.getShelfName(),cellInfoDto.getSColumn(),cellInfoDto.getSRow()),
                                    TaskTypeConstant.BOX_TO_CELL_FROM_PAPER_COUNTERS, 0, boxItem.getQuantity(), taskInfo.getBoxCode(), taskInfo.getIsTop(), taskInfo.getBillOutDetailId());
                            taskInfoService.save(boxBackCellInfo);
                        }
                        //没有则载具出到AGV
                        else{
                            //14-载具移动到到AVG出货口
                            TaskInfo outbound = new TaskInfo(new GuidUtils().toString(), "128", "132", TaskTypeConstant.CARRIER_TO_AVG_FROM_PAPER_COUNTERS, 0,
                                    quantity, taskInfo.getBoxCode(), taskInfo.getIsTop(), taskInfo.getBillOutDetailId());
                            taskInfoService.save(outbound);
                            //根据当前箱号查询未点数的点数任务，下一筐出库单
                            criteria.setBillOutDetailId(null);
                            criteria.setBoxCode(taskInfo.getBoxCode());
                            List<PickTaskDto> pickTaskDtos1 = pickTaskService.findByState(criteria);
                            //如果有下发点数任务
                            if(pickTaskDtos1.size() >0){
                                PickTaskDto pickTaskDto = pickTaskDtos1.get(0);
                                updatePickTaskState(pickTaskDto);
                                //根据当前点数机内的箱号，查询到此箱出料，12-下发点数任务
                                TaskInfo count = new TaskInfo(new GuidUtils().toString(), taskInfo.getStartPosition(), "128", TaskTypeConstant.COUNT_TO_CARRIER, 0,
                                        pickTaskDto.getPickQuantity(), pickTaskDto.getBoxCode(), pickTaskDto.getSequence().toString(), pickTaskDto.getBillOutDetailId());
                                taskInfoService.save(count);
                            }
                            //如果没有则当前箱回库
                            else{
                                CellInfoDto cellInfoDto = cellInfoService.getBestCell();
                                cellInfoService.updateCellInfoState(cellInfoDto,2);
                                boxInfo.setBoxCellId(cellInfoDto.getCellId());
                                boxInfoService.update(boxInfo);
                                //13-点数机中托盘寻找合适的货位返回
                                TaskInfo boxBackCellInfo = new TaskInfo(new GuidUtils().toString(), "120",MyUtils.connectShelfNameAndRowAndColumn(cellInfoDto.getShelfName(),cellInfoDto.getSColumn(),cellInfoDto.getSRow()),
                                        TaskTypeConstant.BOX_TO_CELL_FROM_PAPER_COUNTERS, 0, boxItem.getQuantity(), taskInfo.getBoxCode(), taskInfo.getIsTop(), taskInfo.getBillOutDetailId());
                                taskInfoService.save(boxBackCellInfo);
                            }
                        }
                    }
                    //13-点数机中托盘寻找合适的货位返回完成
                    else if (state == 3 && type == TaskTypeConstant.BOX_TO_CELL_FROM_PAPER_COUNTERS) {
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        //将货位状态改为0
                        updateCellInfoStateOne(1,taskId);
                        //将托盘表中绑定已放置的货位Id
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());
                        boxInfo.setBoxState(1);
                        boxInfoService.update(boxInfo);
                    }
                    //15-从点数机出空框到货位（此时检测有货无货，无货正常上到货位，有货，框到异常处理货位）完成
                    else if (state == 3 && type == TaskTypeConstant.CHECK_FROM_PAPER_COUNTERS) {
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        if(taskInfo.getInventoryCheckId() != null) {
                            if (taskInfo.getStartPosition().equals(TaskTypeConstant.EXCEPTION_MANAGE_CELL)) {
                                BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(taskInfo.getBoxCode());
                                boxInfo.setBoxState(1);
                                boxInfoService.update(boxInfo);
                                CellInfo cellInfo = cellInfoService.findById(boxInfo.getBoxCellId());
                                cellInfo.setState(1);
                                cellInfoService.update(cellInfo);
                            }
                        }
                    }
                    //21 - 盘库任务（托盘从货位到点数机左）完成
                    else if(state == 3 && type == TaskTypeConstant.CHECK_CELL_TO_PAPER_COUNTERS_LEFT){
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        //下发点数任务
                        BoxItem boxItem = boxItemService.getBoxItemByBoxCode(taskInfo.getBoxCode());
                        TaskInfo countTaskInfo = new TaskInfo(new GuidUtils().toString(),
                                "120","128",TaskTypeConstant.CHECK_COUNT,0,boxItem.getQuantity(),boxItem.getBoxCode(),
                                "0",taskInfo.getCardNo(),DateUtils.getTime());
                        countTaskInfo.setInventoryCheckId(taskInfo.getInventoryCheckId());
                        taskInfoService.save(countTaskInfo);
                    }
                    //22 - 盘库任务（空托盘从货位到点数机右）完成
                    else if(state == 3 && type == TaskTypeConstant.CHECK_NULL_BOX_TO_PAPER_COUNTERS_RIGHT){
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                    }
                    //23 - 盘库任务（点数并在点完后检测左边托盘是否为空）完成
                    else if(state == 3 && type == TaskTypeConstant.CHECK_COUNT){
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        taskInfo.setCompleteQuantity(quantity);
                        InventoryCheck inventoryCheck = inventoryCheckService.findById(taskInfo.getInventoryCheckId());
                        //查找原箱
                        BoxItemDto boxItem = boxItemService.findList(new BoxItemCriteria(inventoryCheck.getBoxCode())).get(0);
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxItem.getBoxCode());
                        boxInfo.setHasGoods(0);
                        boxInfoService.update(boxInfo);
                        //查找点数后箱
                        BoxItem toBoxItem = boxItemService.getBoxItemByBoxCode(inventoryCheck.getToBoxCode());
                        toBoxItem.setItemCode(boxItem.getItemCode());
                        toBoxItem.setBatch(boxItem.getBatch());
                        toBoxItem.setQuantity(quantity);
                        toBoxItem.setBillInDetailId(boxItem.getBillInDetailId());
                        toBoxItem.setSubInventoryId(boxItem.getSubInventoryId());
                        toBoxItem.setPd(boxItem.getPd());
                        toBoxItem.setExp(boxItem.getExp());
                        toBoxItem.setInTime(boxItem.getInTime());
                        toBoxItem.setWorkerOrderNo(null);
                        boxItemService.update(toBoxItem);
                        BoxInfo toboxInfo = boxInfoService.getBoxInfoByBoxCode(toBoxItem.getBoxCode());
                        toboxInfo.setHasGoods(1);
                        boxInfoService.update(toboxInfo);

                        inventoryCheck.setAfterCheckQuantity(quantity);
                        inventoryCheckService.update(inventoryCheck);

                        boxItem.setQuantity(0);
                        boxItem.setLockQuantity(0);
                        boxItem.setForecastStockQuantity(0);
                        boxItemService.update(boxItem);
                        //下发回库任务
                        TaskInfo backWarehouseTaskInfo = new TaskInfo(new GuidUtils().toString(),"120",
                                MyUtils.connectShelfNameAndRowAndColumn(boxItem.getShelfName(),boxItem.getsColumn(),boxItem.getsRow()),
                                TaskTypeConstant.CHECK_LEFT_BOX_TO_CELL_FROM_PAPERS_COUNTERS,0,0, boxItem.getBoxCode(),
                                "0",taskInfo.getCardNo(),DateUtils.getTime());
                        backWarehouseTaskInfo.setInventoryCheckId(inventoryCheck.getInventoryCheckId());
                        taskInfoService.save(backWarehouseTaskInfo);
                        //下发到出库口任务
                        TaskInfo toOperatorTaskInfo = new TaskInfo(new GuidUtils().toString(),"128","105",
                                TaskTypeConstant.CHECK_RIGHT_BOX_TO_LABELER_FROM_PAPERS_COUNTERS,0,toBoxItem.getQuantity(), toBoxItem.getBoxCode(),
                                "0",taskInfo.getCardNo(),DateUtils.getTime());
                        toOperatorTaskInfo.setBarCode(MyUtils.connectPrintString(toBoxItem.getItemCode(),toBoxItem.getQuantity(),toBoxItem.getExp(),toBoxItem.getBatch(),boxItem.getItemName()));
                        toOperatorTaskInfo.setInventoryCheckId(inventoryCheck.getInventoryCheckId());
                        taskInfoService.save(toOperatorTaskInfo);
                    }
                    //24 - 盘库任务（左边托盘回空货位或者盘盈锁定货位）完成
                    else if(state == 3 && type == TaskTypeConstant.CHECK_LEFT_BOX_TO_CELL_FROM_PAPERS_COUNTERS){
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        //修改状态
                        updateCellInfoStateOne(1, taskId);
                        //将容器表中的boxCellId改为cellId
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByTaskId(taskId);
                        //设置托盘状态为1，托盘进入货位
                        boxInfo.setBoxState(1);
                        boxInfoService.update(boxInfo);
                    }
                    //25 - 盘库任务（右边托盘从点数机到入库口）完成
                    else if(state == 3 && type == TaskTypeConstant.CHECK_RIGHT_BOX_TO_LABELER_FROM_PAPERS_COUNTERS){
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        //下发贴标回库动作
                        BoxItemDto boxItem = boxItemService.findList(new BoxItemCriteria(taskInfo.getBoxCode())).get(0);
                        TaskInfo paperTaskInfo = new TaskInfo(new GuidUtils().toString(),"105",
                                MyUtils.connectShelfNameAndRowAndColumn(boxItem.getShelfName(),boxItem.getsColumn(),boxItem.getsRow()),
                                TaskTypeConstant.CHECK_TO_CELL_FROM_LABELER,0,taskInfo.getQuantity(), taskInfo.getBoxCode(),
                                "0",taskInfo.getCardNo(),DateUtils.getTime());
                        paperTaskInfo.setBarCode(taskInfo.getBarCode());
                        paperTaskInfo.setInventoryCheckId(taskInfo.getInventoryCheckId());
                        taskInfoService.save(paperTaskInfo);
                    }
                    //26 - 盘库任务（对右边托盘重新贴标入库）完成
                    else if(state == 3 && type == TaskTypeConstant.CHECK_TO_CELL_FROM_LABELER){
                        taskInfo.setTaskEndTime(DateUtils.getTime());
                        //修改状态
                        updateCellInfoStateOne(1, taskId);
                        //将容器表中的boxCellId改为cellId
                        BoxInfo boxInfo = boxInfoService.getBoxInfoByTaskId(taskId);
                        //设置托盘状态为1，托盘进入货位
                        boxInfo.setBoxState(1);
                        boxInfoService.update(boxInfo);
                    }
                    taskInfoService.update(taskInfo);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }finally{
        }
        return ResultGenerator.genSuccessResult();
    }
    /**
     * 当载具到达AGV出库口时调用
     */
    @PostMapping("/updateTaskInfoTypeIsOutAGV")
    @ResponseBody
    @Transactional
    public Result updateTaskInfoTypeIsSixState(HttpServletRequest request) {
        try {
            String wholeStr = MyUtils.analysisHttpServletRequest(request);
            JSONArray jsonArrays = JSONArray.parseArray(wholeStr);
            for(int i=0;i<jsonArrays.size();i++){
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                String taskId = jsonObject.get("TaskNo").toString().trim();
                Integer state = Integer.parseInt(jsonObject.get("State").toString().trim());
//                Integer quantity = jsonObject.get("Quantity")!=null?Integer.parseInt(jsonObject.get("Quantity").toString().trim()):null;
                TaskInfo taskInfo = taskInfoService.getTaskInfoByTaskId(taskId);
                    //6-出库任务（将有合适货物的载具调度到AGV）给MES传递接收载具指令(接口stockWipOutReq)完成
                if (taskInfo != null) {
                    if(!taskInfo.getState().equals(state)) {
                        //14-点数机出货到AGV并点数
                        if(!taskInfo.getState().equals(3)) {
                            if (state.equals(3) && taskInfo.getType() == TaskTypeConstant.CARRIER_TO_AVG_FROM_PAPER_COUNTERS) {
                                taskInfo.setTaskEndTime(DateUtils.getTime());
                                WebserviceResponse webserviceResponse = null;
                                List<Door> doors = doorService.selectDoorList(new Door(null, null, null, 2, null, null));
                                String time = DateUtils.getTime();
                                Carrier carrier = carrierService.findFirstCarrier();
//                        BoxItem boxItem = boxItemService.getBoxItemByBoxCode(task.getBoxCode());
                                TaskInfoCriteria criteria = new TaskInfoCriteria(taskInfo.getBillOutDetailId(), TaskTypeConstant.COUNT_TO_CARRIER, null);
                                List<TaskInfoDto> taskInfoDtos = taskInfoService.findList(criteria);
                                List<PickTaskDto> pickTaskDtos = pickTaskService.findByState(new PickTaskCriteria(taskInfo.getBillOutDetailId()));
                                String batch = pickTaskDtos.get(0).getBatch();
                                Integer completeQuantity = 0;
//                        for (TaskInfoDto taskInfoDto : taskInfoDtos) {
//                            completeQuantity += taskInfoDto.getCompleteQuantity();
//                        }
                                TaskInfoDto taskInfoDto = taskInfoDtos.get(0);
//                        SubInventory subInventory = subInventoryService.findById(1);
                                RequestIdAuto requestIdAuto = requestIdAutoService.backAutoId("WMS工单发料写入EBS接口");
//                        WorkerOrderIssueTime workerOrderIssueTime = workerOrderIssueTimeService.findById(1);
                                WorkerOrderIssueTime workerOrderIssueTime = TaskTypeConstant.workerOrderIssueTime;
                                List<RequestId> requestIds = new ArrayList<>();
                                List<Map<String, String>> lists = new ArrayList<>();
                                for (PickTaskDto pickTaskDto : pickTaskDtos) {
                                    if(pickTaskDto.getPickQuantity()>0) {
                                        lists.add(MyUtils.wipOut(TaskTypeConstant.organizationId.toString(), TaskTypeConstant.MES_BILL_OUT,
                                                taskInfoDto.getBillNo(), taskInfoDto.getInventoryItemId().toString(), (pickTaskDto.getPickQuantity() * (-1)) + "",
                                                workerOrderIssueTime.getOperationSeqnum() == null ? null : workerOrderIssueTime.getOperationSeqnum(),
                                                pickTaskDto.getBatch(), pickTaskDto.getSubInventoryCode(), pickTaskDto.getSlotting() == null ? "" : pickTaskDto.getSlotting(),
                                                MyUtils.getNinetySecondsAgo(), taskInfoDto.getUnit()));

                                        RequestId requestId = new RequestId(requestIdAuto.getRequestId(), taskInfoDto.getInventoryItemId(), (pickTaskDto.getPickQuantity() * (-1)),
                                                pickTaskDto.getBatch(), pickTaskDto.getSubInventoryCode(), pickTaskDto.getSlotting() == null ? null : Integer.parseInt(pickTaskDto.getSlotting()),
                                                TaskTypeConstant.organizationId, TaskTypeConstant.MES_BILL_OUT, taskInfoDto.getBillNo(), workerOrderIssueTime.getOperationSeqnum(),
                                                DateUtils.getTime(), taskInfoDto.getUnit(), TaskTypeConstant.OUT, TaskTypeConstant.FAIL_WAIT_MANAGE, "调用WMS工单发料写入EBS接口失败", "ERROR");
//                            requestIdService.save(requestId);
                                        requestIds.add(requestId);
                                    }
                                    completeQuantity += pickTaskDto.getPickQuantity();
                                }
                                BillOutMaster billOutMaster = billOutMasterService.selectBillOutMasterByBillOutDetailId(taskInfo.getBillOutDetailId());
                                BillOutDetail billOutDetail = billOutDetailService.findById(taskInfo.getBillOutDetailId());
                                String code = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                        "<StockWipOutReq\n" +
                                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                                        "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" \n" +
                                        "macCode=\"" + doors.get(0).getCode() + "\" \n" +
                                        "wipEntity =\"" + taskInfoDto.getBillNo() + "\"\n" +
                                        "berthCode=\"" + doors.get(0).getAddressCode() + "\" \n" +
                                        "taskCode=\"" + taskInfo.getTaskId() + "\">\n" +
                                        "<item tagCode=\"" + doors.get(0).getCode() + "_1000\" tagValue=\"" + taskInfoDto.getItemCode() + "\" timeStamp=\"" + time + "\" />\n" +
                                        "<item tagCode=\"" + doors.get(0).getCode() + "_1001\" tagValue=\"" + completeQuantity + "\" timeStamp=\"" + time + "\" />\n" +
                                        "<item tagCode=\"" + doors.get(0).getCode() + "_1002\" tagValue=\"" + billOutDetail.getPriority() + "\" timeStamp=\"" + time + "\" />\n" +
                                        "<item tagCode=\"" + doors.get(0).getCode() + "_1003\" tagValue=\"" + carrier.getCarrierCode() + "\" timeStamp=\"" + time + "\" /> " +
                                        "<item tagCode=\"" + doors.get(0).getCode() + "_1004\" tagValue=\"" + batch + "\" timeStamp=\"" + time + "\" />\n" +
                                        "</StockWipOutReq>";
                                carrier.setCode(code);
                                carrier.setBillOutDetailId(taskInfo.getBillOutDetailId());
                                if (billOutDetail.getQuantity() == completeQuantity) {
                                    billOutMaster.setState(2);
                                } else {
                                    billOutMaster.setState(1);
                                }
                                billOutDetail.setAlreadyOutQuantity(completeQuantity);
                                billOutDetailService.update(billOutDetail);
                                billOutMasterService.update(billOutMaster);
                                carrierService.update(carrier);
                                //EBS扣减数据
                                requestIdService.inventoryMinus(requestIds, requestIdAuto, lists);

                                CallAgv callAgv = new CallAgv();
                                webserviceResponse = serverVisitAddressService.requestMesServer("StockWipOutReq", code);
                                callAgv.setId(null);
                                callAgv.setMethodName("StockWipOutReq");
                                callAgv.setCode(code);
                                callAgv.setCreateTime(DateUtils.getTime());
                                callAgv.setWipEntity(billOutMaster.getBillNo());
                                callAgv.setItemCode(billOutDetail.getItemCode());
                                callAgv.setQuantity(billOutDetail.getAlreadyOutQuantity());
                                callAgv.setErrorCode(webserviceResponse.getErrorCode());
                                callAgv.setTaskCode(webserviceResponse.getTaskCode() == null ? null : webserviceResponse.getTaskCode());
                                callAgv.setErrorMsg("WMS工单完成,呼叫AGV接口。" + webserviceResponse.getErrorMsg());
                                callAgvService.save(callAgv);
                                if (webserviceResponse.getErrorMsg().equals("OK") && webserviceResponse.getErrorCode().equals("0")) {
                                    carrier.setTime(DateUtils.getTime());
                                    carrier.setCarrierState(2);
                                    carrierService.update(carrier);
                                } else {
                                    WarnInformation warnInformation = new WarnInformation("呼叫AGV取载具失败：" + webserviceResponse.getErrorMsg(),
                                            TaskTypeConstant.UNDEALT, TaskTypeConstant.CALL_AGV_ERROR, DateUtils.getTime());
                                    warnInformation.setTaskId(taskInfo.getTaskId());
                                    warnInformationService.save(warnInformation);
                                    if(webserviceResponse.getErrorMsg().equals("【AGV问题】：呼叫AGV异常-位置个数少于0, 不能入库")){
                                        TaskTypeConstant.call_agv_state = 2;
                                        carrierService.callMesGetCarrier(carrier,callAgv,warnInformation);
                                    }else {
                                        WebSocketServer.sendInfo(webserviceResponse.getErrorMsg() + "出料口呼叫AGV失败，任务编号为" + taskInfo.getTaskId(), TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
                                    }
                                }
                            }
                        }
                        taskInfo.setState(state);
                        taskInfoService.update(taskInfo);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }finally{

        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 修改托盘状态
     */
    public void updateBoxInfoState(Integer state,String boxCode){
        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxCode);
        boxInfo.setBoxCellId(null);
        boxInfo.setBoxState(state);
        boxInfoService.updateBoxInfo(boxInfo);
    }
    private void updateCellInfoStateOne(Integer state,String taskId){
        CellInfo cellInfo = cellInfoService.getCellInfoByTaskId(taskId);
        cellInfo.setState(state);
        cellInfoService.update(cellInfo);
    }

    /**
     *
     */
    private void updatePickTaskState(PickTaskDto pickTaskDto){
        pickTaskDto.setPickState(3);
        pickTaskService.update(pickTaskDto);
    }

    private void updateBoxIndo(String boxCode,Integer cellId){
        BoxInfo boxInfo = boxInfoService.getBoxInfoByBoxCode(boxCode);
        boxInfo.setBoxCellId(cellId);
        boxInfoService.update(boxInfo);
    }

    /**
     * 任务
     */
    @Log(title = "导出任务列表", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:taskInfo:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TaskInfoCriteria criteria)
    {
        criteria.setPageSize(10000);
        List<TaskInfoDto> list = taskInfoService.findList(criteria);
        ExcelUtil<TaskInfoDto> util = new ExcelUtil<TaskInfoDto>(TaskInfoDto.class);
        return util.exportExcel(list, "任务列表");
    }

}

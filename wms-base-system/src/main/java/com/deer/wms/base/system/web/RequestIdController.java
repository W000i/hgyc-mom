package com.deer.wms.base.system.web;

import com.alibaba.fastjson.JSONArray;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.service.RequestIdAutoService;
import com.deer.wms.base.system.service.RequestIdService;
import com.deer.wms.base.system.service.ServerVisitAddressService;
import com.deer.wms.common.core.domain.AjaxResult;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by  on 2019/12/26.
*/
@Controller
@RequestMapping("/requestId")
public class RequestIdController  extends BaseController{

    private String prefix = "manage/callPortRecord";

    @Autowired
    private RequestIdService requestIdService;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private RequestIdAutoService requestIdAutoService;

    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("requestId:view")
    @GetMapping()
    public String requestId()
    {
        return prefix + "/requestId";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
    RequestId requestId = requestIdService.findById(id);
        mmap.put("requestId", requestId);
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


    @PostMapping
    @ResponseBody
    public Result add(@RequestBody RequestId requestId) {
        requestIdService.save(requestId);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        requestIdService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody RequestId requestId) {
        requestIdService.update(requestId);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        RequestId requestId = requestIdService.findById(id);
        return ResultGenerator.genSuccessResult(requestId);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(RequestIdCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<RequestId> list = requestIdService.findAll();
        return getDataTable(list);
    }

    @PostMapping("/findList")
    @ResponseBody
    public  TableDataInfo findList(RequestIdCriteria criteria) {
        startPage();
        List<RequestIdDto> list = requestIdService.selectList(criteria);
        return getDataTable(list);
    }

    @RequiresPermissions("requestId:refresh")
    @GetMapping("/refresh")
    @ResponseBody
    public AjaxResult refresh()
    {
        List<RequestIdDto> requestIdDtos = requestIdService.findProcessing(new RequestIdCriteria(TaskTypeConstant.MANAGING));
        if(requestIdDtos.size()>0){
            List<Map<String, String>> wipOuts = new ArrayList<>();
            List<Map<String, String>> accountAliasOuts = new ArrayList<>();
            List<Map<String, String>> subInventoryTranfers = new ArrayList<>();

            for(RequestIdDto requestIdDto : requestIdDtos){
                Map<String, String> map = new HashMap<>();
                if(requestIdDto.getType().equals(TaskTypeConstant.OUT)){
                    map.put("requestId",requestIdDto.getRequestId()+"");
                    wipOuts.add(map);
                }else if(requestIdDto.getType().equals(TaskTypeConstant.ACCOUNT_ALIAS)){
                    map.put("requestId",requestIdDto.getRequestId()+"");
                    accountAliasOuts.add(map);
                }else if(requestIdDto.getType().equals(TaskTypeConstant.TRANSFER)){
                    map.put("requestId",requestIdDto.getRequestId()+"");
                    subInventoryTranfers.add(map);
                }
            }
            //查询工单发料数据处理情况并修改状态
            if(wipOuts.size()>0) {
                selectEBSSStatus("3", TaskTypeConstant.GET_WIPTRXS_INTF, TaskTypeConstant.QUERY, null, wipOuts);
            }
            if(accountAliasOuts.size()>0) {
                selectEBSSStatus("4", TaskTypeConstant.GET_OTHERS_INTF, TaskTypeConstant.QUERY, null, accountAliasOuts);
            }
            if(subInventoryTranfers.size()>0) {
                selectEBSSStatus("5", TaskTypeConstant.GET_SUBINV_INTF, TaskTypeConstant.QUERY, null, subInventoryTranfers);
            }
        }else{
            return toAjax(true).success("暂无EBS正在处理中数据");
        }
        return toAjax(true).success("刷新成功！");
    }

    private void selectEBSSStatus(String requestAutoId,String serviceName,String operate,Integer organizationId,List<Map<String,String>> list){
        EbsBack entityStr = serverVisitAddressService.requestServerCode(requestAutoId,serviceName,operate,organizationId,list);
        //判断是否请求到参数
        if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
            JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
            for (int i = 0; i < jsonArrays.size(); i++) {
                com.alibaba.fastjson.JSONObject jsonObject = jsonArrays.getJSONObject(i);
                // 处理状态
                String requestId = MyUtils.backString(jsonObject.get("requestId"));
                String processStatus = MyUtils.backString(jsonObject.get("processStatus"));
                String errorMsg = MyUtils.backString(jsonObject.get("errorMsg"));
                Integer id = MyUtils.backInteger(jsonObject.get("id"));
                RequestIdDto requestIdDto = requestIdService.findProcessing(new RequestIdCriteria(requestId,id)).get(0);
                requestIdDto.setProcessStatus(processStatus);
                requestIdDto.setErrorMsg(errorMsg);
                if(processStatus.equals("SUCCESS")){
                    requestIdDto.setState(TaskTypeConstant.SUCCESS);
                }else if(processStatus.equals("ERROR")){
                    requestIdDto.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                    requestIdDto.setErrorMsg(errorMsg);
                }else{
                    requestIdDto.setState(TaskTypeConstant.MANAGING);
                }
                requestIdService.update(requestIdDto);
            }
        }
    }

    @RequiresPermissions("requestId:sendEbs")
    @GetMapping("/sendToEbs")
    @ResponseBody
    public AjaxResult sendToEbs()
    {
        List<RequestIdDto> requestIdDtos = requestIdService.findProcessing(new RequestIdCriteria(TaskTypeConstant.FAIL_WAIT_MANAGE));
        if(requestIdDtos.size()>0){
            //创建交货参数对象
           /* List<Map<String, String>> deliveryParams = new ArrayList<>();
            RequestIdAuto deliveryRequestIdAuto = requestIdAutoService.backAutoId("WMS交货数据写入EBS接口");
            List<RequestId> deliveryRequestIds = new ArrayList<>();*/
            //创建工单出库对象
            List<Map<String, String>> wipOutParams = new ArrayList<>();
            RequestIdAuto wipOutRequestIdAuto = requestIdAutoService.backAutoId("WMS工单发料写入EBS接口");
            List<RequestId> wipOutRequestIds = new ArrayList<>();
            //创建账户别名出库对象
            List<Map<String, String>> accountAliasOuts = new ArrayList<>();
            RequestIdAuto accountAliasOutsRequestIdAuto = requestIdAutoService.backAutoId("WMS账户别名发料写入EBS接口");
            List<RequestId> accountAliasOutsRequestIds = new ArrayList<>();
            //创建子库转移对象
            List<Map<String, String>> subInventoryTranfers = new ArrayList<>();
            RequestIdAuto subInventoryTranfersRequestIdAuto = requestIdAutoService.backAutoId("WMS子库存转移写入EBS接口");
            List<RequestId> subInventoryTranfersRequestIds = new ArrayList<>();

            try {
                for (RequestIdDto requestIdDto : requestIdDtos) {
                    //先修改当前状态
                    //交货
                    /*if (TaskTypeConstant.DELIVERY.equals(requestIdDto.getType())) {
                        deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), requestIdDto.getTransactionId().toString(), requestIdDto.getOrganizationId().toString(),
                                requestIdDto.getSubInventory(), requestIdDto.getLocatorId() == null ? null : requestIdDto.getLocatorId().toString(),
                                requestIdDto.getLotNumber(), requestIdDto.getQuantity().toString(), requestIdDto.getShipmentNum()));
                        //修改交货数据并写入
                        requestIdDto.setAutoGrowingId(null);
                        requestIdDto.setRequestId(deliveryRequestIdAuto.getAutoId());
                        requestIdDto.setTransactionDate(DateUtils.getTime());
                        requestIdDto.setErrorMsg("WMS交货数据写入EBS接口失败");
                        deliveryRequestIds.add(requestIdDto);
                    }*/
                    //工单出库
                    if (TaskTypeConstant.OUT.equals(requestIdDto.getType())) {
                        requestIdDto.setState(TaskTypeConstant.ALREADY_MANAGE);
                        requestIdService.update(requestIdDto);
                        wipOutParams.add(MyUtils.wipOut(requestIdDto.getOrganizationId().toString(), TaskTypeConstant.MES_BILL_OUT,
                                requestIdDto.getWipEntityId(), requestIdDto.getItemId().toString(), requestIdDto.getQuantity().toString(),
                                requestIdDto.getOperationSeqnum(), requestIdDto.getLotNumber(), requestIdDto.getSubInventory(),
                                requestIdDto.getLocatorId() == null ? null : requestIdDto.getLocatorId().toString(),
                                MyUtils.getNinetySecondsAgo(), requestIdDto.getTransactionUom()));
                        requestIdDto.setAutoGrowingId(null);
                        requestIdDto.setRequestId(wipOutRequestIdAuto.getRequestId());
                        requestIdDto.setTransactionDate(DateUtils.getTime());
                        requestIdDto.setErrorMsg("调用WMS工单发料写入EBS接口失败");
                        requestIdDto.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                        wipOutRequestIds.add(requestIdDto);
                    }
                    //账户别名出库
                    else if (TaskTypeConstant.ACCOUNT_ALIAS.equals(requestIdDto.getType())) {
                        requestIdDto.setState(TaskTypeConstant.ALREADY_MANAGE);
                        requestIdService.update(requestIdDto);
                        accountAliasOuts.add(MyUtils.accountAliasOut(requestIdDto.getTransactionTypeId(), requestIdDto.getOrganizationId().toString(),
                                requestIdDto.getItemId().toString(), requestIdDto.getSubInventory(),
                                requestIdDto.getLocatorId() == null ? null : requestIdDto.getLocatorId().toString(),
                                requestIdDto.getTransSourceName(), requestIdDto.getTransSourceId().toString(), requestIdDto.getLotNumber(),
                                requestIdDto.getQuantity().toString(), MyUtils.getNinetySecondsAgo(), requestIdDto.getTransactionUom(),
                                requestIdDto.getSourceHeaderId().toString(), requestIdDto.getSourceLineId().toString()));
                        requestIdDto.setAutoGrowingId(null);
                        requestIdDto.setRequestId(accountAliasOutsRequestIdAuto.getRequestId());
                        requestIdDto.setTransactionDate(DateUtils.getTime());
                        requestIdDto.setErrorMsg("调用WMS工单发料写入EBS接口失败");
                        requestIdDto.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                        accountAliasOutsRequestIds.add(requestIdDto);
                    }
                    //子库存转移
                    else if (TaskTypeConstant.TRANSFER.equals(requestIdDto.getType())) {
                        requestIdDto.setState(TaskTypeConstant.ALREADY_MANAGE);
                        requestIdService.update(requestIdDto);
                        subInventoryTranfers.add(MyUtils.subInventoryTransfer(TaskTypeConstant.SUB_INVENTORY_TRANSFER_TYPE, requestIdDto.getOrganizationId().toString(),
                                requestIdDto.getItemId().toString(), requestIdDto.getQuantity().toString(), requestIdDto.getSubInventory(),
                                requestIdDto.getLocatorId() == null ? null : requestIdDto.getLocatorId().toString(), MyUtils.getNinetySecondsAgo(),
                                requestIdDto.getTransactionUom(), requestIdDto.getTransSubInventory(),
                                requestIdDto.getTransLocatorId() == null ? null : requestIdDto.getTransLocatorId().toString(), requestIdDto.getLotNumber(),
                                requestIdDto.getSourceHeaderId().toString(), requestIdDto.getSourceLineId().toString()));

                        requestIdDto.setAutoGrowingId(null);
                        requestIdDto.setRequestId(subInventoryTranfersRequestIdAuto.getRequestId());
                        requestIdDto.setTransactionDate(DateUtils.getTime());
                        requestIdDto.setErrorMsg("WMS调用EBS子库转移接口失败");
                        requestIdDto.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                        subInventoryTranfersRequestIds.add(requestIdDto);
                    }
                }
                //交货
                /*if (deliveryParams.size() > 0) {
                    requestIdService.delivery(deliveryRequestIds,deliveryParams,deliveryRequestIdAuto);
                }*/
                //请求工单出库
                if (wipOutParams.size() > 0) {
                    requestIdService.inventoryMinus(wipOutRequestIds,wipOutRequestIdAuto,wipOutParams);
                }
                //账户别名出库
                if (accountAliasOuts.size() > 0) {
                    requestIdService.accountAliasOut(accountAliasOuts,accountAliasOutsRequestIds,accountAliasOutsRequestIdAuto);
                }
                //子库存转移
                if (subInventoryTranfers.size() > 0) {
                    requestIdService.subInventoryTransfer(subInventoryTranfersRequestIdAuto,subInventoryTranfersRequestIds, subInventoryTranfers);
                }
            }catch(Exception e){
                e.printStackTrace();
                throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
            }
        }else{
            return toAjax(true).success("暂无EBS处理失败数据");
        }
        return toAjax(true).success("刷新成功！");
    }

    /**
     * 已手动在EBS处理
     * @param requestIdCriteria
     * @return
     */
    @PostMapping("/manualPushToEBS")
    @ResponseBody
    @Transactional
    public Result manualPushToEBS(@RequestBody RequestIdCriteria requestIdCriteria)
    {
        String error = "服务器内部错误，请联系管理员";
        try{
            List<RequestIdDto> requestIdDtos = requestIdService.findProcessing(requestIdCriteria);
            for(RequestIdDto requestIdDto : requestIdDtos){
                if(!requestIdDto.getState().equals(TaskTypeConstant.FAIL_WAIT_MANAGE)){
                    error = "只能勾选失败需处理，请勿勾选其他！";
                    throw new RuntimeException();
                }
                requestIdDto.setHandleCard(requestIdCriteria.getLoginPersonCardNo());
                requestIdDto.setState(TaskTypeConstant.ALREADY_MANUAL_MANAGE);
                requestIdDto.setProcessStatus("SUCCESS");
                requestIdDto.setErrorMsg("#");
                requestIdService.update(requestIdDto);
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

    //EBS处理成功
    @PostMapping("/ebsManageSuccess")
    @ResponseBody
    @Transactional
    public Result ebsManageSuccess(@RequestBody RequestIdCriteria requestIdCriteria)
    {
        String error = "服务器内部错误，请联系管理员";
        try{
            List<RequestIdDto> requestIdDtos = requestIdService.findProcessing(requestIdCriteria);
            for(RequestIdDto requestIdDto : requestIdDtos){
                if(!requestIdDto.getState().equals(TaskTypeConstant.MANAGING)){
                    error = "只能勾选失败待处理，请勿勾选其他！";
                    throw new RuntimeException();
                }
                if(TaskTypeConstant.RECEIVE.equals(requestIdDto.getType()) || TaskTypeConstant.DELIVERY.equals(requestIdDto.getType())){
                    requestIdDto.setHandleCard(requestIdCriteria.getLoginPersonCardNo());
                    requestIdDto.setState(TaskTypeConstant.SUCCESS);
                    requestIdDto.setProcessStatus("SUCCESS");
                    requestIdDto.setErrorMsg("#");
                    requestIdService.update(requestIdDto);
                }else {
                    error = "只能勾选采购接收或者交货接口";
                    throw new RuntimeException();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

    //EBS处理失败重新推送
    @PostMapping("/ebsManualError")
    @ResponseBody
    @Transactional
    public Result ebsManualError(@RequestBody RequestIdCriteria requestIdCriteria)
    {
        String error = "服务器内部错误，请联系管理员";
        try{
            List<RequestIdDto> requestIdDtos = requestIdService.findProcessing(requestIdCriteria);
            //创建接收参数对象
            /*List<Map<String, String>> receiveParams = new ArrayList<>();
            RequestIdAuto receiveRequestIdAuto = requestIdAutoService.backAutoId("WMS接收数据写入EBS接口");
            List<RequestId> receiveRequestIds = new ArrayList<>();*/
            //创建交货参数对象
            List<Map<String, String>> deliveryParams = new ArrayList<>();
            RequestIdAuto deliveryRequestIdAuto = requestIdAutoService.backAutoId("WMS交货数据写入EBS接口");
            List<RequestId> deliveryRequestIds = new ArrayList<>();

            for(RequestIdDto requestIdDto : requestIdDtos){
                if(!requestIdDto.getState().equals(TaskTypeConstant.FAIL_WAIT_MANAGE)){
                    error = "只能勾选失败待处理，请勿勾选其他！";
                    throw new RuntimeException();
                }
                if(TaskTypeConstant.RECEIVE.equals(requestIdDto.getType()) || TaskTypeConstant.DELIVERY.equals(requestIdDto.getType())){
                    requestIdDto.setHandleCard(requestIdCriteria.getLoginPersonCardNo());
                    requestIdDto.setState(TaskTypeConstant.ALREADY_MANAGE);
                    requestIdDto.setProcessStatus("ERROR");
                    requestIdDto.setErrorMsg("EBS处理失败");
                    requestIdService.update(requestIdDto);

                    if (TaskTypeConstant.DELIVERY.equals(requestIdDto.getType())) {
                        deliveryParams.add(MyUtils.delivery(MyUtils.getNinetySecondsAgo(), requestIdDto.getTransactionId().toString(), requestIdDto.getOrganizationId().toString(),
                                requestIdDto.getSubInventory(), requestIdDto.getLocatorId() == null ? null : requestIdDto.getLocatorId().toString(),
                                requestIdDto.getLotNumber(), requestIdDto.getQuantity().toString(), requestIdDto.getShipmentNum()));
                        //修改交货数据并写入
                        requestIdDto.setAutoGrowingId(null);
                        requestIdDto.setRequestId(deliveryRequestIdAuto.getRequestId());
                        requestIdDto.setTransactionDate(DateUtils.getTime());
                        requestIdDto.setErrorMsg("WMS交货数据写入EBS接口失败");
                        requestIdDto.setState(TaskTypeConstant.FAIL_WAIT_MANAGE);
                        deliveryRequestIds.add(requestIdDto);
                    }
                    /*if (TaskTypeConstant.RECEIVE.equals(requestIdDto.getType())) {
                        receiveParams.add(MyUtils.receive(requestIdDto.getOrganizationId().toString(),requestIdDto.getPoHeaderId().toString(),
                                requestIdDto.getPoLineId().toString(),requestIdDto.getPoLineLocationId().toString(),requestIdDto.getPoDistributionId().toString(),
                                requestIdDto.getItemId().toString(),requestIdDto.getQuantity().toString(),requestIdDto.getReceiptDate(),requestIdDto.getLotNumber(),
                                requestIdDto.getExpirationDate(),requestIdDto.getOriginationDate()));
                        //修改数据并写入
                        requestIdDto.setAutoGrowingId(null);
                        requestIdDto.setRequestId(receiveRequestIdAuto.getRequestId());
                        requestIdDto.setTransactionDate(DateUtils.getTime());
                        requestIdDto.setErrorMsg("WMS交货数据写入EBS接口失败");
                        receiveRequestIds.add(requestIdDto);
                    }*/
                }else {
                    error = "只能勾选采购接收或者交货接口";
                    throw new RuntimeException();
                }
            }
            //交货
            if (deliveryParams.size() > 0) {
                requestIdService.delivery(deliveryRequestIds,deliveryParams,deliveryRequestIdAuto);
            }
            //接收
            /*if (receiveParams.size() > 0) {
                requestIdService.receive(null,receiveRequestIds,receiveParams,receiveRequestIdAuto);
            }*/
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

}

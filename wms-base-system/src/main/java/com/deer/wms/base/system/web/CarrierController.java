package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.service.CallAgvService;
import com.deer.wms.base.system.service.CarrierService;
import com.deer.wms.base.system.service.MESWebService.WebserviceResponse;
import com.deer.wms.base.system.service.ServerVisitAddressService;
import com.deer.wms.base.system.service.WarnInformationService;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.core.domain.AjaxResult;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.enums.BusinessType;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.poi.ExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.deer.wms.common.core.result.Result;
import com.deer.wms.common.core.result.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.ui.ModelMap;
import com.deer.wms.common.core.controller.BaseController;
import com.deer.wms.common.core.page.TableDataInfo;

import java.util.List;

/**
 * Created by  on 2019/10/11.
 */
@Controller
@RequestMapping("/carrier")
public class CarrierController extends BaseController {

    private String prefix = "carrier";

    @Autowired
    private CarrierService carrierService;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private WarnInformationService warnInformationService;
    @Autowired
    private CallAgvService callAgvService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    public String detail() {
        return prefix + "/detail";
    }

    @RequiresPermissions("carrier:view")
    @GetMapping()
    public String carrier() {
        return "manage/carrier/carrier";
    }

    /**
     * 修改
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap) {
        Carrier carrier = carrierService.findById(id);
        mmap.put("carrier", carrier);
        return prefix + "/edit";
    }

    /**
     * 新增
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }


    @PostMapping("/insert")
    @ResponseBody
    public Result add(@RequestBody Carrier carrier) {
        carrierService.save(carrier);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        carrierService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody Carrier carrier) {
        carrierService.update(carrier);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        Carrier carrier = carrierService.findById(id);
        return ResultGenerator.genSuccessResult(carrier);
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(CarrierCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<Carrier> list = carrierService.findAll();
        return getDataTable(list);
    }

    @PostMapping("/findList")
    @ResponseBody
    public TableDataInfo findList(CarrierCriteria criteria) {
        startPage();
        List<CarrierDto> list = carrierService.findList(criteria);
        return getDataTable(list);
    }

    @PostMapping("/callAgv")
    @ResponseBody
    public Result callAgv(@RequestBody CarrierCriteria criteria) {
        try {
            if (TaskTypeConstant.call_agv_state == 2) {
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "当前【AGV问题】：呼叫AGV异常-位置个数少于0, 不能入库。正在重复呼叫中，请勿手动！");
            }
            Carrier carrier = carrierService.findById(criteria.getCarrierId());
            if (carrier.getCarrierState().equals(2) || carrier.getCode() == null || carrier.getCode() == "") {
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "当前载具未绑定工单或不在排队状态，暂不允许呼叫AGV");
            }
            CallAgv callAgv = new CallAgv();
            WebserviceResponse webserviceResponse = serverVisitAddressService.requestMesServer("StockWipOutReq", carrier.getCode());
            callAgv.setMethodName("StockWipOutReq");
            callAgv.setCode(carrier.getCode());
            callAgv.setErrorCode(webserviceResponse.getErrorCode());
            callAgv.setTaskCode(webserviceResponse.getTaskCode() == null ? null : webserviceResponse.getTaskCode());
            callAgv.setErrorMsg("WMS工单完成,呼叫AGV接口。" + webserviceResponse.getErrorMsg());
            callAgv.setCreateTime(DateUtils.getTime());
            callAgvService.save(callAgv);
            if (webserviceResponse.getErrorMsg().equals("OK") && webserviceResponse.getErrorCode().equals("0")) {
                carrier.setTime(DateUtils.getTime());
                carrier.setCarrierState(2);
                carrierService.update(carrier);
            } else {
                WarnInformation warnInformation = new WarnInformation("呼叫AGV取载具失败：" + webserviceResponse.getErrorMsg(),
                        TaskTypeConstant.UNDEALT, TaskTypeConstant.CALL_AGV_ERROR, DateUtils.getTime());
                warnInformationService.save(warnInformation);
//                    WebSocketServer.sendInfo(webserviceResponse.getErrorMsg() + "，出料口呼叫AGV失败。", TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/AGVManualOut")
    @ResponseBody
    public Result AGVManualOut(@RequestBody CarrierCriteria criteria) {
        try {
            Carrier carrier = carrierService.findById(criteria.getCarrierId());
            if (carrier.getCarrierState().equals(2)) {
                return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE, "当前载具已是出库状态！");
            }
            carrier.setTime(DateUtils.getTime());
            carrier.setCarrierState(2);
            carrierService.update(carrier);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
        return ResultGenerator.genSuccessResult();
    }

    @Log(title = "导出任务列表", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:carrier:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(CarrierCriteria criteria)
    {
        criteria.setPageSize(10000);
        List<CarrierDto> list = carrierService.findList(criteria);
        ExcelUtil<CarrierDto> util = new ExcelUtil<CarrierDto>(CarrierDto.class);
        return util.exportExcel(list, "载具");
    }

}

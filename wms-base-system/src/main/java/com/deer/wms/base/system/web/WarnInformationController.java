package com.deer.wms.base.system.web;

import com.alibaba.fastjson.JSONArray;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.WarnInformation;
import com.deer.wms.base.system.model.WarnInformationCriteria;
import com.deer.wms.base.system.model.WarnInformationDto;
import com.deer.wms.base.system.model.task.AlarmClearUp;
import com.deer.wms.base.system.service.WarnInformationService;
import com.deer.wms.base.system.service.rabbitMQ.MsgProducer;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.core.domain.AjaxResult;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.enums.BusinessType;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.framework.web.domain.Server;
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

import java.io.IOException;
import java.util.List;

/**
* Created by  on 2020/01/06.
*/
@Controller
@RequestMapping("/warnInformation")
public class WarnInformationController  extends BaseController{

    private String prefix = "warnInformation";

    @Autowired
    private WarnInformationService warnInformationService;
    @Autowired
    private MsgProducer msgProducer;


    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("warnInformation:view")
    @GetMapping()
    public String warnInformation()
    {
        return "manage/alarm/alarm";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
    WarnInformation warnInformation = warnInformationService.findById(id);
        mmap.put("warnInformation", warnInformation);
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
    public Result add(@RequestBody WarnInformation warnInformation) {
        warnInformationService.save(warnInformation);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        warnInformationService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @Log(title = "删除报警信息", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public Result remove(@RequestBody WarnInformationCriteria warnInformationCriteria)
    {
        try
        {
            for(Integer id : warnInformationCriteria.getIds()){
                warnInformationService.deleteById(id);
            }
            return ResultGenerator.genSuccessResult();
        }
        catch (Exception e)
        {
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody WarnInformation warnInformation) {
        warnInformationService.update(warnInformation);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        WarnInformation warnInformation = warnInformationService.findById(id);
        return ResultGenerator.genSuccessResult(warnInformation);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(WarnInformationCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<WarnInformation> list = warnInformationService.findAll();
        return getDataTable(list);
    }

    @PostMapping("/findList")
    @ResponseBody
    public  TableDataInfo findList(WarnInformationCriteria criteria) {
        startPage();
        List<WarnInformationDto> list = warnInformationService.findList(criteria);
        return getDataTable(list);
    }

    @GetMapping("/alreadyFinish")
    @ResponseBody
    public Result alreadyFinish() {
        try {
            msgProducer.sendAlarm(JSONArray.toJSONString(
                    new AlarmClearUp("alarm", 0)));
            WebSocketServer.sendInfo("12", TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
        }catch(Exception io){
            io.printStackTrace();
            throw new ServiceException(CommonCode.GENERAL_WARING_CODE);
        }
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/manualManage")
    @ResponseBody
    @Transactional
    public Result manualManage(@RequestBody WarnInformationCriteria warnInformationCriteria)
    {
        String error = "服务器内部错误，请联系管理员";
        try{
            List<WarnInformationDto> warnInformationDtos = warnInformationService.findList(warnInformationCriteria);
            for(WarnInformationDto warnInformationDto : warnInformationDtos){
                if(!warnInformationDto.getState().equals(TaskTypeConstant.UNDEALT)){
                    error = "只能勾选未处理异常，请勿勾选已处理异常！";
                    throw new RuntimeException();
                }
                warnInformationDto.setFinishTime(DateUtils.getTime());
                warnInformationDto.setHandleCard(warnInformationCriteria.getLoginPersonCardNo());
                warnInformationDto.setState(TaskTypeConstant.ALREADY_MANGE_ALARM);
                warnInformationService.update(warnInformationDto);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
        return ResultGenerator.genSuccessResult();
    }

}

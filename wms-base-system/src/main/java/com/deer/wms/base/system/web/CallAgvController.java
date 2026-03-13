package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.CallAgv;
import com.deer.wms.base.system.model.CallAgvCriteria;
import com.deer.wms.base.system.service.CallAgvService;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.enums.BusinessType;
import com.deer.wms.common.exception.ServiceException;
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
* Created by  on 2020/01/14.
*/
@Controller
@RequestMapping("/callAgv")
public class CallAgvController  extends BaseController{

    private String prefix = "callAgv";

    @Autowired
    private CallAgvService callAgvService;



    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("callAgv:view")
    @GetMapping()
    public String callAgv()
    {
        return  "manage/wmsWithImesMutual/mutualRecord";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
    CallAgv callAgv = callAgvService.findById(id);
        mmap.put("callAgv", callAgv);
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
    public Result add(@RequestBody CallAgv callAgv) {
        callAgvService.save(callAgv);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long id) {
        callAgvService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody CallAgv callAgv) {
        callAgvService.update(callAgv);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Long id) {
        CallAgv callAgv = callAgvService.findById(id);
        return ResultGenerator.genSuccessResult(callAgv);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(CallAgvCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<CallAgv> list = callAgvService.findAll();
        return getDataTable(list);
    }

    @PostMapping("/findList")
    @ResponseBody
    public  TableDataInfo findList(CallAgvCriteria criteria) {
        startPage();
        List<CallAgv> list = callAgvService.findList(criteria);
        return getDataTable(list);
    }

    @Log(title = "删除报警信息", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public Result remove(@RequestBody CallAgvCriteria callAgvCriteria)
    {
        try
        {
            List<CallAgv> callAgvs = callAgvService.findList(new CallAgvCriteria("-1",1,100000));
            StringBuilder rs = new StringBuilder();
            for (int i =0;i<callAgvs.size();i++){
                CallAgv callAgv = callAgvs.get(i);
                rs.append(callAgv.getId()+",");
            }
            if(callAgvs.size()>0) {
                String ids = rs.substring(0, (rs.length() - 1));
                callAgvService.deleteByIds(ids);
            }
            return ResultGenerator.genSuccessResult();
        }
        catch (Exception e)
        {
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }

}

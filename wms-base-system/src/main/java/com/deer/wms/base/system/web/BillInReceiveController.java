package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.BillInReceive;
import com.deer.wms.base.system.model.BillInReceiveCriteria;
import com.deer.wms.base.system.service.BillInReceiveService;
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
* Created by  on 2020/09/16.
*/
@Controller
@RequestMapping("billInReceive")
public class BillInReceiveController  extends BaseController{

    private String prefix = "billInReceive";

    @Autowired
    private BillInReceiveService billInReceiveService;



    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("billInReceive:view")
    @GetMapping()
    public String billInReceive()
    {
        return prefix + "/billInReceive";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
    BillInReceive billInReceive = billInReceiveService.findById(id);
        mmap.put("billInReceive", billInReceive);
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
    public Result add(@RequestBody BillInReceive billInReceive) {
        billInReceiveService.save(billInReceive);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        billInReceiveService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody BillInReceive billInReceive) {
        billInReceiveService.update(billInReceive);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        BillInReceive billInReceive = billInReceiveService.findById(id);
        return ResultGenerator.genSuccessResult(billInReceive);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(BillInReceiveCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<BillInReceive> list = billInReceiveService.findAll();
        return getDataTable(list);
    }

}

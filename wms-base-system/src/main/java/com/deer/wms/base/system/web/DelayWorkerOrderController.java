package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.DelayWorkerOrder;
import com.deer.wms.base.system.model.DelayWorkerOrderCriteria;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.service.DelayWorkerOrderService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.common.annotation.Log;
import com.deer.wms.common.core.domain.AjaxResult;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.enums.BusinessType;
import com.deer.wms.common.exception.BusinessException;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.StringUtils;
import com.deer.wms.common.utils.poi.ExcelUtil;
import com.deer.wms.framework.util.ShiroUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
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
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
* Created by  on 2020/08/25.
*/
@Controller
@RequestMapping("/delayWorkerOrder")
public class DelayWorkerOrderController  extends BaseController{

    private String prefix = "delayWorkerOrder";

    @Autowired
    private DelayWorkerOrderService delayWorkerOrderService;
    @Autowired
    private IBoxItemService boxItemService;


    /**
    * 详情
    */
    @GetMapping("/detail")
    public String detail()
    {
        return prefix + "/detail";
    }

    @RequiresPermissions("delayWorkerOrder:view")
    @GetMapping()
    public String delayWorkerOrder()
    {
        return prefix + "/delayWorkerOrder";
    }

    /**
    * 修改
    */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
    DelayWorkerOrder delayWorkerOrder = delayWorkerOrderService.findById(id);
        mmap.put("delayWorkerOrder", delayWorkerOrder);
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


    @PostMapping("/add")
    @ResponseBody
    public Result add(@RequestBody DelayWorkerOrder delayWorkerOrder) {
        delayWorkerOrderService.save(delayWorkerOrder);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        delayWorkerOrderService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody DelayWorkerOrder delayWorkerOrder) {
        delayWorkerOrderService.update(delayWorkerOrder);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        DelayWorkerOrder delayWorkerOrder = delayWorkerOrderService.findById(id);
        return ResultGenerator.genSuccessResult(delayWorkerOrder);
    }

    @PostMapping("/list")
    @ResponseBody
    public  TableDataInfo list(DelayWorkerOrderCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<DelayWorkerOrder> list = delayWorkerOrderService.findAll();
        return getDataTable(list);
    }

    @Log(title = "导入延期工单号", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    @ResponseBody
    @Transactional
    public AjaxResult importData(MultipartFile file, @Param("loginPersonCardNo") String loginPersonCardNo) throws Exception
    {
        String message = "导入成功";
        ExcelUtil<DelayWorkerOrder> util = new ExcelUtil<DelayWorkerOrder>(DelayWorkerOrder.class);
        List<DelayWorkerOrder> delayWorkerOrders = util.importExcel(file.getInputStream());
        if (StringUtils.isNull(delayWorkerOrders) || delayWorkerOrders.size() == 0) {
            throw new BusinessException("导入数据不能为空！");
        }
        BoxItem boxItem = new BoxItem(TaskTypeConstant.POSTPONE);
        DelayWorkerOrderCriteria delayWorkerOrderCriteria = new DelayWorkerOrderCriteria();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(DelayWorkerOrder delayWorkerOrder : delayWorkerOrders){
            if(delayWorkerOrder.getWorkerOrderId().trim().equals("")
                    || delayWorkerOrder.getItemCode().trim().equals("")
                    || delayWorkerOrder.getExp().trim().equals("")
                    || delayWorkerOrder.getBatch().trim().equals("")
            ){
                throw new BusinessException("检测到信息为空，请修改后再次导入！");
            }
            Date date = new Date(delayWorkerOrder.getExp());
            delayWorkerOrder.setExp(simpleDateFormat.format(date));
            boxItem.setExp(delayWorkerOrder.getExp());
            boxItem.setBatch(delayWorkerOrder.getBatch());
            boxItem.setItemCode(delayWorkerOrder.getItemCode());
            //根据信息查找是否有此延期库存
            List<BoxItem> boxItems = boxItemService.selectBoxItemList(boxItem);
            if(boxItems.size() == 0 || boxItems.get(0).getQuantity()<=0){
                throw new BusinessException("库存无料号为"+delayWorkerOrder.getItemCode()+"，批次为"
                        +delayWorkerOrder.getBatch()+",延期日期为"+delayWorkerOrder.getExp()+"的延期库存！请修改后再次导入");
            }
            //根据工单Id查询是否已导入工单
            delayWorkerOrderCriteria.setWorkerOrderId(delayWorkerOrder.getWorkerOrderId());
            List<DelayWorkerOrder> delayWorkerOrderList = delayWorkerOrderService.findList(delayWorkerOrderCriteria);
            if(delayWorkerOrderList.size()>0){
                for(DelayWorkerOrder delayWorkerOrder1 : delayWorkerOrderList) {
                    if(!delayWorkerOrder1.getItemCode().equals(delayWorkerOrder.getItemCode())) {
                        throw new BusinessException("工单Id" + delayWorkerOrder.getWorkerOrderId() + "的料号与之前导入的料号"+delayWorkerOrder1.getItemCode()+"不一致");
                    }
                    if(delayWorkerOrder1.getBatch().equals(delayWorkerOrder.getBatch())) {
                        throw new BusinessException("工单Id为" + delayWorkerOrder.getWorkerOrderId() + "，批次"+delayWorkerOrder1.getBatch()+"已存在！请修改后导入！");
                    }
                }
            }
            delayWorkerOrder.setOperatorNo(loginPersonCardNo);
            delayWorkerOrderService.save(delayWorkerOrder);
        }
        return AjaxResult.success(message);
    }

    @Log(title = "导出延期库存工单", businessType = BusinessType.EXPORT)
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate()
    {
        ExcelUtil<DelayWorkerOrder> util = new ExcelUtil<DelayWorkerOrder>(DelayWorkerOrder.class);
        return util.importTemplateExcel("延期库存工单");
    }
}

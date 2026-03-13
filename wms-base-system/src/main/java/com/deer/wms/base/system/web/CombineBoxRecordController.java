package com.deer.wms.base.system.web;

import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.box.BoxInfo;
import com.deer.wms.base.system.model.box.BoxItem;
import com.deer.wms.base.system.model.box.BoxItemCriteria;
import com.deer.wms.base.system.model.box.BoxItemDto;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.service.CombineBoxRecordService;
import com.deer.wms.base.system.service.OperatorService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.core.text.Convert;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.common.utils.GuidUtils;
import com.deer.wms.framework.util.MyUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import sun.nio.cs.Surrogate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by  on 2019/11/04.
 */
@Controller
@RequestMapping("/combineBoxRecord")
public class CombineBoxRecordController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(CombineBoxRecordController.class);

    private String prefix = "combineBoxRecord";

    @Autowired
    private CombineBoxRecordService combineBoxRecordService;
    @Autowired
    private IBoxItemService boxItemService;
    @Autowired
    private ITaskInfoService taskInfoService;
    @Autowired
    private ICellInfoService cellInfoService;
    @Autowired
    private BoxInfoService boxInfoService;
    @Autowired
    private IItemInfoService itemInfoService;

    /**
     * 详情
     */
    @GetMapping("/detail")
    public String detail() {
        return prefix + "/detail";
    }

    @RequiresPermissions("combineBoxRecord:view")
    @GetMapping()
    public String combineBoxRecord() {
        return "manage/combineBox/combineBox";
    }

    @PostMapping("/findList")
    @ResponseBody
    public TableDataInfo findList(CombineBoxRecordCriteria criteria) {
        startPage();
        List<CombineBoxRecordDto> list = combineBoxRecordService.findList(criteria);
        return getDataTable(list);
    }

    /**
     * 修改
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap) {
        CombineBoxRecord combineBoxRecord = combineBoxRecordService.findById(id);
        mmap.put("combineBoxRecord", combineBoxRecord);
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
    public Result add(@RequestBody CombineBoxRecord combineBoxRecord) {
        combineBoxRecordService.save(combineBoxRecord);
        return ResultGenerator.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Integer id) {
        combineBoxRecordService.deleteById(id);
        return ResultGenerator.genSuccessResult();
    }

    @PostMapping("/update")
    @ResponseBody
    public Result update(@RequestBody CombineBoxRecord combineBoxRecord) {
        combineBoxRecordService.update(combineBoxRecord);
        return ResultGenerator.genSuccessResult();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Result detail(@PathVariable Integer id) {
        CombineBoxRecord combineBoxRecord = combineBoxRecordService.findById(id);
        return ResultGenerator.genSuccessResult(combineBoxRecord);
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(CombineBoxRecordCriteria criteria) {
        PageHelper.startPage(criteria.getPageNum(), criteria.getPageSize());
        List<CombineBoxRecord> list = combineBoxRecordService.findAll();
        return getDataTable(list);
    }

    @Autowired
    private OperatorService operatorService;

    @PostMapping("/clearMoBlockQuantity")
    @ResponseBody
    @Transactional
    public Result clearMoBlockQuantity(@RequestBody CombineBoxRecordCriteria combineBoxRecordCriteria) {

        System.out.println("clearMoBlockQuantity");
        log.info("clearMoBlockQuantity");

        BoxItemCriteria boxItemCriteria = new BoxItemCriteria();
        boxItemCriteria.setIds(combineBoxRecordCriteria.getIds());
        List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteria);

        for (BoxItemDto boxItemDto : boxItemDtos) {
            boxItemDto.setForecastStockQuantity(0);
            boxItemService.update(boxItemDto);
        }
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 点击合框按钮，堆垛机取可以合框物料的两框
     */
    @PostMapping("/combineBox")
    @ResponseBody
    @Transactional
    public Result combineBox(@RequestBody CombineBoxRecordCriteria combineBoxRecordCriteria) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            Operator operator = operatorService.findByCard(combineBoxRecordCriteria.getLoginPersonCardNo());
            BoxItemCriteria boxItemCriteria = new BoxItemCriteria();
            boxItemCriteria.setIds(combineBoxRecordCriteria.getIds());
            boxItemCriteria.setOrderByState(1003);
            List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteria);
            if (boxItemDtos.size() != 2) {
                error = "勾选数量有误";
                throw new RuntimeException();
            }
            String msg = cellInfoService.judgeBoxItemState(boxItemDtos);
            if (!msg.equals("success")) {
                error = msg;
                throw new RuntimeException();
            }
            BoxItemDto boxItemDto = boxItemDtos.get(0);
            BoxItemDto boxItemDto1 = boxItemDtos.get(1);
            if ((boxItemDto.getQuantity() + boxItemDto1.getQuantity()) > boxItemDto.getMaxPackQty())
            {
                Integer quantity = boxItemDto.getQuantity() + boxItemDto1.getQuantity();
                error = "合框后数量大于单框最大存储数量！当前两箱总数量为"+
                        quantity+"张" + "，最大存储数量为"+boxItemDto.getMaxPackQty()+"请重新选择！";
                throw new RuntimeException();
            }
            if (!boxItemDto.getItemCode().equals(boxItemDto1.getItemCode())) {
                error = "当前所选料号不一致！";
                throw new RuntimeException();
            }
            if (operator.getManyBatchPermission().equals(1)) {
                if (!boxItemDto.getBatch().equals(boxItemDto1.getBatch())) {
                    error = "当前批次不一致，您无多批次存储权限，请联系管理员开启！";
                    throw new RuntimeException();
                }
            }
            if (!boxItemDto.getSubInventoryId().equals(boxItemDto1.getSubInventoryId())) {
                error = "所选框子库不同，请重新选择！";
                throw new RuntimeException();
            }
            if (boxItemDto.getLockQuantity()>0 || boxItemDto1.getLockQuantity()>0
            ) {
                error = "选中箱有预测备料库存，请先解锁再进行合框！";
                throw new RuntimeException();
            }
            CombineBoxRecord combineBoxRecord = new CombineBoxRecord(boxItemDto.getBoxCode(), boxItemDto.getQuantity(),
                    boxItemDto1.getBoxCode(), boxItemDto1.getQuantity(), DateUtils.getTime(),
                    combineBoxRecordCriteria.getLoginPersonCardNo(),boxItemDto.getBatch(),boxItemDto1.getBatch(),boxItemDto.getItemCode());
            combineBoxRecord.setSubInventoryId(boxItemDto.getSubInventoryId());
            combineBoxRecord.setFromExp(boxItemDto.getExp());
            combineBoxRecord.setToExp(boxItemDto1.getExp());
            combineBoxRecordService.save(combineBoxRecord);
            for (BoxItemDto boxItemDtoTwo : boxItemDtos) {
                cellInfoService.updateCellStateAndBoxStateAndSendTaskInfo(boxItemDtoTwo,null,boxItemCriteria.getLoginPersonCardNo());
            }
            return ResultGenerator.genSuccessResult(boxItemDto.getQuantity());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(CommonCode.PARAMETER_ERROR, error);
        }
    }

    /**
     * 点击合框按钮，堆垛机取可以合框物料的两框
     */
    @PostMapping("/afterCombineBoxBillIn")
    @ResponseBody
    @Transactional
    public Result afterCombineBoxBillIn(@RequestBody CombineBoxRecordCriteria combineBoxRecordCriteria) {
        String error = "服务器内部错误，请联系管理员";
        try {
            if(taskInfoService.judgeWhetherCheckTaskInfo()){
                error = "盘点中，请勿下发其他任务!";
                throw new RuntimeException();
            }
            BoxItem boxItem = boxItemService.getBoxItemByBoxCode(combineBoxRecordCriteria.getBoxCode());
            ItemInfo itemInfo = itemInfoService.findByItemCode(boxItem.getItemCode());
            Integer combineQuantity = combineBoxRecordCriteria.getQuantity()+boxItem.getQuantity();
            if(combineQuantity > itemInfo.getMaxPackQty()){
                error = "超过单箱最大可存储数量！";
            }
            boxItem.setQuantity(combineQuantity);
            boxItemService.update(boxItem);
            cellInfoService.inAvailableBox(boxItem, combineBoxRecordCriteria.getLoginPersonCardNo(), null, itemInfo.getItemName());
            return ResultGenerator.genSuccessResult();
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR,error);
        }
    }
}

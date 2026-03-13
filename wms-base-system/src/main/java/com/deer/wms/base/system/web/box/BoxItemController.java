package com.deer.wms.base.system.web.box;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.box.*;
import com.deer.wms.base.system.model.item.ItemInfo;
import com.deer.wms.base.system.model.task.TaskInfo;
import com.deer.wms.base.system.model.ware.CellInfo;
import com.deer.wms.base.system.model.ware.CellInfoDto;
import com.deer.wms.base.system.service.ServerVisitAddressService;
import com.deer.wms.base.system.service.SubinventoryTransferRecordService;
import com.deer.wms.base.system.service.box.BoxInfoService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.base.system.service.item.IItemInfoService;
import com.deer.wms.base.system.service.task.ITaskInfoService;
import com.deer.wms.base.system.service.ware.ICellInfoService;
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
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.omg.SendingContext.RunTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组盘 信息操作处理
 * 
 * @author guo
 * @date 2019-06-03
 */
@Controller
@RequestMapping("/in/boxItem")
public class BoxItemController extends BaseController
{
    private String prefix = "in/boxItem";
	
	@Autowired
	private IBoxItemService boxItemService;
	@Autowired
	private SubinventoryTransferRecordService subinventoryTransferRecordService;
	@Autowired
	private ServerVisitAddressService serverVisitAddressService;
	@Autowired
	private BoxInfoService boxInfoService;
	@Autowired
	private ITaskInfoService taskInfoService;
	@Autowired
	private ICellInfoService cellInfoService;
	@Autowired
	private IItemInfoService itemInfoService;
	
	@RequiresPermissions("in:boxItem:view")
	@GetMapping("/page")
	public String boxItem()
	{
	    return prefix + "/boxItem";
	}

	@RequiresPermissions("in:inventoryManage:view")
	@GetMapping("/inventoryManagePage")
	public String inventoryManage()
	{
		return prefix + "/inventoryManage";
	}


	/**
	 *
	 * 关联查询托盘信息
	 *
	 *
	 */

	@PostMapping("/findList")
	@ResponseBody
	public TableDataInfo findList(BoxItemCriteria boxItemCriteria)
	{
		startPage();
		List<BoxItemDto> list = boxItemService.selectBoxItemDtoList(boxItemCriteria);
		return getDataTable(list);
	}

	@PostMapping("/selectList")
	@ResponseBody
	public TableDataInfo selectList(BoxItemCriteria boxItemCriteria)
	{
		if(boxItemCriteria.getType() != null && boxItemCriteria.getType().equals(1)){
			if(!boxItemCriteria.getItemCode().equals("")){
				throw new ServiceException(CommonCode.GENERAL_WARING_CODE, "差异库存查询，请勿输入其他条件查询");
			}
			if(!boxItemCriteria.getBatch().equals("")){
				throw new ServiceException(CommonCode.GENERAL_WARING_CODE, "差异库存查询，请勿输入其他条件查询");
			}
			if(!boxItemCriteria.getSubInventoryId().equals(-1)){
				throw new ServiceException(CommonCode.GENERAL_WARING_CODE, "差异库存查询，请勿输入其他条件查询");
			}
			boxItemCriteria.setPageNum(null);
			boxItemCriteria.setPageSize(null);
			List<InventoryCompare> list = boxItemService.selectList(boxItemCriteria);
			List<InventoryCompare> list2 = new ArrayList<>();
			if (list.size() > 0) {
				EbsBack entityStr = getEBSStock(list);
				if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
					JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
					for (int i = 0; i < list.size(); i++) {
						InventoryCompare inventoryCompare = list.get(i);
						Bloop:
						for (int j = 0; j < jsonArrays.size(); j++) {
							JSONObject jsonObject = jsonArrays.getJSONObject(j);
							Integer inventoryItemId = MyUtils.backInteger(jsonObject.get("inventoryItemId"));
							String lotNumber = MyUtils.backString(jsonObject.get("lotNumber"));
							String subinventoryCode = MyUtils.backString(jsonObject.get("subinventoryCode"));
							if (inventoryCompare.getInventoryItemId().equals(inventoryItemId)
									&& inventoryCompare.getBatch().equals(lotNumber)
									&& inventoryCompare.getSubInventoryCode().equals(subinventoryCode)
							) {
								Integer transactionQuantity = MyUtils.backDouble(jsonObject.get("transactionQuantity"));
								Integer differentQuantity = (transactionQuantity == null ? 0 : transactionQuantity) - inventoryCompare.getQuantity();
								if(differentQuantity < 0 || differentQuantity > 0){
									list.get(i).setEbsQuantity(transactionQuantity == null ? 0 : transactionQuantity);
									list.get(i).setDifferenceQuantity(differentQuantity);
									list2.add(list.get(i));
								}
								break Bloop;
							}
							else if (j == (jsonArrays.size() - 1)) {
								list.get(i).setEbsQuantity(0);
								list.get(i).setDifferenceQuantity(0 - inventoryCompare.getQuantity());
								list2.add(list.get(i));
							}
						}
					}
				} else {
					throw new ServiceException(CommonCode.GENERAL_WARING_CODE, "访问EBS失败！");
				}
			}
			return getDataTable(list2);
		}
		else {
			startPage();
			List<InventoryCompare> list = boxItemService.selectList(boxItemCriteria);
			if (list.size() > 0) {
				EbsBack entityStr = getEBSStock(list);
				if (entityStr != null && entityStr.getSuccess().equals("true") && entityStr.getTotal() > 0) {
					JSONArray jsonArrays = JSONArray.parseArray(entityStr.getRows());
					for (int i = 0; i < list.size(); i++) {
						InventoryCompare inventoryCompare = list.get(i);
						Bloop:
						for (int j = 0; j < jsonArrays.size(); j++) {
							JSONObject jsonObject = jsonArrays.getJSONObject(j);
							Integer inventoryItemId = MyUtils.backInteger(jsonObject.get("inventoryItemId"));
							String lotNumber = MyUtils.backString(jsonObject.get("lotNumber"));
							String subinventoryCode = MyUtils.backString(jsonObject.get("subinventoryCode"));
							if (inventoryCompare.getInventoryItemId().equals(inventoryItemId)
									&& inventoryCompare.getBatch().equals(lotNumber)
									&& inventoryCompare.getSubInventoryCode().equals(subinventoryCode)
							) {
								Integer transactionQuantity = MyUtils.backDouble(jsonObject.get("transactionQuantity"));
								list.get(i).setEbsQuantity(transactionQuantity == null ? 0 : transactionQuantity);
								list.get(i).setDifferenceQuantity((transactionQuantity == null ? 0 : transactionQuantity) - inventoryCompare.getQuantity());
								break Bloop;
							} else if (j == (jsonArrays.size() - 1)) {
								list.get(i).setEbsQuantity(0);
								list.get(i).setDifferenceQuantity(0 - inventoryCompare.getQuantity());
							}
						}
					}
				} else {
					throw new ServiceException(CommonCode.GENERAL_WARING_CODE, "访问EBS失败！");
				}
			}
			return getDataTable(list);
		}
	}

	private EbsBack getEBSStock(List<InventoryCompare> list){
		String itemCodes = "";
		String subinventoryCodes = "";
		String batch = "";
		for (int i = 0; i < list.size(); i++) {
			InventoryCompare inventoryCompare = list.get(i);
			if (i == list.size() - 1) {
				itemCodes += inventoryCompare.getItemCode();
				subinventoryCodes += inventoryCompare.getSubInventoryCode();
				batch += inventoryCompare.getBatch();
			} else {
				itemCodes += inventoryCompare.getItemCode() + ",";
				subinventoryCodes += inventoryCompare.getSubInventoryCode() + ",";
				batch += inventoryCompare.getBatch() + ",";
			}
		}
		EbsBack entityStr = serverVisitAddressService.requestServerCodeSelectInventory(
				MyUtils.createSelectEBSStockBusinessData(itemCodes, subinventoryCodes, null, batch));
		return entityStr;
	}

	/**
	 * 查询组盘列表
	 */
	@RequiresPermissions("in:boxItem:list")
	@PostMapping("/list")
	@ResponseBody
	public TableDataInfo list(BoxItem boxItem)
	{
		startPage();
        List<BoxItem> list = boxItemService.selectBoxItemList(boxItem);
		return getDataTable(list);
	}
	
	
	/**
	 * 导出组盘列表
	 */
	@RequiresPermissions("in:boxItem:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BoxItem boxItem)
    {
    	List<BoxItem> list = boxItemService.selectBoxItemList(boxItem);
        ExcelUtil<BoxItem> util = new ExcelUtil<BoxItem>(BoxItem.class);
        return util.exportExcel(list, "boxItem");
    }
	
	/**
	 * 新增组盘
	 */
	@GetMapping("/add")
	public String add()
	{
	    return prefix + "/add";
	}
	
	/**
	 * 新增保存组盘
	 */
	@RequiresPermissions("in:boxItem:add")
	@Log(title = "组盘", businessType = BusinessType.INSERT)
	@PostMapping("/add")
	@ResponseBody
	public AjaxResult addSave(BoxItem boxItem)
	{		
		return toAjax(boxItemService.insertBoxItem(boxItem));
	}

	/**
	 * 修改组盘
	 */
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Integer id, ModelMap mmap)
	{
		BoxItem boxItem = boxItemService.selectBoxItemById(id);
		mmap.put("boxItem", boxItem);
	    return prefix + "/edit";
	}
	
	/**
	 * 修改保存组盘
	 */
	@RequiresPermissions("in:boxItem:edit")
	@Log(title = "组盘", businessType = BusinessType.UPDATE)
	@PostMapping("/edit")
	@ResponseBody
	public AjaxResult editSave(BoxItem boxItem)
	{		
		return toAjax(boxItemService.updateBoxItem(boxItem));
	}
	
	/**
	 * 删除组盘
	 */
	@RequiresPermissions("in:boxItem:remove")
	@Log(title = "组盘", businessType = BusinessType.DELETE)
	@PostMapping( "/remove")
	@ResponseBody
	public AjaxResult remove(String ids)
	{		
		return toAjax(boxItemService.deleteBoxItemByIds(ids));
	}


	/**
	 * 品质异常检验
	 */
//	@RequiresPermissions("in:boxItem:checkOut")
	@Log(title = "检验", businessType = BusinessType.OTHER)
	@PostMapping( "/qualityAbnormalCheck")
	@ResponseBody
	@Transactional
	public Result qualityAbnormalCheck(@RequestBody BoxItemCriteria boxItemCriteria)
	{
		String error = "服务器内部错误，请联系管理员";
		try {
			if(taskInfoService.judgeWhetherCheckTaskInfo()){
				error = "盘点中，请勿下发其他任务!";
				throw new RuntimeException();
			}
			List<BoxItemDto> boxItemDtos = boxItemService.findList(boxItemCriteria);
			String bool = cellInfoService.judgeBoxItemState(boxItemDtos);
			if (!bool.equals("success")) {
				error = bool;
				throw new RuntimeException();
			}
			cellInfoService.updateCellStateAndBoxStateAndSendTaskInfo(boxItemDtos.get(0), null, boxItemCriteria.getLoginPersonCardNo());
		}catch(Exception e){
			e.printStackTrace();
			throw new ServiceException(CommonCode.GENERAL_WARING_CODE,error);
		}
		return ResultGenerator.genSuccessResult();
	}

	//检验入库后
	@PostMapping( "/billInAfterQualityAbnormalCheck")
	@ResponseBody
	@Transactional
	public Result billInAfterQualityAbnormalCheck(@RequestBody BoxItemCriteria boxItemCriteria)
	{
		try {
			if(taskInfoService.judgeWhetherCheckTaskInfo()){
				return ResultGenerator.genFailResult(CommonCode.GENERAL_WARING_CODE,"盘点中，请勿下发其他任务!");
			}
			BoxItem boxItem = boxItemService.getBoxItemByBoxCode(boxItemCriteria.getBoxCode());
			ItemInfo itemInfo = itemInfoService.findByItemCode(boxItem.getItemCode());
			cellInfoService.inAvailableBox(boxItem, boxItemCriteria.getLoginPersonCardNo(), null, itemInfo.getItemName());
		}catch(Exception e){
			e.printStackTrace();
			throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
		}
		return ResultGenerator.genSuccessResult();
	}

}

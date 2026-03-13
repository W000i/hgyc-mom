package com.deer.wms.base.system.service.box.impl;

import com.deer.wms.base.system.dao.box.BoxItemMapper;
import com.deer.wms.base.system.model.DelayWorkerOrder;
import com.deer.wms.base.system.model.DelayWorkerOrderCriteria;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.box.*;
import com.deer.wms.base.system.model.threeDimensional.Box;
import com.deer.wms.base.system.service.DelayWorkerOrderService;
import com.deer.wms.base.system.service.box.IBoxItemService;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.common.core.text.Convert;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 组盘 服务层实现
 * 
 * @author guo
 * @date 2019-06-03
 */
@Service
public class BoxItemServiceImpl extends AbstractService<BoxItem, String> implements IBoxItemService
{
	@Autowired
	private BoxItemMapper boxItemMapper;

	@Autowired
	private DelayWorkerOrderService delayWorkerOrderService;

	/**
	 *	关联查询托盘相关信息
	 *
	 * @return
	 */
	@Override
	public List<BoxItemDto> selectBoxItemDtoList(BoxItemCriteria boxItemCriteria) {

		return boxItemMapper.selectBoxItemDtoList(boxItemCriteria);
	}

	/**
	 * 根据托盘编码寻找所有在货位上的托盘信息  便于统计数据
	 *
	 * @param itemCode
	 * @return
	 */
	@Override
	public List<BoxItemDto> getBoxItemDtoByitemCode(String itemCode) {

		return boxItemMapper.getBoxItemDtoByitemCode(itemCode);
	}

	/**
	 * 根据任务id查询托盘信息
	 *
	 * @param taskId
	 * @return
	 */
	@Override
	public BoxItem getBoxItemByTaskId(String taskId) {

		return boxItemMapper.getBoxItemByTaskId(taskId);
	}

	/**
	 * 根据物料编码查询货位信息  (根据批次排序， 保证先进先出)
	 *
	 * @param itemCode
	 * @return
	 */
	@Override
	public List<BoxItemDto> getFullCellInfoForOutOfStock(String itemCode) {

		/*return boxItemMapper.getFullCellInfoForOutOfStock(itemCode);*/
		return null;
	}

	/**
	 *
	 * 出库任务，寻找合适的出库货位(根据货物编码)
	 *
	 * @param itemCode
	 * @param quantity
	 * @return   返回类型集合， 当最早批次货位货物数量不够时，继续用别的货位,一直不够就一直加，直到所有货位都出货
	 */
	@Override
	public List<BoxItemDto> getFullCellInfoForOutOfStockForSaveTaskInfo(@Param("itemCode") String itemCode,@Param("quantity") Integer quantity) {
		/*List<BoxItemDto> boxItemDtos = boxItemService.getFullCellInfoForOutOfStock(itemCode);
		if(boxItemDtos != null){
			Integer quantitys = 0;
			int count = 0;
			for(BoxItemDto boxItemDto : boxItemDtos){
				quantitys += boxItemDto.getQuantity();
				//当需要的数量<可取出的数量
				if(quantity < quantitys){
					//如果第一次循环就也满足上面条件，则就取第一个元素作为新集合返回
					if(count == 0){
						List<BoxItemDto> fistBoxItemDto = new ArrayList<BoxItemDto>();
						fistBoxItemDto.add(boxItemDto);
						return fistBoxItemDto;
					}
					//如果超过1次循环,则截取一个新的集合返回
					List<BoxItemDto> newBoxItemDtos = boxItemDtos.subList(0,count+1);
					return newBoxItemDtos;
				}
				count++;
				//如果计已经达到集合长度，表示货物不足， 直接返回该集合，全出
				if(count == boxItemDtos.size()){
					return boxItemDtos;
				}
			}
		}*/
		return null;
	}

	/**
	 * 根据 托盘编码查找托盘
	 *
	 * @param boxCode
	 * @return
	 */
	@Override
	public BoxItem getBoxItemByBoxCode(String boxCode) {

		return boxItemMapper.getBoxItemByBoxCode(boxCode);
	}

	/**
     * 查询组盘信息
     * 
     * @param id 组盘ID
     * @return 组盘信息
     */
    @Override
	public BoxItem selectBoxItemById(Integer id)
	{
	    return boxItemMapper.selectBoxItemById(id);
	}
	
	/**
     * 查询组盘列表
     * 
     * @param boxItem 组盘信息
     * @return 组盘集合
     */
	@Override
	public List<BoxItem> selectBoxItemList(BoxItem boxItem)
	{
	    return boxItemMapper.selectBoxItemList(boxItem);
	}
	
    /**
     * 新增组盘
     * 
     * @param boxItem 组盘信息
     * @return 结果
     */
	@Override
	public int insertBoxItem(BoxItem boxItem)
	{
	    return boxItemMapper.insertBoxItem(boxItem);
	}
	
	/**
     * 修改组盘
     * 
     * @param boxItem 组盘信息
     * @return 结果
     */
	@Override
	public int updateBoxItem(BoxItem boxItem)
	{
	    return boxItemMapper.updateBoxItem(boxItem);
	}

	@Override
	public void deleteByBoxCode(String boxCode){
		boxItemMapper.deleteByBoxCode(boxCode);
	}

	/**
     * 删除组盘对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	@Override
	public int deleteBoxItemByIds(String ids)
	{
		return boxItemMapper.deleteBoxItemByIds(Convert.toStrArray(ids));
	}

	/**
	 * 根据id升序查找第一个可合框物料
	 */
	public BoxItem findOneCombineBoxGroupByItemCodeAndBatch(){
		return boxItemMapper.findOneCombineBoxGroupByItemCodeAndBatch();
	}

	public List<BoxItemDto> findMoreCombineBoxByItemCodeAndBatch(BoxItem boxItem){
		return boxItemMapper.findMoreCombineBoxByItemCodeAndBatch(boxItem);
	}

	public List<BoxItemDto> findByWorkerOrder(BoxItemCriteria criteria){
		return boxItemMapper.findByWorkerOrder(criteria);
	}

	/**
	 * 跟物料编码查询合适的托盘进行预测备料
	 * @return
	 */
	@Override
	public List<BoxItemDto> findSuitByItemCodeAndQuantity(@Param("itemCode") String itemCode,@Param("quantity") Integer quantity,@Param("workerOrderId")String workerOrderId) {
		//定义sql查询接收
		List<BoxItemDto> boxItemDtos = null;
		//定义查询参数
		BoxItemCriteria boxItemCriteria = new BoxItemCriteria();
		boxItemCriteria.setItemCode(itemCode);
		//定义返回值
		List<BoxItemDto> lists = new ArrayList<BoxItemDto>();
		//如果不进行延期管控
		if(TaskTypeConstant.workerOrderIssueTime.getDelayControl().equals(1)){
			boxItemCriteria.setOrderByState(901);
			//查询可出数量
			boxItemDtos = boxItemMapper.getFullCellInfoForOutOfStock(boxItemCriteria);
			//如果大于0
			if(boxItemDtos.size()>0){
				CalculateReturnedValue calculateReturnedValue = calculate(lists,boxItemDtos,quantity);
				if(calculateReturnedValue.getQuantity()>0) {
					return null;
				}else{
					return calculateReturnedValue.getBoxItemDtos();
				}
			}
		}
		//进行延期管控
		else{
			//根据工单ID查询绑定延期库存
			List<DelayWorkerOrder> delayWorkerOrderList = delayWorkerOrderService.findList(new DelayWorkerOrderCriteria(workerOrderId));
			List<String> batchs = new ArrayList<>();
			if(delayWorkerOrderList.size()>0){
				for(DelayWorkerOrder delayWorkerOrder : delayWorkerOrderList) {
					batchs.add(delayWorkerOrder.getBatch());
				}
			}
			//查询匹配工单ID的延期物料
			boxItemCriteria.setOrderByState(902);
			if(batchs.size()>0) {
				boxItemCriteria.setBatchs(batchs);
			}
			boxItemDtos = boxItemMapper.getFullCellInfoForOutOfStock(boxItemCriteria);
			//如果延期物料中没查到相应匹配的工单则查询合格库存的物料
			if(delayWorkerOrderList.size()<=0){
				//查询合格库的库存
				boxItemCriteria.setOrderByState(903);
				boxItemDtos = boxItemMapper.getFullCellInfoForOutOfStock(boxItemCriteria);
				//如果合格库有库存执行以下方法，没有返回null
				if(boxItemDtos.size()>0){
					CalculateReturnedValue calculateReturnedValue = calculate(lists,boxItemDtos,quantity);
					if(calculateReturnedValue.getQuantity()>0) {
						return null;
					}else{
						return calculateReturnedValue.getBoxItemDtos();
					}
				}
			}
			//如果延期物料有物料可以出
			else{
				//执行计算方法
				CalculateReturnedValue calculateReturnedValue = calculate(lists,boxItemDtos,quantity);
				//判断计算后可出数量是否满足可出数量
				//如果数量大于0表示延期可出数量不满足需求数量
				if(calculateReturnedValue.getQuantity()>0){
					//查询合格库库存
					boxItemCriteria.setWorkerOrderId(null);
					boxItemCriteria.setOrderByState(903);
					boxItemDtos = boxItemMapper.getFullCellInfoForOutOfStock(boxItemCriteria);
					//给list赋值，吧当前延期库存放入 list中
					lists = calculateReturnedValue.getBoxItemDtos();
					//如果合格库有库存执行以下方法，没有返回null
					if(boxItemDtos.size()>0){
						CalculateReturnedValue calculateReturnedValue1 = calculate(lists,boxItemDtos,quantity);
						if(calculateReturnedValue1.getQuantity()>0) {
							return null;
						}else{
							return calculateReturnedValue1.getBoxItemDtos();
						}
					}else{
						return lists;
					}
				}else{
					//可出数量满足需求数量，直接返回
					return calculateReturnedValue.getBoxItemDtos();
				}

			}
		}
		return null;
	}

	private CalculateReturnedValue calculate(List<BoxItemDto> lists, List<BoxItemDto> boxItemDtos, Integer quantity){
		CalculateReturnedValue calculateReturnedValue = new CalculateReturnedValue();
		if(boxItemDtos != null && boxItemDtos.size()>0){
			Integer quantitys = 0;
			for(int i=0;i<boxItemDtos.size();i++){
				//计算单箱可出数量
				BoxItemDto boxItemDto = boxItemDtos.get(i);
				quantitys = boxItemDto.getQuantity()-boxItemDto.getForecastStockQuantity()-(boxItemDto.getLockQuantity()==null?0:boxItemDto.getLockQuantity());
				//当需求的数量小于等于托盘中可出数量
				if(quantity<=quantitys){
					//截取此集合返回
					lists.add(boxItemDto);
					calculateReturnedValue.setBoxItemDtos(lists);
					calculateReturnedValue.setQuantity(0);
					return calculateReturnedValue;
				}else{
					//当可出数量小于需求数量时，需求数量减去可出数量，用来判断下一筐
					lists.add(boxItemDto);
					quantity -= quantitys;
				}
			}
			//当循环完之后需求数量大于0,返回整个查询到的集合和剩余需求数量
			calculateReturnedValue.setBoxItemDtos(lists);
			calculateReturnedValue.setQuantity(quantity);
			return calculateReturnedValue;
		}
		return null;
	}

	@Override
	public List<BoxItemDto> findList(BoxItemCriteria criteria){
		return boxItemMapper.findList(criteria);
	}

	@Override
	public List<BoxItemDto> findSluggishOverdue(BoxItemCriteria criteria){
		return boxItemMapper.findSluggishOverdue(criteria);
	}

	@Override
	public List<BoxItemDto> findWillOverdue(BoxItemCriteria criteria){
		return boxItemMapper.findWillOverdue(criteria);
	}

	@Override
	public List<BoxItemDto> findBoxItemList(BoxItemCriteria criteria){
		return boxItemMapper.findBoxItemList(criteria);
	}

	@Override
	public List<BoxItemDto> workerOrderLackOut(BoxItemCriteria criteria){
		return boxItemMapper.workerOrderLackOut(criteria);
	}

	@Override
	public 	List<UnqualifiedOverTakeCanDelayDays> findUnqualifiedOverTakeCanDelayDays(BoxItemCriteria criteria)
	{
		return boxItemMapper.findUnqualifiedOverTakeCanDelayDays(criteria);
	}

	@Override
	public Integer totalQuantity(){
		return boxItemMapper.totalQuantity();
	}

	@Override
	public List<Box> findByCellId(BoxItemCriteria criteria){
		return boxItemMapper.findByCellId(criteria);
	}

	@Override
	public List<InventoryCompare> selectList(BoxItemCriteria criteria){
		return boxItemMapper.selectList(criteria);
	}

	@Override
	public List<BoxItemDto> findListTwo(BoxItemCriteria criteria){
		return boxItemMapper.findListTwo(criteria);
	}
}

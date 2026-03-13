package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.InventoryCheck;
import com.deer.wms.base.system.model.InventoryCheckCriteria;
import com.deer.wms.base.system.model.InventoryCheckDto;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface InventoryCheckMapper extends Mapper<InventoryCheck> {
    InventoryCheck findByBillOutDetailAndType(InventoryCheckCriteria inventoryCheckCriteria);

    List<InventoryCheckDto> findList(InventoryCheckCriteria inventoryCheckCriteria);
}
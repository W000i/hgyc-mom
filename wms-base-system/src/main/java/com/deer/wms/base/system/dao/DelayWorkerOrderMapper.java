package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.DelayWorkerOrder;
import com.deer.wms.base.system.model.DelayWorkerOrderCriteria;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface DelayWorkerOrderMapper extends Mapper<DelayWorkerOrder> {
    List<DelayWorkerOrder> findList(DelayWorkerOrderCriteria criteria);
}
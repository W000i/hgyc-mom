package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.DelayWorkerOrder;
import com.deer.wms.base.system.model.DelayWorkerOrderCriteria;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2020/08/25.
 */
public interface DelayWorkerOrderService extends Service<DelayWorkerOrder, Integer> {
    List<DelayWorkerOrder> findList(DelayWorkerOrderCriteria criteria);
}

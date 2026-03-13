package com.deer.wms.base.system.service.impl;

import com.deer.wms.base.system.dao.DelayWorkerOrderMapper;
import com.deer.wms.base.system.model.DelayWorkerOrder;
import com.deer.wms.base.system.model.DelayWorkerOrderCriteria;
import com.deer.wms.base.system.service.DelayWorkerOrderService;


import com.deer.wms.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by  on 2020/08/25.
 */
@Service
@Transactional
public class DelayWorkerOrderServiceImpl extends AbstractService<DelayWorkerOrder, Integer> implements DelayWorkerOrderService {

    @Autowired
    private DelayWorkerOrderMapper delayWorkerOrderMapper;


    @Override
    public List<DelayWorkerOrder> findList(DelayWorkerOrderCriteria criteria) {
        return delayWorkerOrderMapper.findList(criteria);
    }
}

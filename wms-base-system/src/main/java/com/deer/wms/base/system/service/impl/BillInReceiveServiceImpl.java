package com.deer.wms.base.system.service.impl;

import com.deer.wms.base.system.dao.BillInReceiveMapper;
import com.deer.wms.base.system.model.BillInReceive;
import com.deer.wms.base.system.model.BillInReceiveCriteria;
import com.deer.wms.base.system.service.BillInReceiveService;


import com.deer.wms.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by  on 2020/09/16.
 */
@Service
@Transactional
public class BillInReceiveServiceImpl extends AbstractService<BillInReceive, Integer> implements BillInReceiveService {

    @Autowired
    private BillInReceiveMapper billInReceiveMapper;

    @Override
    public List<BillInReceive> findList(BillInReceiveCriteria billInReceiveCriteria){
        return billInReceiveMapper.findList(billInReceiveCriteria);
    }
}

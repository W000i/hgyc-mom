package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.BillInReceive;
import com.deer.wms.base.system.model.BillInReceiveCriteria;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface BillInReceiveMapper extends Mapper<BillInReceive> {

    List<BillInReceive> findList(BillInReceiveCriteria billInReceiveCriteria);
}
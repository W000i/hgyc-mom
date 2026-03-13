package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.CallAgv;
import com.deer.wms.base.system.model.CallAgvCriteria;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface CallAgvMapper extends Mapper<CallAgv> {
    List<CallAgv> findList(CallAgvCriteria callAgvCriteria);
}
package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.CombineBoxRecord;
import com.deer.wms.base.system.model.CombineBoxRecordCriteria;
import com.deer.wms.base.system.model.CombineBoxRecordDto;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface CombineBoxRecordMapper extends Mapper<CombineBoxRecord> {
    List<CombineBoxRecordDto> findList(CombineBoxRecordCriteria combineBoxRecordCriteria);
}
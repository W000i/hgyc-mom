package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.ProcessRecord;
import com.deer.wms.base.system.model.ProcessRecordCriteria;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface ProcessRecordMapper extends Mapper<ProcessRecord> {
    List<ProcessRecord> findList(ProcessRecordCriteria criteria);
}
package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.ProcessRecord;
import com.deer.wms.base.system.model.ProcessRecordCriteria;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2019/12/03.
 */
public interface ProcessRecordService extends Service<ProcessRecord, Integer> {
    List<ProcessRecord> findList(ProcessRecordCriteria criteria);
}

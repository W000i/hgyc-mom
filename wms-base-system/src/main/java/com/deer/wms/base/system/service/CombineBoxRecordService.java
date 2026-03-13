package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.CombineBoxRecord;
import com.deer.wms.base.system.model.CombineBoxRecordCriteria;
import com.deer.wms.base.system.model.CombineBoxRecordDto;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2019/11/04.
 */
public interface CombineBoxRecordService extends Service<CombineBoxRecord, Integer> {
    List<CombineBoxRecordDto> findList(CombineBoxRecordCriteria combineBoxRecordCriteria);

}

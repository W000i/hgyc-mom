package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.CallAgv;
import com.deer.wms.base.system.model.CallAgvCriteria;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2020/01/14.
 */
public interface CallAgvService extends Service<CallAgv, Long> {
    List<CallAgv> findList(CallAgvCriteria callAgvCriteria);
}

package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.BillInReceive;
import com.deer.wms.base.system.model.BillInReceiveCriteria;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2020/09/16.
 */
public interface BillInReceiveService extends Service<BillInReceive, Integer> {
    List<BillInReceive> findList(BillInReceiveCriteria billInReceiveCriteria);
}

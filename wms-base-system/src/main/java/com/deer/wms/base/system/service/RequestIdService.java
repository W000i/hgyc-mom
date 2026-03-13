package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.*;
import com.deer.wms.common.core.service.Service;

import java.util.List;
import java.util.Map;


/**
 * Created by  on 2019/12/26.
 */
public interface RequestIdService extends Service<RequestId, Integer> {
    List<RequestIdDto> selectList(RequestIdCriteria criteria);

    List<RequestIdDto> findProcessing(RequestIdCriteria requestIdCriteria);

    void subInventoryTransfer(RequestIdAuto requestIdAuto,List<RequestId> requestIds, List<Map<String,String>> lists) throws Exception;

    void inventoryMinus(List<RequestId> requestIds, RequestIdAuto requestIdAuto, List<Map<String,String>> lists) throws Exception;

    void delivery(List<RequestId> requestIds, List<Map<String,String>> lists, RequestIdAuto requestIdAuto) throws Exception;

    void accountAliasOut(List<Map<String,String>> lists, List<RequestId> requestIds, RequestIdAuto requestIdAuto) throws Exception;

    String receive(List<BillInReceive> billInReceives,List<BillInRecordDto> billInRecordDtos,List<RequestId> requestIds, List<Map<String,String>> lists, RequestIdAuto requestIdAuto) throws Exception;


}

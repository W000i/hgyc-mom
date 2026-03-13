package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.*;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2019/10/11.
 */
public interface CarrierService extends Service<Carrier, Integer> {
    /**
     * 查找输送线上第一个载具
     * @return
     */
    Carrier findFirstCarrier();

    /**
     * 进入点数机验证是否为当前载具
     */
    Carrier inValidate(String carrierCode);

    List<CarrierDto> findList(CarrierCriteria carrierCriteria);

    void callMesGetCarrier(Carrier carrier, CallAgv callAgv, WarnInformation warnInformation);
}

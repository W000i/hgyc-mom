package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.Carrier;
import com.deer.wms.base.system.model.CarrierCriteria;
import com.deer.wms.base.system.model.CarrierDto;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface
CarrierMapper extends Mapper<Carrier> {
    Carrier findFirstCarrier();

    Carrier inValidate(String carrierCode);

    List<CarrierDto> findList(CarrierCriteria carrierCriteria);
}
package com.deer.wms.base.system.dao;

import com.deer.wms.base.system.model.WarnInformation;
import com.deer.wms.base.system.model.WarnInformationCriteria;
import com.deer.wms.base.system.model.WarnInformationDto;
import com.deer.wms.base.system.model.threeDimensional.Warn;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface WarnInformationMapper extends Mapper<WarnInformation> {

    List<WarnInformationDto> findList(WarnInformationCriteria criteria);

    List<Warn> findUntreated(WarnInformationCriteria criteria);
}
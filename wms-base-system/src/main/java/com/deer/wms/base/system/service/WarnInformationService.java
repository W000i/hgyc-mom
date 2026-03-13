package com.deer.wms.base.system.service;

import com.deer.wms.base.system.model.WarnInformation;
import com.deer.wms.base.system.model.WarnInformationCriteria;
import com.deer.wms.base.system.model.WarnInformationDto;
import com.deer.wms.base.system.model.threeDimensional.Warn;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by  on 2020/01/06.
 */
public interface WarnInformationService extends Service<WarnInformation, Integer> {
    List<WarnInformationDto> findList(WarnInformationCriteria criteria);

    List<Warn> findUntreated(WarnInformationCriteria criteria);
}

package com.deer.wms.base.system.service.impl;

import com.deer.wms.base.system.dao.WarnInformationMapper;
import com.deer.wms.base.system.model.WarnInformation;
import com.deer.wms.base.system.model.WarnInformationCriteria;
import com.deer.wms.base.system.model.WarnInformationDto;
import com.deer.wms.base.system.model.threeDimensional.Warn;
import com.deer.wms.base.system.service.WarnInformationService;


import com.deer.wms.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by  on 2020/01/06.
 */
@Service
@Transactional
public class WarnInformationServiceImpl extends AbstractService<WarnInformation, Integer> implements WarnInformationService {

    @Autowired
    private WarnInformationMapper warnInformationMapper;

    @Override
    public List<WarnInformationDto> findList(WarnInformationCriteria criteria){
        return warnInformationMapper.findList(criteria);
    }

    @Override
    public List<Warn> findUntreated(WarnInformationCriteria criteria){
        return warnInformationMapper.findUntreated(criteria);
    }
}

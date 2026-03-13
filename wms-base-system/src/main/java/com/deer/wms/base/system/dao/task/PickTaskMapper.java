package com.deer.wms.base.system.dao.task;

import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.PickTaskDto;
import com.deer.wms.base.system.model.threeDimensional.OutTotal;
import com.deer.wms.common.core.commonMapper.Mapper;

import java.util.List;

public interface PickTaskMapper extends Mapper<PickTask> {

    List<PickTaskDto> findListTwo(PickTaskCriteria criteria);

    List<OutTotal> selectList(PickTaskCriteria criteria);

    List<PickTaskDto> findList(PickTaskCriteria criteria);

    List<PickTaskDto> findByState(PickTaskCriteria criteria);

    Integer totalSevenQuantity();
}
package com.deer.wms.base.system.service.task;

import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.PickTaskDto;
import com.deer.wms.base.system.model.threeDimensional.OutTotal;
import com.deer.wms.common.core.service.Service;

import java.util.List;


/**
 * Created by guo on 2019/07/23.
 */
public interface PickTaskService extends Service<PickTask, Integer> {

    List<PickTaskDto> findListTwo(PickTaskCriteria criteria);

    List<OutTotal> selectList(PickTaskCriteria criteria);

    /**
     * 根据条件查询要出库的东西
     */
    List<PickTaskDto> findList(PickTaskCriteria criteria);

    List<PickTaskDto> findByState(PickTaskCriteria criteria);

    //统计前七天的出库数量
    Integer totalSevenQuantity();
}

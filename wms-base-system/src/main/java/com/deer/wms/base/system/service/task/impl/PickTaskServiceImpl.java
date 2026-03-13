package com.deer.wms.base.system.service.task.impl;

import com.deer.wms.base.system.dao.task.PickTaskMapper;
import com.deer.wms.base.system.model.task.PickTask;
import com.deer.wms.base.system.model.task.PickTaskCriteria;
import com.deer.wms.base.system.model.task.PickTaskDto;
import com.deer.wms.base.system.model.threeDimensional.OutTotal;
import com.deer.wms.base.system.service.box.impl.BoxItemServiceImpl;
import com.deer.wms.base.system.service.task.PickTaskService;


import com.deer.wms.common.core.service.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by guo on 2019/07/23.
 */
@Service
@Transactional
public class PickTaskServiceImpl extends AbstractService<PickTask, Integer> implements PickTaskService {

    @Autowired
    private PickTaskMapper pickTaskMapper;

    @Autowired
    private BoxItemServiceImpl boxItemService;

    @Override
    public List<PickTaskDto> findListTwo(PickTaskCriteria criteria) {
        return pickTaskMapper.findListTwo(criteria);
    }


    @Override
    public List<OutTotal> selectList(PickTaskCriteria criteria) {
        return pickTaskMapper.selectList(criteria);
    }

    @Override
    public List<PickTaskDto> findList(PickTaskCriteria criteria) {
        return pickTaskMapper.findList(criteria);
    }

    @Override
    public List<PickTaskDto> findByState(PickTaskCriteria criteria){
        return pickTaskMapper.findByState(criteria);
    }

    @Override
    public  Integer totalSevenQuantity(){
        Integer a = boxItemService.totalQuantity();
        Integer b = pickTaskMapper.totalSevenQuantity();
        Integer d = b == null ? 0 : b;
        if(a>0 && d>0){
            Integer c =  (d/7)>=1 ? (d/7) : 1;
            return a/c>=1 ? a/c : 1;
        }else{
            return 0;
        }
    }
}

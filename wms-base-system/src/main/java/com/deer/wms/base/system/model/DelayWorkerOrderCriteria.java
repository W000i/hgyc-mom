package com.deer.wms.base.system.model;

import com.deer.wms.common.core.service.QueryCriteria;

/**
* Created by  on 2020/08/25.
*/
public class DelayWorkerOrderCriteria extends QueryCriteria {
    private String workerOrderId;

    public String getWorkerOrderId() {
        return workerOrderId;
    }

    public void setWorkerOrderId(String workerOrderId) {
        this.workerOrderId = workerOrderId;
    }

    public DelayWorkerOrderCriteria() {
    }

    public DelayWorkerOrderCriteria(String workerOrderId) {
        this.workerOrderId = workerOrderId;
    }
}

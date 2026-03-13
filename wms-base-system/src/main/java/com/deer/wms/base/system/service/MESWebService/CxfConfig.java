package com.deer.wms.base.system.service.MESWebService;

import com.deer.wms.base.system.model.ServerVisitAddress;
import com.deer.wms.base.system.model.TaskTypeConstant;
import com.deer.wms.base.system.model.WorkerOrderIssueTime;
import com.deer.wms.base.system.service.ServerVisitAddressService;
import com.deer.wms.base.system.service.SubInventoryService;
import com.deer.wms.base.system.service.WorkerOrderIssueTimeService;
import com.deer.wms.base.system.service.ware.IWareInfoService;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.Endpoint;

@Configuration
public class CxfConfig {
    @Autowired
    private MesWebService mesWebService;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private WorkerOrderIssueTimeService workerOrderIssueTimeService;
    @Autowired
    private IWareInfoService wareInfoService;
    @Autowired
    private SubInventoryService subInventoryService;
    @Bean
    public Endpoint getEndpoint() {
        WorkerOrderIssueTime workerOrderIssueTime= workerOrderIssueTimeService.findById(1);
        TaskTypeConstant.AUTO_EXECUTE = workerOrderIssueTime.getAutoExecute();
        TaskTypeConstant.workerOrderIssueTime = workerOrderIssueTime;
        TaskTypeConstant.wareInfo = wareInfoService.findById(212);
        TaskTypeConstant.organizationId = subInventoryService.findById(1).getOrganizationId();

        ServerVisitAddress serverVisitAddress = serverVisitAddressService.findAddressById(2);
        String ipAddress = serverVisitAddress.getVisitAddress();
        Endpoint publish = EndpointImpl.publish(ipAddress,mesWebService);
        return publish;
    }
}

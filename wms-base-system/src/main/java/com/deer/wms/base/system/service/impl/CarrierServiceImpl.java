package com.deer.wms.base.system.service.impl;

import com.deer.wms.base.system.dao.CarrierMapper;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.service.CallAgvService;
import com.deer.wms.base.system.service.CarrierService;


import com.deer.wms.base.system.service.MESWebService.WebserviceResponse;
import com.deer.wms.base.system.service.ServerVisitAddressService;
import com.deer.wms.base.system.service.WarnInformationService;
import com.deer.wms.base.system.service.webSocket.WebSocketServer;
import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by  on 2019/10/11.
 */
@Service
public class CarrierServiceImpl extends AbstractService<Carrier, Integer> implements CarrierService {

    @Autowired
    private CarrierMapper carrierMapper;
    @Autowired
    private ServerVisitAddressService serverVisitAddressService;
    @Autowired
    private WarnInformationService warnInformationService;
    @Autowired
    private CallAgvService callAgvService;

    @Override
    public Carrier findFirstCarrier(){
        return carrierMapper.findFirstCarrier();
    }

    @Override
    public Carrier inValidate(@Param("carrierCode") String carrierCode){
        return carrierMapper.inValidate(carrierCode);
    }

    @Override
    public List<CarrierDto> findList(CarrierCriteria carrierCriteria){
        return carrierMapper.findList(carrierCriteria);
    }

    @Override
    public void callMesGetCarrier(Carrier carrier, CallAgv callAgv, WarnInformation warnInformation){
        try {
            MyThread myThread = new MyThread();
            myThread.setCallAgv(callAgv);
            myThread.setCarrier(carrier);
            myThread.setWarnInformation(warnInformation);
            myThread.start();
        }catch(Exception e){
            e.printStackTrace();
            throw new ServiceException(CommonCode.SERVER_INERNAL_ERROR);
        }
    }
    class MyThread extends Thread{

        private Carrier carrier;
        private CallAgv callAgv;
        private WarnInformation warnInformation;

        public void setCarrier(Carrier carrier) {
            this.carrier = carrier;
        }
        public void setCallAgv(CallAgv callAgv) {
            this.callAgv = callAgv;
        }
        public void setWarnInformation(WarnInformation warnInformation) {
            this.warnInformation = warnInformation;
        }

        @Override
        public void run() {
            synchronized(this) {
                try {
                    WebserviceResponse webserviceResponse = null;
                    while (!isInterrupted()) {
                        Thread.sleep(10*60*1000); // 休眠10分钟
                        webserviceResponse = serverVisitAddressService.requestMesServer("StockWipOutReq", carrier.getCode());
                        callAgv.setId(null);
                        callAgv.setCreateTime(DateUtils.getTime());
                        callAgv.setErrorCode(webserviceResponse.getErrorCode());
                        callAgv.setTaskCode(webserviceResponse.getTaskCode() == null ? null : webserviceResponse.getTaskCode());
                        callAgv.setErrorMsg("WMS工单完成,呼叫AGV接口。" + webserviceResponse.getErrorMsg());
                        callAgvService.save(callAgv);
                        if (webserviceResponse.getErrorMsg().equals("OK") && webserviceResponse.getErrorCode().equals("0")) {
                            carrier.setTime(DateUtils.getTime());
                            carrier.setCarrierState(2);
                            update(carrier);
                            TaskTypeConstant.call_agv_state = 1;
                            interrupt();
                            System.out.println(Thread.currentThread().getName() + this.getState());
                        } else {
                            warnInformation.setWarnId(null);
                            warnInformation.setMemo("呼叫AGV取载具失败：" + webserviceResponse.getErrorMsg());
                            warnInformation.setCreateTime(DateUtils.getTime());
                            warnInformationService.save(warnInformation);
                            if(webserviceResponse.getErrorMsg().equals("【AGV问题】：呼叫AGV异常-位置个数少于0, 不能入库")){

                            }else {
                                TaskTypeConstant.call_agv_state = 1;
                                interrupt();
                                System.out.println(Thread.currentThread().getName() + this.getState());
                                WebSocketServer.sendInfo(webserviceResponse.getErrorMsg() + "出料口呼叫AGV失败，任务编号为" + callAgv.getTaskCode(), TaskTypeConstant.ALARM_ASSIGN_ACCOUNT.toString());
                            }
                        }
                    }
                }
                catch (InterruptedException ie) {
                    System.out.println(Thread.currentThread().getName() +" ("+this.getState()+") catch InterruptedException.");
                }
                catch(Exception e){

                }
                finally {

                }
            }
        }
    }

}

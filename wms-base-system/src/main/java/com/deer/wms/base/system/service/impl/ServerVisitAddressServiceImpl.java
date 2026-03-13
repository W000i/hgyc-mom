package com.deer.wms.base.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.deer.wms.base.system.dao.ServerVisitAddressMapper;
import com.deer.wms.base.system.model.*;
import com.deer.wms.base.system.model.BaseQueryParams;
import com.deer.wms.base.system.model.Body;
import com.deer.wms.base.system.model.EbsBack;
import com.deer.wms.base.system.model.SystemParams;
import com.deer.wms.base.system.model.ebsModel.SelectInventoryBaseQueryParams;
import com.deer.wms.base.system.model.item.BaseQueryParams2;
import com.deer.wms.base.system.model.item.Body2;
import com.deer.wms.base.system.service.MESWebService.WebserviceResponse;
import com.deer.wms.base.system.service.ServerVisitAddressService;


import com.deer.wms.common.core.result.CommonCode;
import com.deer.wms.common.core.service.AbstractService;
import com.deer.wms.common.exception.ServiceException;
import com.deer.wms.common.utils.DateUtils;
import com.deer.wms.framework.util.MyUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by  on 2019/09/26.
 */
@Service
public class ServerVisitAddressServiceImpl extends AbstractService<ServerVisitAddress, Integer> implements ServerVisitAddressService {

    @Autowired
    private ServerVisitAddressMapper serverVisitAddressMapper;

    @Override
    public List<ServerVisitAddress> findList(ServerVisitAddressCriteria criteria) {
        return serverVisitAddressMapper.findList(criteria);
    }

    @Override
    public ServerVisitAddress findAddressById(@Param("id") Integer id) {
        return serverVisitAddressMapper.findAddressById(id);
    }

    private static ServerVisitAddress serverVisitAddressThree = null;

    //创建连接并请求EBS获取数据
    private EbsBack getEbsBack(String listJson) {
        CloseableHttpClient client = HttpClients.createDefault();
        String entityStr = null;
        CloseableHttpResponse response = null;
        try {
            String accessToken = getEbsAccessToken(); //获取服务端返回过来的access_token
            if (serverVisitAddressThree == null) {
                serverVisitAddressThree = serverVisitAddressMapper.findAddressById(3);
            }
            HttpPost httpPost = new HttpPost(serverVisitAddressThree.getVisitAddress() + "?access_token=" + accessToken);
//                httpPost.setHeader("Authorization","bearer "+accessToken);
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity stringEntity = new StringEntity(listJson, "UTF-8");
            httpPost.setEntity(stringEntity);
            response = client.execute(httpPost);
            // 获得响应的实体对象
            HttpEntity entity = response.getEntity();
            // 使用Apache提供的工具类进行转换成字符串
            entityStr = EntityUtils.toString(entity, "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(entityStr);
            String success = jsonObject.get("success").toString().trim();
            String msg = jsonObject.get("msg").toString().trim();
            Integer total = null;
            String rows = "";
            if (success.equals("true")) {
                String obj = jsonObject.get("obj").toString().trim();
                JSONObject jsonObject1 = JSONObject.parseObject(obj);
                total = jsonObject1.get("total").toString().trim() == null ? 0 : Integer.parseInt(jsonObject1.get("total").toString().trim());
                rows = jsonObject1.get("rows").toString().trim();
            }
            return new EbsBack(success, msg, total, rows);
        } catch (ClientProtocolException e) {
            System.err.println("Http协议出现问题");
            e.printStackTrace();
//            throw new ServiceException(CommonCode.VISIT_EBS_FAIL);
            return null;
        } catch (ParseException e) {
            System.err.println("解析错误");
            e.printStackTrace();
//            throw new ServiceException(CommonCode.VISIT_EBS_FAIL);
            return null;
        } catch (IOException e) {
            System.err.println("IO异常");
            e.printStackTrace();
//            throw new ServiceException(CommonCode.VISIT_EBS_FAIL);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
//            throw new ServiceException(CommonCode.VISIT_EBS_FAIL);
            return null;
        } finally {
            // 释放连接
            if (null != response) {
                try {
                    response.close();
                    client.close();
                } catch (IOException e) {
                    System.err.println("释放连接出错");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 请求物料
     *
     * @param itemCode
     * @param organizationId
     * @return
     */
    @Override
    public EbsBack requestItemId(String itemCode, Integer organizationId) {
        SystemParams systemParams = new SystemParams("1", "PCB_APS", "APS", "EBS",
                "getInvItemInfosForPcbAps", MyUtils.randomAssignFigures(6), System.currentTimeMillis(),
                TaskTypeConstant.QUERY, "1.0", "fd");
        BaseQueryParams2 baseQueryParams = new BaseQueryParams2(organizationId, "2000-01-01", DateUtils.getDate(), 1, 50
        );
        List<Map<String, String>> lists = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("itemCode", itemCode);
        lists.add(map);
//           systemParams.setToken(accessToken);
        Body2 body2 = new Body2(systemParams, baseQueryParams, lists);
        String listJson = JSON.toJSONString(body2);
        EbsBack Ebsback = getEbsBack(listJson);
        return Ebsback;
    }

    //请求EBS其他接口
    @Override
    public EbsBack requestServerCode(String requestId, String serviceName, String serviceOperation, Integer organizationId, List<Map<String, String>> lists) {
        SystemParams systemParams = new SystemParams(requestId, "NT_WMS", "NT_WMS", "EBS",
                serviceName, MyUtils.randomAssignFigures(6), System.currentTimeMillis(),
                serviceOperation, "1.0", "");
        String listJson = "";
        if (organizationId != null) {
            BaseQueryParams baseQueryParams = new BaseQueryParams(organizationId);
            Body body = new Body(systemParams, baseQueryParams, lists);
            listJson = JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
        } else {
            BaseQueryParams1 baseQueryParams = new BaseQueryParams1();
            Body1 body = new Body1(systemParams, baseQueryParams, lists);
            listJson = JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
        }
        EbsBack ebsBack = getEbsBack(listJson);
        return ebsBack;
    }

    //请求EBS查询库存
    @Override
    public EbsBack requestServerCodeSelectInventory(List<Map<String, String>> lists) {
        SystemParams systemParams = new SystemParams("6", "NT_WMS", "NT_WMS", "EBS",
                TaskTypeConstant.GET_EXISTING_STOCK, MyUtils.randomAssignFigures(6), System.currentTimeMillis(),
                TaskTypeConstant.QUERY, "1.0", "");
        String listJson = "";
        SelectInventoryBaseQueryParams baseQueryParams = new SelectInventoryBaseQueryParams(
                TaskTypeConstant.organizationId, 1, 2000
        );
        Body body = new Body(systemParams, baseQueryParams, lists);
        listJson = JSON.toJSONString(body, SerializerFeature.WriteMapNullValue);
        EbsBack ebsBack = getEbsBack(listJson);
        return ebsBack;
    }

    private static OAuthClient oAuthClient = null;
    private static OAuthClientRequest accessTokenRequest = null;

    //获取EBS访问权限token
    private String getEbsAccessToken() {
        try {
            if (oAuthClient == null) {
                ServerVisitAddress serverVisitAddressFour = serverVisitAddressMapper.findAddressById(4);
                OAuthClient oAuthClient1 = new OAuthClient(new URLConnectionClient());
                OAuthClientRequest accessTokenRequest1 = OAuthClientRequest
                        .tokenLocation(serverVisitAddressFour.getVisitAddress())
                        .setGrantType(GrantType.CLIENT_CREDENTIALS)
                        .setUsername(serverVisitAddressFour.getAccount())
                        .setPassword(serverVisitAddressFour.getPassword())
                        .buildQueryMessage();
                accessTokenRequest1.addHeader("Accept", "application/json");
                accessTokenRequest1.addHeader("Content-Type", "application/json");
                String auth = MyUtils.encode(serverVisitAddressFour.getAccount() + ':' + serverVisitAddressFour.getPassword());
                accessTokenRequest1.addHeader("Authorization", "Basic " + auth);
                oAuthClient = oAuthClient1;
                accessTokenRequest = accessTokenRequest1;
            }
            OAuthAccessTokenResponse oAuthResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.POST); //去服务端请求access_token，并返回响应
            String accessToken = oAuthResponse.getAccessToken();
            return accessToken;
        } catch (OAuthSystemException oau) {
            System.err.println("token访问异常");
            oau.printStackTrace();
            throw new ServiceException(CommonCode.VISIT_EBS_FAIL);
        } catch (OAuthProblemException oau) {
            System.err.println("token访问异常");
            oau.printStackTrace();
            throw new ServiceException(CommonCode.VISIT_EBS_FAIL);
        }

    }

    /**
     * 当地址改变时修改常量
     *
     * @param serverVisitAddress
     */
    @Override
    public void update(ServerVisitAddress serverVisitAddress) {
        super.update(serverVisitAddress);
        if (serverVisitAddress.getVisitAddressId().equals(1)) {
            JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
            client = dcf.createClient(serverVisitAddress.getVisitAddress());
        } else if (serverVisitAddress.getVisitAddressId().equals(4)) {
            oAuthClient = null;
            accessTokenRequest = null;
        } else if (serverVisitAddress.getVisitAddressId().equals(3)) {
            serverVisitAddressThree = serverVisitAddress;
        }
    }

    private static Map<String, Client> clientMap = new HashMap<String, Client>();
    private static Client client = null;

    /**
     * 给mes发送请求
     *
     * @param methodName
     * @param code
     * @return
     */
    @Override
    public WebserviceResponse requestMesServer(String methodName, String code) {
        WebserviceResponse webserviceResponse = null;
        try {
            Object[] objects;
            if (client == null) {
                String MESIP = serverVisitAddressMapper.findAddressById(1).getVisitAddress();
                JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//                QName qname = new QName("http://scc.com.cn","macIntf");
                client = dcf.createClient(MESIP);
//                Client client = dcf.createClient(MESIP);
//                clientMap.put(methodName,client);
            }
//            Client client = clientMap.get(methodName);
            Client client1 = client;
            objects = client1.invoke("macIntf", methodName, code);
            webserviceResponse = analysisObject(objects[0]);
//            System.out.println("返回数据:" + objects[0].toString());
        } catch (Exception e) {
            e.printStackTrace();
            webserviceResponse = new WebserviceResponse(null, "-1", "调用MES接口出错", null);
        }
        return webserviceResponse;
    }


    public WebserviceResponse analysisObject(Object object) throws Exception {
        Field taskCode = object.getClass().getDeclaredField("x003CTaskCodeX003EKBackingField");
        taskCode.setAccessible(true);
        Field errorCode = object.getClass().getDeclaredField("x003CErrorCodeX003EKBackingField");
        errorCode.setAccessible(true);
        Field errorMsg = object.getClass().getDeclaredField("x003CErrorMsgX003EKBackingField");
        errorMsg.setAccessible(true);
        Field resultData = object.getClass().getDeclaredField("x003CResultDataX003EKBackingField");
        resultData.setAccessible(true);
        String taskCode1 = taskCode.get(object).toString();
        String errorCode1 = errorCode.get(object).toString();
        String errorMsg1 = errorMsg.get(object).toString();
        String resultData1 = resultData.get(object) == null ? "" : resultData.get(object).toString();
        WebserviceResponse webserviceResponse = new WebserviceResponse(taskCode1, errorCode1, errorMsg1, resultData1);
        return webserviceResponse;
    }


}

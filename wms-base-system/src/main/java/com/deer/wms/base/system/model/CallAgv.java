package com.deer.wms.base.system.model;

import javax.persistence.*;

@Table(name = "call_agv")
public class CallAgv {
    /**
     * 呼叫空载具任务
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 编码
     */
    @Column(name="code")
    private String code;

    @Column(name="method_name")
    private String methodName;

    @Column(name="task_code")
    private String taskCode;

    @Column(name="error_code")
    private String errorCode;

    @Column(name="error_msg")
    private String errorMsg;

    @Column(name="wip_entity")
    private String wipEntity;

    @Column(name="item_code")
    private String itemCode;

    @Column(name="quantity")
    private Integer quantity;

    @Column(name="create_time")
    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getWipEntity() {
        return wipEntity;
    }

    public void setWipEntity(String wipEntity) {
        this.wipEntity = wipEntity;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 获取呼叫空载具任务
     *
     * @return id - 呼叫空载具任务
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置呼叫空载具任务
     *
     * @param id 呼叫空载具任务
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取编码
     *
     * @return code - 编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置编码
     *
     * @param code 编码
     */
    public void setCode(String code) {
        this.code = code;
    }


}
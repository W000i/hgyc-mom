package com.deer.wms.base.system.model;


import javax.persistence.*;

@Table(name = "warn_information")
public class WarnInformation {
    /**
     * id
     */
    @Id
    @Column(name = "warn_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer warnId;

    /**
     * 报警信息
     */
    private String memo;

    /**
     * 1-待处理2-已处理
     */
    private Integer state;

    /**
     * 类型
     */
    @Column(name="type")
    private Integer type;

    /**
     * 创建时间
     */
    @Column(name="create_time")
    private String createTime;

    /**
     * 处理时间
     */
    @Column(name="finish_time")
    private String finishTime;

    /**
     * 编码
     */
    @Column(name="alarm_code")
    private String alarmCode;

    /**
     * 报警状态
     */
    @Column(name="alarm_state")
    private Integer alarmState;

    @Column(name="handle_card")
    private String handleCard;

    @Column(name="box_code")
    private String boxCode;

    @Column(name = "task_id")
    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getHandleCard() {
        return handleCard;
    }

    public void setHandleCard(String handleCard) {
        this.handleCard = handleCard;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public Integer getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(Integer alarmState) {
        this.alarmState = alarmState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取id
     *
     * @return warn_id - id
     */
    public Integer getWarnId() {
        return warnId;
    }

    /**
     * 设置id
     *
     * @param warnId id
     */
    public void setWarnId(Integer warnId) {
        this.warnId = warnId;
    }

    /**
     * 获取报警信息
     *
     * @return memo - 报警信息
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置报警信息
     *
     * @param memo 报警信息
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 获取1-待处理2-已处理
     *
     * @return state - 1-待处理2-已处理
     */
    public Integer getState() {
        return state;
    }

    /**
     * 设置1-待处理2-已处理
     *
     * @param state 1-待处理2-已处理
     */
    public void setState(Integer state) {
        this.state = state;
    }

    public WarnInformation() {
    }

    public WarnInformation(String memo, Integer state, Integer type, String createTime) {
        this.memo = memo;
        this.state = state;
        this.type = type;
        this.createTime = createTime;
    }

    public WarnInformation(String createTime, String alarmCode, Integer alarmState,Integer state) {
        this.createTime = createTime;
        this.alarmCode = alarmCode;
        this.alarmState = alarmState;
        this.state = state;
    }
}
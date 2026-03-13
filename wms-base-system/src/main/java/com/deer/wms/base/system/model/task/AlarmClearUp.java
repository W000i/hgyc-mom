package com.deer.wms.base.system.model.task;

public class AlarmClearUp {
    private String code;
    private Integer state;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public AlarmClearUp() {
    }

    public AlarmClearUp(String code, Integer state) {
        this.code = code;
        this.state = state;
    }
}

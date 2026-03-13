package com.deer.wms.base.system.model;

import javax.persistence.*;

public class WarnInformationDto extends WarnInformation{
    private String location;
    private String content;
    private String processMethod;
    private String within;
    private String name;

    public String getWithin() {
        return within;
    }

    public void setWithin(String within) {
        this.within = within;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getProcessMethod() {
        return processMethod;
    }

    public void setProcessMethod(String processMethod) {
        this.processMethod = processMethod;
    }
}
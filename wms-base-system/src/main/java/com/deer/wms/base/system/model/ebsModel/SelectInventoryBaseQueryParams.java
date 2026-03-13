package com.deer.wms.base.system.model.ebsModel;

public class SelectInventoryBaseQueryParams {
    private Integer organizationId;
    private Integer startPosition;
    private Integer rowsCnt;

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public Integer getRowsCnt() {
        return rowsCnt;
    }

    public void setRowsCnt(Integer rowsCnt) {
        this.rowsCnt = rowsCnt;
    }

    public SelectInventoryBaseQueryParams() {
    }

    public SelectInventoryBaseQueryParams(Integer organizationId, Integer startPosition, Integer rowsCnt) {
        this.organizationId = organizationId;
        this.startPosition = startPosition;
        this.rowsCnt = rowsCnt;
    }
}

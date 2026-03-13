package com.deer.wms.base.system.model.box;

import java.util.List;

public class CalculateReturnedValue {

    private List<BoxItemDto> boxItemDtos;
    private Integer quantity;

    public List<BoxItemDto> getBoxItemDtos() {
        return boxItemDtos;
    }

    public void setBoxItemDtos(List<BoxItemDto> boxItemDtos) {
        this.boxItemDtos = boxItemDtos;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

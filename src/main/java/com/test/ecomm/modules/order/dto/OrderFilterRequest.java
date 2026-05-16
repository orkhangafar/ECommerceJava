package com.test.ecomm.modules.order.dto;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.PageFilterRequest;
import com.test.ecomm.modules.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFilterRequest extends PageFilterRequest {
    private OrderStatus status;

    public OrderFilterRequest() {
        setSortBy(AppConstants.SORT_ORDERS_BY);
    }
}

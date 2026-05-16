package com.test.ecomm.modules.product.dto;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.PageFilterRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductFilterRequest extends PageFilterRequest {

    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long categoryId;

    public ProductFilterRequest() {
        setSortBy(AppConstants.SORT_PRODUCTS_BY);
    }
}

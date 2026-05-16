package com.test.ecomm.modules.category.dto;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.PageFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryFilterRequest extends PageFilterRequest {

    public CategoryFilterRequest() {
        setSortBy(AppConstants.SORT_CATEGORIES_BY);
    }
}

package com.test.ecomm.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageFilterRequest {

    @Min(value = 0, message = "{page.number.min}")
    private int pageNumber = 0;

    @Min(value = 1, message = "{page.size.min}")
    @Max(value = 100, message = "{page.size.max}")
    private int pageSize = 18;
    private String sortBy;
    private String sortOrder = "asc";
}

package com.test.ecomm.modules.user.dto;

import com.test.ecomm.common.constants.AppConstants;
import com.test.ecomm.common.dto.PageFilterRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterRequest extends PageFilterRequest {
    private String name;
    private String email;
    private String role;

    public UserFilterRequest() {
        setSortBy(AppConstants.SORT_USERS_BY);
    }
}

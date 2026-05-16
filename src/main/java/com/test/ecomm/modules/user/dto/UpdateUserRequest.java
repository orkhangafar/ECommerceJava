package com.test.ecomm.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @NotBlank(message = "{auth.firstname.not_blank}")
    @Size(max = 50, message = "{user.firstname.size}")
    private String firstName;

    @NotBlank(message = "{auth.lastname.not_blank}")
    @Size(max = 50, message = "{user.lastname.size}")
    private String lastName;
}

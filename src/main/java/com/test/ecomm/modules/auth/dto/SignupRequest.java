package com.test.ecomm.modules.auth.dto;

import com.test.ecomm.common.validation.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class SignupRequest {

    @NotBlank(message = "{auth.firstname.not_blank}")
    private String firstName;

    @NotBlank(message = "{auth.lastname.not_blank}")
    private String lastName;

    @NotBlank(message = "{auth.email.not_blank}")
    @Email(message = "{auth.email.invalid}")
    private String email;

    @NotBlank(message = "{auth.password.not_blank}")
    @Size(min = 8, message = "{auth.password.size}")
    private String password;

    @NotBlank(message = "{auth.password.retype.not_blank}")
    private String confirmPassword;
}

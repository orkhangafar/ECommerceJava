package com.test.ecomm.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeactivateRequest {

    @NotBlank(message = "{user.password.required}")
    private String password;
}

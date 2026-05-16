package com.test.ecomm.common.validation;

import com.test.ecomm.modules.auth.dto.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        SignupRequest user = (SignupRequest) obj;
        return user.getPassword() != null && user.getPassword().equals(user.getConfirmPassword());
    }
}

package com.trustai.userservice.user.validation.impl;

import com.trustai.userservice.user.exception.DuplicateRecordException;
import com.trustai.userservice.user.exception.IdNotFoundException;
import com.trustai.userservice.user.validation.RegistrationRequestValidatorTemplate;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class RegistrationRequestRegistrationRequestValidator extends RegistrationRequestValidatorTemplate {

    @Override
    protected void validateDuplicateUsername(String username) throws DuplicateRecordException {
        boolean isExist = userRepository.existsByUsername(username);
        if (isExist) throw new DuplicateRecordException("username already exist");
    }

    @Override
    protected void validateValidEmailFormat(String email) throws DuplicateRecordException {

    }

    @Override
    protected void validateDuplicateEmail(String email) throws DuplicateRecordException {

    }

    @Override
    protected void validateMobileFormat(String mobile) throws DuplicateRecordException {

    }

    @Override
    protected void validateDuplicateMobile(String mobile) throws DuplicateRecordException {

    }

    @Override
    protected boolean isMobileNumberVerified(String mobile) throws ValidationException {
        return false;
    }

    @Override
    protected void validateReferralCode(String referralCode) throws DuplicateRecordException {
        userRepository.findByReferralCode(referralCode).orElseThrow(() -> new IdNotFoundException("invalid referralCode"));
    }
}

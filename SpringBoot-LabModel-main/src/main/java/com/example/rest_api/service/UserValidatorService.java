package com.example.rest_api.service;


import com.example.rest_api.database.users.model.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class UserValidatorService implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UserEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object userEntity, Errors errors) {
        UserEntity user = (UserEntity) userEntity;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "user.username.empty", "Username cannot be empty");
        if (user.getUsername().length() < 3 || user.getUsername().length() > 32) {
            errors.rejectValue("username", "user.username.length", "Username must be between 3 and 32 characters");
        }

        // Validarea pentru email
        String emailRegexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!user.getEmail().matches(emailRegexPattern)) {
            errors.rejectValue("email", "user.email.invalid", "Invalid email format");
        }

        // Validarea pentru parola
        String passwordRegexPattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        if (!user.getPassword().matches(passwordRegexPattern)) {
            errors.rejectValue("password", "user.password.weak", "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character");
        }

        // Validarea pentru confirmarea parolei
        if (!user.getPassword().equals(user.getRepeatPassword())) {
            errors.rejectValue("repeatPassword", "user.password.mismatch", "Passwords do not match");
        }
    }

    public void validateLogin(Object target, Errors errors) {
        UserEntity user = (UserEntity) target;

        // Validarea pentru email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "user.email.empty", "Email cannot be empty");
        String emailRegexPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!user.getEmail().matches(emailRegexPattern)) {
            errors.rejectValue("email", "user.email.invalid", "Invalid email format");
        }

        // Validarea pentru parolă
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.password.empty", "Password cannot be empty");
    }

}

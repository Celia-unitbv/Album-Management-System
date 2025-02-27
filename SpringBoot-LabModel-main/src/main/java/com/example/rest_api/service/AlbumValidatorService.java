package com.example.rest_api.service;

import com.example.rest_api.database.resources.model.Album;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Service
public class AlbumValidatorService implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Album.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Album album = (Album) target;

        // Validarea pentru numele albumului
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "album.name.empty", "Numele albumului nu poate fi gol");
        if (album.getName() != null && album.getName().length() < 3) {
            errors.rejectValue("name", "album.name.short", "Numele albumului trebuie să conțină cel puțin 3 caractere");
        }
    }
}

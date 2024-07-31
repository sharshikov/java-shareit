package ru.practicum.shareit.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AvailableValidator implements ConstraintValidator<ValidAvailable, Boolean> {

    @Override
    public void initialize(ValidAvailable constraintAnnotation) {
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        // Возвращаем false, если значение null
        return value != null;
    }
}

package uk.tw.energy.domain;

import io.vavr.control.Validation;

public interface Validations {
    default <T> Validation<String, T> validateNotNull(T value, String error) {
        if (value == null) return Validation.invalid(error);
        return Validation.valid(value);
    }

    default <T extends Number> Validation<String, T> validateNotZero(T value, String error) {
        if (value.doubleValue() == 0) return Validation.invalid(error);
        return Validation.valid(value);
    }
}

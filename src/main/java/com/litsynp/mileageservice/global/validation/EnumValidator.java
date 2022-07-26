package com.litsynp.mileageservice.global.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<Enum, String> {

    private Enum annotation;

    @Override
    public void initialize(Enum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result = false;
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();

        // Null check
        if (value == null) {
            return false;
        }

        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                // enum 값과 실제로 string 값이 일치하는지 확인
                if (value.equals(enumValue.toString())
                        // ignoreCase가 true 라면 대소문자 (case) 무시하고 확인
                        || (this.annotation.ignoreCase() && value.equalsIgnoreCase(
                        enumValue.toString()))) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}

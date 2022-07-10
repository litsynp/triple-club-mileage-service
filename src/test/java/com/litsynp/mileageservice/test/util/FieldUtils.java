package com.litsynp.mileageservice.test.util;

import java.lang.reflect.Field;

public class FieldUtils {

    public static Object writeField(Object instance, String fieldName, Object fieldValue) {
        try {
            Field instanceField = instance.getClass().getDeclaredField(fieldName);
            instanceField.setAccessible(true);
            instanceField.set(instance, fieldValue);
            return instance;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field named " + fieldName);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot set the field with given value");
        }
    }

    public static Object writeSuperField(Object instance, String superFieldName, Object superFieldValue) {
        try {
            Field instanceField = instance.getClass().getSuperclass().getDeclaredField(superFieldName);
            instanceField.setAccessible(true);
            instanceField.set(instance, superFieldValue);
            return instance;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("No such field named " + superFieldValue);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot set the field with given value");
        }
    }
}

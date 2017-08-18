package com.app.utils.tables.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.app.utils.tables.filter.StringFilter;

@Target({ ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AttributeDescriptor {
    Class<?> filter() default StringFilter.class; // indica el tipo de filtrado a utilizar
    String path(); //indica la columna de la tabla correspondiente en la BD
}
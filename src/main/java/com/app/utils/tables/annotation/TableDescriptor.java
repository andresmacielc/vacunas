package com.app.utils.tables.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface TableDescriptor {
    String name(); // nombre de la tabla
    String schema() default ""; // esquema de la tabla
}
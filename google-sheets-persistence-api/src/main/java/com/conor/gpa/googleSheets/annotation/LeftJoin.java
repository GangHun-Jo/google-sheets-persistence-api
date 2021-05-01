package com.conor.gpa.googleSheets.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LeftJoin {
	//TODO : joinColumn이나 targetClass 잘못들어갔을 때 예외처리
	String joinColumn();

	Class<?> targetClass();
}

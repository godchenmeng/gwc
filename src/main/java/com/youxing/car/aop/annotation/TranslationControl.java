package com.youxing.car.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/** 
*自定义注解 拦截Controller 
*/
//定义注解的作用目标**作用范围字段、枚举的常量/方法  
@Target({ElementType.TYPE,ElementType.METHOD})
//该注解会在class字节码文件中存在，在运行时可以通过反
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationControl {
	 /**
     * 权限的名称
     * @return
     */
    String value() default "";

}

package org.junit.tools.configuration.base;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
* Annotation to define a reference to a method and its signature.
*
 * @author Robert Streng
*/
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Documented
public @interface MethodRef {
 
       String name();
 
       String signature();
}

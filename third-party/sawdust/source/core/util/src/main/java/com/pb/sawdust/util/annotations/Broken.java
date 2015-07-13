package com.pb.sawdust.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * The {@code Broken} annotation indicates that a certain method or type is broken and should not be used.
 *
 * @author crf <br/>
 *         Started 1/16/11 4:42 PM
 */
@Inherited
@Target({ElementType.CONSTRUCTOR,ElementType.METHOD,ElementType.TYPE})
public @interface Broken {
    String reason() default "";
}

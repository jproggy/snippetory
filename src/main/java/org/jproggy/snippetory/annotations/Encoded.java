package org.jproggy.snippetory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jproggy.snippetory.Encodings;

/**
 * Mark an encoded container. The encoding can either be defined by it's name
 * given as a String value (I.e. @Encoded("html")) or via a method annotated
 * with {@link Encoding}. If neither is done {@link Encodings.NULL} is assumed.
 *   
 * @author B. Ebertz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Encoded {
	String value() default "";
}

package org.jproggy.snippetory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to provide the encoding. The return type of the annotated
 * method has to be either {@link String} or 
 * {@link org.jproggy.snippetory.spi.Encoding Encoding}. In case of String it has
 * to be the name of a registered encoding. In latter case it has to be a 
 * registered encoding, too. 
 * 
 * @author B. Ebertz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Encoding {

}

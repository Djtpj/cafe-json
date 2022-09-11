package io.github.djtpj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for marking to the io.github.djtpj.JsonStreamer that this method should be automatically added to the json file
 * @see JsonStreamer
 * @see JsonPrimitive
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSave {
    JsonPrimitive value() default JsonPrimitive.OBJECT;
}

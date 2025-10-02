package catserver.api.annotation;

import java.lang.annotation.*;

/**
 * Indicates that the annotated api method is backported from a newer version of Minecraft.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Backported {
    String from() default "";
}

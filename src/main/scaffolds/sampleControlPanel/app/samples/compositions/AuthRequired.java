package samples.compositions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import play.mvc.With;

/**
 * Annotated to controller/action that needs authentication.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
@With(AuthRequiredAction.class)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthRequired {
    String[] usergroups() default {};

    /**
     * Either a url, start with {@code /}; or a revert-routing call, format
     * {@code revert-routes-class:static-field-name:method-name}, example
     * {@code samples.controllers.routes:SampleController:login}.
     *
     * @return
     */
    String loginCall() default "";

    String flashMsg() default "";

    String flashKey() default "";
}

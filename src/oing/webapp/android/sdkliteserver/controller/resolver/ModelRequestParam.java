package oing.webapp.android.sdkliteserver.controller.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Go check out {@link ModelRequestParamArgumentResolver}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelRequestParam {
	/**
	 * Form field name.
	 */
	String value();
}

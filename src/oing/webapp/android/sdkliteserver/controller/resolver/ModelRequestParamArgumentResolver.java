package oing.webapp.android.sdkliteserver.controller.resolver;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Convert from form data to object or objects-in-list.<br/>
 * The HTML form should like this:<br/>
 * <pre>{@code
 * <form>
 *     Single object example:
 *     // The string "user" will be the Annotation "ModelRequestParam" parameter.
 *     <input type="text" name="user.name" value="tom"/>
 *     <input type="password" name="user.password"/>
 *
 *     Bunch of object example:
 *     // The string "books" will be the Annotation "ModelRequestParam" parameter,
 *     // following with an index "[0 1 2 etc...]" start from 0.
 *     // The max index value you put, the max length of List you got, so we got a "List.size() = 3".
 *     <input type="text" name="books[0].title"/>
 *     <input type="text" name="books[0].price"/>
 *     <input type="text" name="books[1].title"/>// A book not for sale.
 *     <input type="text" name="books[2].title"/>
 *     <input type="text" name="books[2].price"/>
 *     etc...
 * </from>
 * }</pre>
 * <p>
 * Define a method includes Annotation "{@linkplain ModelRequestParam ModelRequestParam}":<br/>
 * <pre>{@code
 * public Whatever login(@ModelRequestParam("user") User user) {
 *     // We will get "tom logged in."
 *     System.out.println(user.getName() + " logged in.");
 * }
 *
 * public Whatever insertBooks(@ModelRequestParam("books") List<Book> books) {
 *     // We will get "3 books inserted."
 *     System.out.println(books.size() + " books inserted.");
 * }
 * }</pre>
 */
public class ModelRequestParamArgumentResolver implements HandlerMethodArgumentResolver {
	@Autowired
	private ConversionService conversionService;

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.hasParameterAnnotation(ModelRequestParam.class);
	}

	@Override
	public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
	                              NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
		final String lStrFormFieldName = methodParameter.getParameterAnnotation(ModelRequestParam.class).value();
		Map<String, String[]> lMapRequestParams = webRequest.getParameterMap();
		// Check if ModelRequestParam annotated besides a List<Whatever>.
		final boolean lzIsList = methodParameter.getParameterType().equals(List.class);
		Class lClazzBeanType;// For list "List<Whatever>", we got a bean class type "Whatever".
		// String[] lStrArrPropertyNames;// Bean property names from form field.
		PropertyDescriptor[] lPropertyDescriptorsArrBean;// Property info all of "lClazzBeanType".

		// Find out Bean class type.
		if (lzIsList) {
			ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) methodParameter.getGenericParameterType();
			lClazzBeanType = (Class) parameterizedType.getActualTypeArguments()[0];
		} else {
			lClazzBeanType = (Class) methodParameter.getParameterType();
		}
		lPropertyDescriptorsArrBean = ReflectUtils.getBeanProperties(lClazzBeanType);

		/**
		 * The largest number i in form field "field[i].prop".
		 * This local variable uses when form contains fields like "field[i].prop" only,
		 * It determines the max length of Model List.
		 */
		int lnModelListLength = -1;
		if (lzIsList) {
			final String PARAM_NAME_WITH_LEFTBRACKET = lStrFormFieldName + "[";
			for (Iterator<String> it = webRequest.getParameterNames(); it.hasNext(); ) {
				String lStrName = it.next();
				if (lStrName.startsWith(PARAM_NAME_WITH_LEFTBRACKET)) {
					int lnIndex = Integer.parseInt(lStrName.substring(PARAM_NAME_WITH_LEFTBRACKET.length(), lStrName.indexOf(']')));
					if (lnIndex > lnModelListLength) lnModelListLength = lnIndex;
				}
			}
			lnModelListLength++;
		}
		// Instantiate beans
		if (!lzIsList) {
			String[] lStrArrPropertyValues = new String[lPropertyDescriptorsArrBean.length];
			for (int i = 0; i < lPropertyDescriptorsArrBean.length; i++) {
				String[] lStrArrValues = webRequest.getParameterValues(
						lStrFormFieldName + "." + lPropertyDescriptorsArrBean[i].getName());
				if (lStrArrValues != null && lStrArrValues.length == 1) lStrArrPropertyValues[i] = lStrArrValues[0];
			}
			return instantiateBean(lClazzBeanType, lPropertyDescriptorsArrBean, lStrArrPropertyValues);
		} else {
			ArrayList<Object> lListModels = new ArrayList<>();
			for (int i = 0; i < lnModelListLength; i++) {
				String[] lStrArrPropertyValues = new String[lPropertyDescriptorsArrBean.length];
				for (int j = 0; j < lPropertyDescriptorsArrBean.length; j++) {
					PropertyDescriptor propertyDescriptor = lPropertyDescriptorsArrBean[j];
					String lStrName = lStrFormFieldName + "[" + i + "]." + propertyDescriptor.getName();
					String[] lStrArrValues = lMapRequestParams.get(lStrName);
					if (lStrArrValues != null) lStrArrPropertyValues[j] = lStrArrValues[0];
				}
				lListModels.add(instantiateBean(lClazzBeanType, lPropertyDescriptorsArrBean, lStrArrPropertyValues));
			}
			return lListModels;
		}
	}

	private Object instantiateBean(Class beanClass, PropertyDescriptor[] propertyDescriptors, String[] propertyValues)
			throws InvocationTargetException, IllegalAccessException {
		Object lObjBean = BeanUtils.instantiate(beanClass);
		if (propertyDescriptors == null || propertyDescriptors.length == 0) return lObjBean;
		if (propertyDescriptors.length != propertyValues.length) {
			throw new IllegalArgumentException("PropertyDescriptors.length doesn't match PropertyValues.length. " +
					"Put a null value into PropertyValue array if you don't want some property have a value.");
		}

		for (int i = 0; i < propertyDescriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
			Class lClazzPropertyType = BeanUtils.findPropertyType(propertyDescriptor.getName(), beanClass);
			// We can do it by using PropertyEditor instead of ConversionService.
			Object lObjPropertyValue = conversionService.convert(propertyValues[i], lClazzPropertyType);
			propertyDescriptor.getWriteMethod().invoke(lObjBean, lObjPropertyValue);
		}
		return lObjBean;
	}
}

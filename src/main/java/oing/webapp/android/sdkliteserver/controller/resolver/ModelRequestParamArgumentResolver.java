package oing.webapp.android.sdkliteserver.controller.resolver;

import oing.webapp.android.sdkliteserver.tools.xmleditor.AddonSiteTypeV3;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
 *     <input type="text" name="books.title" value="Java"/>
 *     <input type="text" name="books.price" value="$5"/>
 *     <input type="text" name="books.title" value="Spring"/>
 *     <!-- A book not for sale, just left null for books.price.
 *     <input type="text" name="books.price" value="$5"/>
 *     -->
 *     <input type="text" name="books.title" value="Mybatis"/>
 *     <input type="text" name="books.price" value="$5"/>
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
		Class lClassBeanType;// For list "List<Whatever>", we got a bean class type "Whatever".
		PropertyDescriptor[] lPropertyDescriptorsArrBean;// Property info all of "lClassBeanType".

		// Find out Bean class type.
		if (lzIsList) {
			ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) methodParameter.getGenericParameterType();
			lClassBeanType = (Class) parameterizedType.getActualTypeArguments()[0];
		} else {
			lClassBeanType = (Class) methodParameter.getParameterType();
		}
		lPropertyDescriptorsArrBean = ReflectUtils.getBeanProperties(lClassBeanType);

		/*
		 * Determines the max length of Model List.
		 */
		int lnModelListLength = -1;
		if (lzIsList) {
			for (String lStrName : lMapRequestParams.keySet()) {
				if (lStrName.startsWith(lStrFormFieldName)) {
					int lnLength = lMapRequestParams.get(lStrName).length;
					if (lnLength > lnModelListLength) lnModelListLength = lnLength;
				}
			}
		}
		// Instantiate beans
		if (!lzIsList) {
			String[] lStrArrPropertyValues = new String[lPropertyDescriptorsArrBean.length];
			for (int i = 0; i < lPropertyDescriptorsArrBean.length; i++) {
				String[] lStrArrValues = webRequest.getParameterValues(
						lStrFormFieldName + "." + lPropertyDescriptorsArrBean[i].getName());
				if (lStrArrValues != null && lStrArrValues.length == 1) lStrArrPropertyValues[i] = lStrArrValues[0];
			}
			return instantiateBean(lClassBeanType, lPropertyDescriptorsArrBean, lStrArrPropertyValues);
		} else {
			ArrayList<Object> lListModels = new ArrayList<>();
			for (int i = 0; i < lnModelListLength; i++) {
				String[] lStrArrPropertyValues = new String[lPropertyDescriptorsArrBean.length];
				for (int j = 0; j < lPropertyDescriptorsArrBean.length; j++) {
					PropertyDescriptor propertyDescriptor = lPropertyDescriptorsArrBean[j];
					String lStrName = lStrFormFieldName + "." + propertyDescriptor.getName();
					String[] lStrArrValues = lMapRequestParams.get(lStrName);
					if (lStrArrValues != null) lStrArrPropertyValues[j] = lStrArrValues[i];
				}
				lListModels.add(instantiateBean(lClassBeanType, lPropertyDescriptorsArrBean, lStrArrPropertyValues));
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
			Object lObjPropertyValue = conversionService.convert(propertyValues[i], lClazzPropertyType);
			Method lMethodSetter = propertyDescriptor.getWriteMethod();
			if (lMethodSetter == null) continue;
			lMethodSetter.invoke(lObjBean, lObjPropertyValue);
		}
		return lObjBean;
	}

	public static class StringToAddonSiteTypeV3Converter implements Converter<String, AddonSiteTypeV3> {
		@Override
		public AddonSiteTypeV3 convert(String s) {
			return AddonSiteTypeV3.forString(s);
		}
	}
}

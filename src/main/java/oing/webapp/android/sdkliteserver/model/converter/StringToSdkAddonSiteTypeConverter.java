package oing.webapp.android.sdkliteserver.model.converter;

import oing.webapp.android.sdkliteserver.model.SdkAddonSite;
import org.springframework.core.convert.converter.Converter;

public class StringToSdkAddonSiteTypeConverter implements Converter<String, SdkAddonSite.Type> {
	@Override
	public SdkAddonSite.Type convert(String s) {
		return SdkAddonSite.Type.forString(s);
	}
}

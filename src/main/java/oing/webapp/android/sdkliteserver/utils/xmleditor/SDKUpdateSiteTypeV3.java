package oing.webapp.android.sdkliteserver.utils.xmleditor;

/**
 * addons_list-3.xml sdk update site types.
 */
public enum SDKUpdateSiteTypeV3 {
	/**
	 * sdk:addonSiteType
	 */
	ADDON_SITE_TYPE("addonSiteType"),
	/**
	 * sdk:sysImgSiteType
	 */
	SYS_IMG_SITE_TYPE("sysImgSiteType");

	private final String type;

	SDKUpdateSiteTypeV3(String type) {
		this.type = type;
	}

	public static SDKUpdateSiteTypeV3 forString(String text) {
		for (SDKUpdateSiteTypeV3 siteType : values()) {
			if (siteType.type.contains(text)) return siteType;
		}
		throw new IllegalArgumentException("Unknown SDK update site type: " + text);
	}

	@Override
	public String toString() {
		return super.toString() + "(" + this.type + ")";
	}
}

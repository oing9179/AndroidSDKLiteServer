package oing.webapp.android.sdkliteserver.utils.xmleditor;

public enum AddonSiteTypeV3 {
	UNKNOWN_SITE_TYPE("unknown:unknownAddonSiteType", "UNKNOWN"),
	SYS_IMG_SITE_TYPE("sdk:sysImgSiteType", "SysImg"),
	ADDON_SITE_TYPE("sdk:addonSiteType", "Addon");

	private final String type;
	private final String friendlyName;

	AddonSiteTypeV3(String type, String friendlyName) {
		this.type = type;
		this.friendlyName = friendlyName;
	}

	public static AddonSiteTypeV3 forString(String type) {
		for (AddonSiteTypeV3 addonSiteType : values()) {
			if (type.equals(addonSiteType.type)) return addonSiteType;
		}
		return UNKNOWN_SITE_TYPE;
	}

	public String value() {
		return type;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + this.friendlyName + ")";
	}
}

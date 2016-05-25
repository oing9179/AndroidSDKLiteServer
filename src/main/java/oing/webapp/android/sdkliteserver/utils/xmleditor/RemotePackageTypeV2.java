package oing.webapp.android.sdkliteserver.utils.xmleditor;

/**
 * Types from repository2-*.xml on element /remotePackage/type-details.xsi:type.
 */
public enum RemotePackageTypeV2 {
	UNKNOWN_PACKAGE_TYPE("unknownPackageType", "UNKNOWN"),
	GENERIC_GENERIC_DETAILS_TYPE("genericDetailsType", "Generic"),
	REPOSITORY_PLATFORM_DETAILS_TYPE("platformDetailsType", "Platform"),
	REPOSITORY_SOURCE_DETAILS_TYPE("sourceDetailsType", "Source"),
	SYSIMG_SYS_IMG_DETAILS_TYPE("sysImgDetailsType", "SysImg"),
	ADDON_ADDON_DETAILS_TYPE("addonDetailsType", "Addon"),
	ADDON_EXTRA_DETAILS_TYPE("extraDetailsType", "Extra");
	/**
	 * Known detailsTypes:
	 * generic:genericDetailsType,
	 * sdk:platformDetailsType,
	 * sys-img:sysImgDetailsType,
	 * addon:addonDetailsType
	 */

	private final String type;
	private final String friendlyName;

	RemotePackageTypeV2(String type, String friendlyName) {
		this.type = type;
		this.friendlyName = friendlyName;
	}

	public static RemotePackageTypeV2 forString(String text) {
		for (RemotePackageTypeV2 packageType : values()) {
			if (text.contains(packageType.type)) return packageType;
		}
		return UNKNOWN_PACKAGE_TYPE;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + this.friendlyName + ")";
	}
}

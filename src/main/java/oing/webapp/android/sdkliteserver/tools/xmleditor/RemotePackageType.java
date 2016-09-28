package oing.webapp.android.sdkliteserver.tools.xmleditor;

public enum RemotePackageType {
	UNKNOWN("Unknown"),
	GENERIC_TYPE("Generic"),
	PLATFORM_TYPE("Platform"),
	SOURCE_TYPE("Source"),
	SYSTEM_IMAGE_TYPE("SysImg"),
	ADDON_TYPE("Addon"),
	ADDON_EXTRA_TYPE("Addon-Extra"),
	ADDON_MAVEN_TYPE("Addon-Maven");

	private final String friendlyName;

	RemotePackageType(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public static RemotePackageType lookupForFriendlyName(String friendlyName) {
		for (RemotePackageType remotePackageType : values()) {
			if (remotePackageType.friendlyName.equals(friendlyName)) {
				return remotePackageType;
			}
		}
		return null;
	}

}

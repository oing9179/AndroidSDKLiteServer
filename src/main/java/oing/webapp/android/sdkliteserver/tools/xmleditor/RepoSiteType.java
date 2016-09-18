package oing.webapp.android.sdkliteserver.tools.xmleditor;

public enum RepoSiteType {
	UNKNOWN("Unknown"),
	ADDON_SITE("Addon"),
	SYSTEM_IMAGE_SITE("SysImg");

	private final String friendlyName;

	RepoSiteType(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public static RepoSiteType lookupByFriendlyName(String friendlyName) {
		for (RepoSiteType repoSiteType : values()) {
			if (repoSiteType.friendlyName.equals(friendlyName)) {
				return repoSiteType;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}

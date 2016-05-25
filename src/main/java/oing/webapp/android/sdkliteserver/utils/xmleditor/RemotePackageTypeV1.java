package oing.webapp.android.sdkliteserver.utils.xmleditor;

/**
 * Remote package lookup for "repository-12.xml".
 */
public enum RemotePackageTypeV1 {
	PLATFORM("platform", "Platform-LegacyV1"),
	SOURCE("source", "Source-LegacyV1"),
	TOOL("tool", "Tool-LegacyV1"),
	PLATFORM_TOOL("platform-tool", "PlatformTool-LegacyV1"),
	BUILD_TOOL("build-tool", "BuildTool-LegacyV1"),
	DOC("doc", "Doc-LegacyV1"),
	SAMPLE("sample", "Sample-LegacyV1"),
	NDK("ndk", "NDK-LegacyV1"),
	LLDB("lldb", "LLDB-LegacyV1"),
	ADD_ON("add-on", "AddOn-LegacyV1"),
	EXTRA("extra", "Extra-LegacyV1"),
	SYSTEM_IMAGE("system-image", "SystemImage-LegacyV1");

	private final String type;
	private final String friendlyName;

	RemotePackageTypeV1(String type, String friendlyName) {
		this.type = type;
		this.friendlyName = friendlyName;
	}

	public static RemotePackageTypeV1 forString(String text) {
		for (RemotePackageTypeV1 packageType : values()) {
			if (text.equals(packageType.type)) return packageType;
		}
		throw new IllegalArgumentException("Unknown remote-package type(v1): " + text);
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + this.friendlyName + ")";
	}
}

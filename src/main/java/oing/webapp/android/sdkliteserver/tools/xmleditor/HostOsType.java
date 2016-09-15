package oing.webapp.android.sdkliteserver.tools.xmleditor;

public enum HostOsType {
	Any(null), Linux("linux"), MacOSX("macosx"), Windows("windows");

	private final String value;

	HostOsType(String value) {
		this.value = value;
	}

	public static HostOsType forString(String text) {
		if (text == null) return Any;
		for (HostOsType hostOs : values()) {
			if (text.equals(hostOs.value)) return hostOs;
		}
		throw new IllegalArgumentException("Unknown host-os type: " + text);
	}

	@Override
	public String toString() {
		return value;
	}
}


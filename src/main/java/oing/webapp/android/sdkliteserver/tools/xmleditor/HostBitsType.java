package oing.webapp.android.sdkliteserver.tools.xmleditor;

public enum HostBitsType {
	Any(null), X86("32"), X86_64("64");

	private final String value;

	HostBitsType(String value) {
		this.value = value;
	}

	public static HostBitsType forString(String text) {
		if (text == null) return Any;
		for (HostBitsType hostBits : values()) {
			if (text.equals(hostBits.value)) return hostBits;
		}
		throw new IllegalArgumentException("Unknown host-os type: " + text);
	}

	@Override
	public String toString() {
		return value;
	}
}


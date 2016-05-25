package oing.webapp.android.sdkliteserver.model;

public class SdkAddonSite {
	private Type type;
	private String url;
	private String name;

	public SdkAddonSite() {
	}

	public SdkAddonSite(Type type, String url, String name) {
		this.type = type;
		this.url = url;
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * sdk-addons-getAll types.
	 */
	public enum Type {
		/**
		 * addon-site
		 */
		ADDON_SITE("addon-site"),
		/**
		 * sys-img-site
		 */
		SYS_IMG_SITE("sys-img-site");

		private final String name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static Type forString(String name) {
			switch (name) {
				case "addon-site":
					return ADDON_SITE;
				case "sys-img-site":
					return SYS_IMG_SITE;
				default:
					throw new IllegalArgumentException("Unknown sdk-addons-list type: " + name);
			}
		}

		@Override
		public String toString() {
			return getName();
		}
	}
}

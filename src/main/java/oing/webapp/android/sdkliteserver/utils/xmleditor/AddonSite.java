package oing.webapp.android.sdkliteserver.utils.xmleditor;

import com.alibaba.fastjson.annotation.JSONField;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;

public class AddonSite {
	private String sourceUrl;
	private AddonSiteTypeV3 type;
	private String displayName;
	private String url;

	public AddonSite() {
	}

	private AddonSite(Builder builder) {
		this.sourceUrl = builder.sourceUrl;
		this.type = builder.type;
		this.displayName = builder.displayName;
		this.url = builder.url;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public AddonSiteTypeV3 getType() {
		return type;
	}

	public void setType(AddonSiteTypeV3 type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JSONField(deserialize = false)
	public String getAbsoluteUrl() {
		return UrlTextUtil.concat(sourceUrl, getUrl());
	}

	public static class Builder {
		private String sourceUrl;
		private AddonSiteTypeV3 type;
		private String displayName;
		private String url;

		public Builder() {
		}

		public AddonSite build() {
			return new AddonSite(this);
		}

		public Builder sourceUrl(String value) {
			this.sourceUrl = value;
			return this;
		}

		public Builder type(AddonSiteTypeV3 value) {
			this.type = value;
			return this;
		}

		public Builder displayName(String value) {
			this.displayName = value;
			return this;
		}

		public Builder url(String value) {
			this.url = value;
			return this;
		}
	}
}

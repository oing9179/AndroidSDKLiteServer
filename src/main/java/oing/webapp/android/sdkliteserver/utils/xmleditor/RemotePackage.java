package oing.webapp.android.sdkliteserver.utils.xmleditor;

import java.util.LinkedList;
import java.util.List;

public class RemotePackage {
	private String type;
	private String baseUrl;
	private String displayName;
	private String revision;
	private String channel;
	private Integer apiLevel;
	private Boolean isObsoleted;
	private List<Archive> archives;

	public RemotePackage() {
		archives = new LinkedList<>();
	}

	private RemotePackage(Builder builder) {
		this.type = builder.type;
		this.baseUrl = builder.baseUrl;
		this.displayName = builder.displayName;
		this.revision = builder.revision;
		this.channel = builder.channel;
		this.apiLevel = builder.apiLevel;
		this.isObsoleted = builder.isObsoleted;
		this.archives = builder.archives;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Integer getApiLevel() {
		return apiLevel;
	}

	public void setApiLevel(Integer apiLevel) {
		this.apiLevel = apiLevel;
	}

	public Boolean getObsoleted() {
		return isObsoleted;
	}

	public void setObsoleted(Boolean obsoleted) {
		isObsoleted = obsoleted;
	}

	public List<Archive> getArchives() {
		return archives;
	}

	public void setArchives(List<Archive> archives) {
		this.archives = archives;
	}

	public static class Builder {
		private String type;
		private String baseUrl;
		private String displayName;
		private String revision;
		private String channel;
		private Integer apiLevel;
		private Boolean isObsoleted;
		private List<Archive> archives;

		public Builder() {
			this.archives = new LinkedList<>();
		}

		public RemotePackage build() {
			return new RemotePackage(this);
		}

		public Builder type(String value) {
			this.type = value;
			return this;
		}

		public Builder baseUrl(String value) {
			this.baseUrl = value;
			return this;
		}

		public Builder displayName(String value) {
			this.displayName = value;
			return this;
		}

		public Builder revision(String value) {
			this.revision = value;
			return this;
		}

		public Builder channel(String value) {
			this.channel = value;
			return this;
		}

		public Builder apiLevel(Integer value) {
			this.apiLevel = value;
			return this;
		}

		public Builder isObsoleted(Boolean value) {
			this.isObsoleted = value;
			return this;
		}

		public Builder addArchive(Archive archive) {
			this.archives.add(archive);
			return this;
		}
	}
}

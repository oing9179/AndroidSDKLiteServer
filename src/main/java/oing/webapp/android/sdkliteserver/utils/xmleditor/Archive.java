package oing.webapp.android.sdkliteserver.utils.xmleditor;

import com.alibaba.fastjson.annotation.JSONField;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;

public abstract class Archive {
	protected transient RemotePackage remotePackageRef;
	protected Long size;
	protected String checksum;
	protected String url;
	protected HostOsType hostOs;
	protected HostBitsType hostBits;
	protected Boolean isExisted;

	public Archive() {
	}

	protected Archive(Builder builder) {
		this.remotePackageRef = builder.remotePackageRef;
		this.size = builder.size;
		this.checksum = builder.checksum;
		this.url = builder.url;
		this.hostOs = builder.hostOs;
		this.hostBits = builder.hostBits;
		this.isExisted = builder.isExisted;
	}

	public RemotePackage getRemotePackageRef() {
		return remotePackageRef;
	}

	public void setRemotePackageRef(RemotePackage remotePackageRef) {
		this.remotePackageRef = remotePackageRef;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getUrl() {
		return url;
	}

	@JSONField(deserialize = false)
	public String getAbsoluteUrl() {
		return UrlTextUtil.concat(remotePackageRef.getBaseUrl(), getUrl());
	}

	public String getFileName() {
		return UrlTextUtil.getFileName(getUrl());
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HostOsType getHostOs() {
		return hostOs;
	}

	public void setHostOs(HostOsType hostOs) {
		this.hostOs = hostOs;
	}

	public HostBitsType getHostBits() {
		return hostBits;
	}

	public void setHostBits(HostBitsType hostBits) {
		this.hostBits = hostBits;
	}

	public Boolean isExisted() {
		return isExisted;
	}

	public void setExisted(Boolean existed) {
		isExisted = existed;
	}

	protected abstract static class Builder {
		private RemotePackage remotePackageRef;
		private Long size;
		private String checksum;
		private String url;
		private HostOsType hostOs;
		private HostBitsType hostBits;
		private Boolean isExisted;

		public Builder(RemotePackage remotePackage) {
			this.remotePackageRef = remotePackage;
		}

		public abstract Archive build();

		public Builder size(Long value) {
			this.size = value;
			return this;
		}

		public Builder checksum(String value) {
			this.checksum = value;
			return this;
		}

		public Builder url(String value) {
			this.url = value;
			return this;
		}

		public Builder hostOs(HostOsType value) {
			this.hostOs = value;
			return this;
		}

		public Builder hostBits(HostBitsType value) {
			this.hostBits = value;
			return this;
		}

		public Builder isExisted(Boolean value) {
			this.isExisted = value;
			return this;
		}
	}
}

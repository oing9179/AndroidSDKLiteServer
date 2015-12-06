package oing.webapp.android.sdkliteserver.model;

import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;

public class SdkArchive {
	private Long id;
	private Long idRepoXmlFile;
	private String type;
	private String displayName;
	private String description;
	private String version;
	private String revision;
	private Integer apiLevel;
	private Boolean isObsoleted;
	private String url;
	private String checksumType;
	private String checksum;
	private Long size;
	private String hostOs;
	private String hostBits;
	private Boolean isExisted;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdRepoXmlFile() {
		return idRepoXmlFile;
	}

	public void setIdRepoXmlFile(Long idRepoXmlFile) {
		this.idRepoXmlFile = idRepoXmlFile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Integer getApiLevel() {
		return apiLevel;
	}

	public void setApiLevel(Integer apiLevel) {
		this.apiLevel = apiLevel;
	}

	public Boolean isObsoleted() {
		return isObsoleted;
	}

	public void setIsObsoleted(Boolean isObsolute) {
		this.isObsoleted = isObsolute;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		lStrFileNameCache = null;
	}

	private String lStrFileNameCache = null;

	/**
	 * Generated from {@link #getUrl()}
	 */
	public String getFileName() {
		if (lStrFileNameCache == null) lStrFileNameCache = UrlTextUtil.getFileName(getUrl());
		return lStrFileNameCache;
	}

	public String getChecksumType() {
		return checksumType;
	}

	public void setChecksumType(String checksumType) {
		this.checksumType = checksumType;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getHostOs() {
		return hostOs;
	}

	public void setHostOs(String hostOs) {
		this.hostOs = hostOs;
	}

	public String getHostBits() {
		return hostBits;
	}

	public void setHostBits(String hostBits) {
		this.hostBits = hostBits;
	}

	public Boolean isExisted() {
		return isExisted;
	}

	public void setIsExisted(Boolean existed) {
		isExisted = existed;
	}
}

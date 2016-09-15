package oing.webapp.android.sdkliteserver.model;

public class RepoXmlFile {
	private Long id;
	private Long idRepoXml;
	private String fileName;
	private String url;

	public RepoXmlFile() {
	}

	private RepoXmlFile(Builder builder) {
		this.id = builder.id;
		this.idRepoXml = builder.idRepoXml;
		this.fileName = builder.fileName;
		this.url = builder.url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdRepoXml() {
		return idRepoXml;
	}

	public void setIdRepoXml(Long idRepoXml) {
		this.idRepoXml = idRepoXml;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public static class Builder {
		private Long id;
		private Long idRepoXml;
		private String fileName;
		private String url;

		public RepoXmlFile build() {
			return new RepoXmlFile(this);
		}

		public Builder id(Long value) {
			this.id = value;
			return this;
		}

		public Builder idRepoXml(Long value) {
			this.idRepoXml = value;
			return this;
		}

		public Builder fileName(String value) {
			this.fileName = value;
			return this;
		}

		public Builder url(String value) {
			this.url = value;
			return this;
		}
	}
}

package oing.webapp.android.sdkliteserver.model;

public class RepoXmlFile {
	private Long id;
	private Long idRepoXml;
	private String fileName;
	private String url;

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
}

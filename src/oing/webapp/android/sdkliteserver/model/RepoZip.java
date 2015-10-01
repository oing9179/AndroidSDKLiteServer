package oing.webapp.android.sdkliteserver.model;

import java.util.Date;

public class RepoZip {
	private Long id;
	private Long idRepoXml;
	private String name;
	private Date dateCreate;
	private Date dateLastModified;
	// fields that does not exists in table.
	private String repoXml_name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Date getDateLastModified() {
		return dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public String getRepoXml_name() {
		return repoXml_name;
	}

	public void setRepoXml_name(String repoXml_name) {
		this.repoXml_name = repoXml_name;
	}
}

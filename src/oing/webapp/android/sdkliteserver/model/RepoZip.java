package oing.webapp.android.sdkliteserver.model;

import java.util.Date;

public class RepoZip {
	private Long id;
	private Long idRepoXml;
	private String name;
	private Date dateCreation;
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

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
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

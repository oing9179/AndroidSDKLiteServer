package oing.webapp.android.sdkliteserver.model;

import java.util.Date;

/**
 * A database entity for table "repo_xml"
 */
public class RepoXml {
	private Long id;
	private String name;
	private Date dateCreation;
	private Date dateLastModified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return super.toString() + " (id: " + id + ", name=" + name + ")";
	}
}

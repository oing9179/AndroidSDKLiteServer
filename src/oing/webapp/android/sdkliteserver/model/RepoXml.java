package oing.webapp.android.sdkliteserver.model;

import java.util.Date;

/**
 * A database entity for table "repo_xml"
 */
public class RepoXml {
	private Long id;
	private String name;
	private Date dateCreate;
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

	@Override
	public String toString() {
		return super.toString() + " (id: " + id + ", name=" + name + ")";
	}
}

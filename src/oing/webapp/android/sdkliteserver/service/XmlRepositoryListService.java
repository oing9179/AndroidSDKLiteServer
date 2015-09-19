package oing.webapp.android.sdkliteserver.service;

import oing.webapp.android.sdkliteserver.model.RepoXml;

import java.io.IOException;
import java.util.List;

public interface XmlRepositoryListService {
	/**
	 * Get all of the repositories form database.
	 */
	List<RepoXml> getAll();

	/**
	 * Select from table "repo_xml" by id
	 */
	RepoXml getById(Long id);

	/**
	 * Create a repository
	 *
	 * @param name       The name of new repository.
	 * @param createFrom The ID of existing repository, and copy data and file into new repository.
	 */
	void create(String name, Long createFrom) throws IOException;

	/**
	 * Delete a repository
	 *
	 * @param id   The repository will be delete.
	 * @param name For web form validation
	 */
	void delete(Long id, String name) throws IOException;
}

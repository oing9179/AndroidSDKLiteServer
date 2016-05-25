package oing.webapp.android.sdkliteserver.service;

import oing.webapp.android.sdkliteserver.model.RepoZip;

import java.io.IOException;
import java.util.List;

public interface ZipRepositoryListService {

	/**
	 * Select all data from repo_zip.
	 */
	List<RepoZip> getAll();

	/**
	 * Select RepoZip by id
	 */
	RepoZip getById(Long id);

	/**
	 * Select RepoZip by name
	 */
	RepoZip getByName(String name);

	/**
	 * Find out how many zip repositories depends on this({@code repoXmlId}) xml repository.
	 */
	List<RepoZip> getDependsRepoXmlId(Long idRepoXml);

	/**
	 * Create a repository
	 */
	void create(String name);

	/**
	 * Delete a repository
	 *
	 * @param id   The id of zip repository.
	 * @param name For web form validation.
	 */
	void delete(Long id, String name) throws IOException;
}
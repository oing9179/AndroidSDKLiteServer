package oing.webapp.android.sdkliteserver.service;

import oing.webapp.android.sdkliteserver.model.RepoXml;

import java.io.IOException;
import java.util.List;

public interface XmlRepositoryService {
	/**
	 * Get all of the repositories form database.
	 */
	List<RepoXml> getAll();

	/**
	 * Create a repository(step 1/2)<br/>
	 * Spring will commit SqlSession, after that, do step 2.
	 *
	 * @param name       The name of new repository.
	 * @param createFrom The ID of existing repository, and copy data and file into new repository.
	 */
	void repositoryCreate_step1(String name, Long createFrom) throws IOException;

	/**
	 * Create a repository(step 2/2)<br/>
	 * In this method, we do only copy xml files into new repository, so no SqlSession required.
	 *
	 * @param name       The name of new repository.
	 * @param createFrom The ID of existing repository, and copy data and file into new repository.
	 */
	void repositoryCreate_step2(String name, Long createFrom) throws IOException;
}

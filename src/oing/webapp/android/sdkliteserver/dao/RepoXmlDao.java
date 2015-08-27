package oing.webapp.android.sdkliteserver.dao;

import oing.webapp.android.sdkliteserver.model.RepoXml;

import java.util.List;

public interface RepoXmlDao {
	/**
	 * Get all of the repositories form database.
	 */
	List<RepoXml> selectAll();

	/**
	 * Returns the name of repo_xml was exist.
	 *
	 * @param name The name of repo_xml.
	 * @return {@code true} if exists.
	 */
	boolean existsByName(String name);

	/**
	 * Select a record by id
	 */
	RepoXml selectById(Long id);

	/**
	 * Select a record by name, there will only one result cuz repo_xml.name is unique.
	 */
	RepoXml selectByName(String name);

	/**
	 * Copy existing xml file into an existing xml repo
	 *
	 * @param fromId The source xml repository id.
	 * @param toId   The destination xml repository id.
	 */
	int copyExistingXmlFilesIntoExistingXmlRepo(Long fromId, Long toId);

	/**
	 * Add a RepoXml to database.
	 *
	 * @return Number of lines affected.
	 */
	int insert(RepoXml repoXml);
}

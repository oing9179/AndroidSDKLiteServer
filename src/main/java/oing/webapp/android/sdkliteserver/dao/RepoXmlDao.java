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
	 * Add a RepoXml to database.
	 *
	 * @return Number of rows affected.
	 */
	int insert(RepoXml repoXml);

	/**
	 * Update RepoXml by id
	 */
	int updateById(RepoXml repoXml);

	/**
	 * Delete a RepoXml from database by ID.<br/>
	 *
	 * @return Number of rows affected.
	 */
	int deleteById(Long id);
}

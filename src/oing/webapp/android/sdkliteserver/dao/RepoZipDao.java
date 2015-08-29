package oing.webapp.android.sdkliteserver.dao;

import oing.webapp.android.sdkliteserver.model.RepoZip;

import java.util.List;

public interface RepoZipDao {
	/**
	 * Select from repo_zip where id_repo_xml=repo_xml.id
	 */
	List<RepoZip> selectDependsOnRepoXmlId(Long idRepoXml);
	/**
	 * Clear dependency from repo_xml, zip repositories that updated will depends nothing.
	 * @param idRepoXml The repo_xml.id
	 * @return Number of rows affected.
	 */
	int updateClearDependencyFromRepoXml(Long idRepoXml);
}

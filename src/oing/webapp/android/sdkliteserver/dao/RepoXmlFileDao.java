package oing.webapp.android.sdkliteserver.dao;

public interface RepoXmlFileDao {
	/**
	 * Delete from repo_xml_file where id_repo_xml=repo_xml.id
	 */
	int deleteDependsRepoXmlId(Long idRepoXml);
}

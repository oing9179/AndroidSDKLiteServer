package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.BaseDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RepoXmlFileDaoImpl extends BaseDao implements RepoXmlFileDao {
	private static final String MAPPER_NAMESPACE = "RepoXmlFile";

	@Override
	public RepoXmlFile selectByFileName(String name) {
		return super.selectOne("selectByFileName", name);
	}

	@Override
	public List<RepoXmlFile> selectDependsRepoXmlId(Long idRepoXml) {
		return super.selectList("selectByRepoXmlId", idRepoXml);
	}

	@Override
	public int insert(RepoXmlFile repoXmlFile) {
		return super.insert("insert", repoXmlFile);
	}

	@Override
	public int insertOrUpdate(RepoXmlFile repoXmlFile) {
		return super.insertOrUpdateById(repoXmlFile);
	}

	@Override
	public int deleteDependsRepoXmlId(Long idRepoXml) {
		return super.delete("deleteDependsRepoXmlId", idRepoXml);
	}

	@Override
	protected String getMapperNamespace() {
		return MAPPER_NAMESPACE;
	}
}

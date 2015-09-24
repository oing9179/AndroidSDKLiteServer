package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.BaseDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class RepoXmlFileDaoImpl extends BaseDao implements RepoXmlFileDao {
	private static final String MAPPER_NAMESPACE = "RepoXmlFile";

	@Override
	public RepoXmlFile selectByIdDependsRepoXmlId(Long id, Long repoXmlId) {
		HashMap<String, Object> mMapParams = new HashMap<>();
		mMapParams.put("id", id);
		mMapParams.put("repoXmlId", repoXmlId);
		return super.selectOne("selectByIdDependsRepoXmlId", mMapParams);
	}

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
	public int copyExistingRecordsForNewXmlRepo(Long fromId, Long toId) {
		HashMap<String, Object> mMapParams = new HashMap<>(2);
		mMapParams.put("fromId", fromId);
		mMapParams.put("toId", toId);
		return super.insert("copyExistingXmlFilesIntoNewXmlRepo", mMapParams);
	}

	@Override
	public int deleteDependsRepoXmlId(Long idRepoXml) {
		return super.delete("deleteDependsRepoXmlId", idRepoXml);
	}

	@Override
	public int deleteById(Long id) {
		return super.delete("deleteById", id);
	}

	@Override
	protected String getMapperNamespace() {
		return MAPPER_NAMESPACE;
	}
}

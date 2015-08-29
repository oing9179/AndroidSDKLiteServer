package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.BaseDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RepoZipDaoImpl extends BaseDao implements RepoZipDao {
	private static final String MAPPER_NAMESPACE = "RepoZip";

	@Override
	public List<RepoZip> selectDependsOnRepoXmlId(Long idRepoXml) {
		return super.selectList("selectByRepoXmlId", idRepoXml);
	}

	@Override
	public int updateClearDependencyFromRepoXml(Long idRepoXml) {
		return super.update("updateClearDependencyFromRepoXml", idRepoXml);
	}

	@Override
	protected String getMapperNamespace() {
		return MAPPER_NAMESPACE;
	}
}
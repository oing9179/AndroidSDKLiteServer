package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.BaseDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import org.springframework.stereotype.Component;

@Component
public class RepoXmlFileDaoImpl extends BaseDao implements RepoXmlFileDao {
	private static final String MAPPER_NAMESPACE = "RepoXmlFile";

	@Override
	public int deleteDependsRepoXmlId(Long idRepoXml) {
		super.delete("deleteDependsRepoXmlId", idRepoXml);
		return 0;
	}

	@Override
	protected String getMapperNamespace() {
		return MAPPER_NAMESPACE;
	}
}

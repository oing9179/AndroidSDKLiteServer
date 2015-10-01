package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.BaseDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class RepoXmlDaoImpl extends BaseDao implements RepoXmlDao {
	private static final String MAPPER_NAMESPACE = "RepoXml";

	@Override
	public List<RepoXml> selectAll() {
		return super.selectList("selectAll", null);
	}

	@Override
	public boolean existsByName(String name) {
		return selectByName(name) != null;
	}

	public RepoXml selectById(Long id) {
		return super.selectOne("selectById", id);
	}

	public RepoXml selectByName(String name) {
		return super.selectOne("selectByName", name);
	}

	@Override
	public int insert(RepoXml repoXml) {
		return super.insert(repoXml);
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

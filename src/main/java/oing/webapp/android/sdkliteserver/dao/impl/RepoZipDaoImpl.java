package oing.webapp.android.sdkliteserver.dao.impl;

import oing.webapp.android.sdkliteserver.dao.BaseDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

@Component
public class RepoZipDaoImpl extends BaseDao implements RepoZipDao {
	private static final String MAPPER_NAMESPACE = "RepoZip";

	@Override
	public List<RepoZip> selectAll() {
		return super.selectList("selectAll", null);
	}

	@Override
	public RepoZip selectById(Long id) {
		return super.selectOne("selectById", id);
	}

	@Override
	public RepoZip selectByName(String name) {
		RepoZip lRepoZip = super.selectOne("selectByName", name);
		loadRepoInfoFromDisk(lRepoZip);
		return lRepoZip;
	}

	@Override
	public List<RepoZip> selectDependsOnRepoXmlId(Long idRepoXml) {
		return super.selectList("selectByRepoXmlId", idRepoXml);
	}

	@Override
	public int insert(RepoZip repoZip) {
		repoZip.setDateLastModified(new Date());
		return super.insert(repoZip);
	}

	@Override
	public int updateById(RepoZip repoZip) {
		repoZip.setDateLastModified(new Date());
		return super.updateById(repoZip);
	}

	@Override
	public int updateClearDependencyFromRepoXml(Long idRepoXml) {
		return super.update("updateClearDependencyFromRepoXml", idRepoXml);
	}

	@Override
	public int deleteById(Long id) {
		return super.delete("deleteById", id);
	}

	@Override
	protected String getMapperNamespace() {
		return MAPPER_NAMESPACE;
	}

	/**
	 * Calc total file count and total file size of this repository.
	 */
	private void loadRepoInfoFromDisk(RepoZip repoZip) {
		if (repoZip == null) return;
		long lnTotalFileSize = 0;
		File[] lFileArrZipFiles = ConfigurationUtil.getZipRepositoryDir(repoZip.getName()).listFiles();
		repoZip.setTotalFileCount(lFileArrZipFiles.length);
		for (File file : lFileArrZipFiles) {
			lnTotalFileSize += file.length();
		}
		repoZip.setTotalFileSize(lnTotalFileSize);
	}
}

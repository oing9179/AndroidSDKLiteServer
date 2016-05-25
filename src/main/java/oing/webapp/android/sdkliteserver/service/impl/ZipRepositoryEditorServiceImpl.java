package oing.webapp.android.sdkliteserver.service.impl;

import jodd.io.FileUtil;
import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.model.SdkArchive;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryEditorService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import oing.webapp.android.sdkliteserver.utils.RepositoryXmlEditor;
import oing.webapp.android.sdkliteserver.utils.UrlTextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ZipRepositoryEditorServiceImpl implements ZipRepositoryEditorService {
	private static final Logger mLogger = LoggerFactory.getLogger(ZipRepositoryEditorServiceImpl.class);
	@Autowired
	private RepoZipDao repoZipDao;
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@Override
	public void updateRepositoryDependency(String repositoryName, Long targetRepoId) {
		RepoZip lRepoZip = zipRepositoryListService.getByName(repositoryName);
		lRepoZip.setIdRepoXml(targetRepoId);
		repoZipDao.updateById(lRepoZip);
	}

	private List<SdkArchive> getAllSdkArchiveInfo(String repositoryName) {
		RepoZip lRepoZip = repoZipDao.selectByName(repositoryName);
		RepoXml lRepoXml = repoXmlDao.selectById(lRepoZip.getIdRepoXml());
		File lFileXmlRepo = ConfigurationUtil.getXmlRepositoryDir(lRepoXml.getName());
		final File lFileZipRepo = ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName());
		List<RepoXmlFile> lListRepoXmlFiles = repoXmlFileDao.selectDependsRepoXmlId(lRepoXml.getId());
		List<SdkArchive> lListSdkArchives = new ArrayList<>();

		try {
			for (RepoXmlFile item : lListRepoXmlFiles) {
				if (item.getFileName().startsWith("addons_list")) continue;
				RepositoryXmlEditor repositoryXmlEditor = new RepositoryXmlEditor(item.getUrl(), new File(lFileXmlRepo, item.getFileName()));
				lListSdkArchives.addAll(repositoryXmlEditor.getSdkArchives(true, true));
			}
		} catch (Exception e) {
			mLogger.warn(e.toString(), e);
			e.printStackTrace();
		}
		lListSdkArchives.forEach(sdkArchive -> {
			File lFile = new File(lFileZipRepo, UrlTextUtil.getFileName(sdkArchive.getUrl()));
			sdkArchive.setIsExisted(lFile.exists());
		});
		return lListSdkArchives;
	}

	@Override
	public List<SdkArchive> getAllSdkArchiveInfo(String repositoryName, boolean isIncludeSysLinux, boolean isIncludeSysOSX,
	                                             boolean isIncludeSysWin, boolean isIncludeObsoleted, boolean isIncludeExisted) {
		List<SdkArchive> lListSdkArchives = getAllSdkArchiveInfo(repositoryName);

		// Do filter
		for (int i = 0; i < lListSdkArchives.size(); i++) {
			SdkArchive sdkArchive = lListSdkArchives.get(i);
			String lStrHostOs = sdkArchive.getHostOs();
			// 根据操作系统过滤掉不想要的zip包.
			if (lStrHostOs != null && ((!isIncludeSysLinux && lStrHostOs.equalsIgnoreCase("linux")) ||
					(!isIncludeSysOSX && lStrHostOs.equalsIgnoreCase("macosx")) ||
					(!isIncludeSysWin && lStrHostOs.equalsIgnoreCase("windows")))) {
				lListSdkArchives.remove(i--);
				continue;
			}
			// 判断是否排除该zip包
			int n = 0;
			n += !sdkArchive.isObsoleted() && !sdkArchive.isExisted() ? 1 : 0;// 该包不过时并且文件不存在.
			n += sdkArchive.isObsoleted() ? 1 : 0;// 该包已过时
			n -= !isIncludeObsoleted && sdkArchive.isObsoleted() ? 1 : 0;// 过时包需要被排除 并且 该包已过时
			n += sdkArchive.isExisted() ? 1 : 0;// 该包已存在
			n -= !isIncludeExisted && sdkArchive.isExisted() ? 1 : 0;// 已存在的包需要被排除 并且 该包已存在
			if (n <= 0) {// 最终结果<=0 则代表该包需要被排除
				lListSdkArchives.remove(i--);
			}
		}
		return lListSdkArchives;
	}

	@Override
	public List<SdkArchive> getNoLongerNeededArchives(String repositoryName, boolean isAbandonObsoleted, boolean isAbandonNotExisted) {
		RepoZip lRepoZip = repoZipDao.selectByName(repositoryName);
		File lFileRepoZip = ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName());
		List<File> lListZipFiles = Arrays.asList(lFileRepoZip.listFiles((dir, name) -> {
			return name.endsWith(".zip");
		}));
		lListZipFiles = new ArrayList<>(lListZipFiles);// Make it mutable.
		List<SdkArchive> lListSdkArchives = getAllSdkArchiveInfo(repositoryName);
		ArrayList<SdkArchive> lListAbandonedArchives = new ArrayList<>();

		for (SdkArchive sdkArchive : lListSdkArchives) {
			// 筛出 已经废弃的并且文件存在的SdkArchive
			if (isAbandonObsoleted && sdkArchive.isObsoleted() && sdkArchive.isExisted()) {
				lListAbandonedArchives.add(sdkArchive);
			}
			// 过滤出zip文件: 存在于zip仓库 但 不存在于所关联的xml仓库.
			for (int j = 0; j < lListZipFiles.size(); j++) {
				File lFileZip = lListZipFiles.get(j);
				// 若 文件能在关联的xml仓库中找到 则剔除, 反之保留.
				if (sdkArchive.getFileName().equals(lFileZip.getName())) {
					lListZipFiles.remove(j);
					break;
				}
				// 当前循环的外侧循环结束后, 剩下的就是不存在于xml仓库的zip文件.
			}
		}
		// 最后, 将不存在与xml仓库的zip文件 加入到 要被废弃的SdkArchive列表.
		for (File lFileZip : lListZipFiles) {
			SdkArchive sdkArchive = new SdkArchive();
			sdkArchive.setIsExisted(false);
			sdkArchive.setDisplayName(lFileZip.getName());
			sdkArchive.setSize(lFileZip.length());
			sdkArchive.setUrl(lFileZip.getName());
			lListAbandonedArchives.add(sdkArchive);
		}
		return lListAbandonedArchives;
	}

	@Override
	public void doRedundancyCleanup(String repositoryName, String[] fileNames) throws IOException {
		// Remove all path-separate characters in file name.
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = fileNames[i].replaceAll("/*", "").replaceAll("\\*", "");
		}
		// Do delete files
		final File lFileRepoZip = ConfigurationUtil.getZipRepositoryDir(repositoryName);
		for (String fileName : fileNames) {
			// deleteFile will fail if fileName is not an actual file.
			FileUtil.deleteFile(new File(lFileRepoZip, fileName));
		}
	}
}

package oing.webapp.android.sdkliteserver.service.impl;

import oing.webapp.android.sdkliteserver.dao.RepoXmlDao;
import oing.webapp.android.sdkliteserver.dao.RepoXmlFileDao;
import oing.webapp.android.sdkliteserver.dao.RepoZipDao;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryEditorService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.tools.xmleditor.Archive;
import oing.webapp.android.sdkliteserver.tools.xmleditor.HostOsType;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RemotePackage;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.IRepoCommonEditor;
import oing.webapp.android.sdkliteserver.tools.xmleditor.editor.RepoXmlEditorFactory;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZipRepositoryEditorServiceImpl implements ZipRepositoryEditorService {
	@Autowired
	private RepoXmlDao repoXmlDao;
	@Autowired
	private RepoXmlFileDao repoXmlFileDao;
	@Autowired
	private RepoZipDao repoZipDao;
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@Override
	public void updateRepositoryDependency(String repositoryName, Long targetRepoId) {
		RepoZip lRepoZip = zipRepositoryListService.getByNameOrThrow(repositoryName);
		lRepoZip.setIdRepoXml(targetRepoId);
		repoZipDao.updateById(lRepoZip);
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public List<RemotePackage> getAllRemotePackages(String repositoryName, boolean isIncludeSysLinux,
	                                                boolean isIncludeSysOSX, boolean isIncludeSysWin,
	                                                boolean isIncludeObsoleted, boolean isIncludeExisted) throws IOException, DocumentException {
		RepoZip lRepoZip = zipRepositoryListService.getByNameOrThrow(repositoryName);
		List<RemotePackage> lListRemotePackages = getAllRemotePackages(lRepoZip.getRepoXml_name(), lRepoZip.getName());

		// filter RemotePackages
		for (int index = 0; index < lListRemotePackages.size(); index++) {
			RemotePackage remotePackage = lListRemotePackages.get(index);
			if (!isIncludeObsoleted && remotePackage.isObsoleted()) {
				lListRemotePackages.remove(index--);
				continue;
			}
			List<Archive> lListArchives = remotePackage.getArchives();
			for (int index2 = 0; index2 < lListArchives.size(); index2++) {
				Archive lArchive = lListArchives.get(index2);
				if ((!isIncludeSysLinux && lArchive.getHostOs() == HostOsType.Linux)
						|| (!isIncludeSysOSX && lArchive.getHostOs() == HostOsType.MacOSX)
						|| (!isIncludeSysWin && lArchive.getHostOs() == HostOsType.Windows)) {
					lListArchives.remove(index2--);
					continue;
				}
				if (!isIncludeExisted && lArchive.isFileExisted()) {
					lListArchives.remove(index2--);
				}
			}
		}
		return lListRemotePackages;
	}

	@Override
	public List<Archive> getNoLongerNeededArchives(String repositoryName, boolean isIncludeObsoleted,
	                                               boolean isIncludeNotInRepo) throws IOException, DocumentException {
		RepoZip lRepoZip = zipRepositoryListService.getByNameOrThrow(repositoryName);
		List<RemotePackage> lListRemotePackages = getAllRemotePackages(lRepoZip.getRepoXml_name(), lRepoZip.getName());
		List<Archive> lListArchivesUnwanted = new LinkedList<>();

		// Find out obsoleted and existed file, then add it into lListArchivesUnwanted.
		lListRemotePackages.stream()
				.filter(remotePackage -> isIncludeObsoleted && remotePackage.isObsoleted())
				.forEach(remotePackage -> {
							lListArchivesUnwanted.addAll(remotePackage.getArchives()
									.stream()
									.filter(Archive::isFileExisted)
									.collect(Collectors.toList()));
						}
				);
		{
			// Find out zip files which is not defined in related xml repository.
			File lFileZipRepo = ConfigurationUtil.getZipRepositoryDir(lRepoZip.getName());
			File[] lFileArrZip = lFileZipRepo.listFiles((dir, name) -> name.lastIndexOf(".zip") != -1);
			if (lFileArrZip != null) {
				Arrays.parallelSort(lFileArrZip);
				List<Archive> lListArchivesNotExisted = new ArrayList<>();
				lListRemotePackages.forEach(remotePackage -> {
							lListArchivesNotExisted.addAll(remotePackage.getArchives().stream()
									/*
									 * The reason of "!archive.isFileExisted()" is:
									 * If archive does exist on local storage, that means the definition of this archive
									 * can be found in given xml repository, otherwise not.
									 */
									.filter(archive -> !archive.isFileExisted())
									.collect(Collectors.toList())
							);
						}
				);
				for (File file : lFileArrZip) {
					lListArchivesNotExisted.forEach(archive -> {
						if (file.equals(new File(lFileZipRepo, archive.getUrl()))) {
							lListArchivesUnwanted.add(archive);
						}
					});
				}
			}
		}
		return lListArchivesUnwanted;
	}

	@Override
	public void doRedundancyCleanup(String repositoryName, String[] fileNames) throws IOException {
		final File lFileRepoZip = ConfigurationUtil.getZipRepositoryDir(repositoryName);
		for (String fileName : fileNames) {
			File lFileZip = new File(lFileRepoZip, fileName);
			if (isFileInDir(lFileRepoZip, lFileZip)) {
				FileUtils.forceDelete(lFileZip);
			}
		}
	}

	/**
	 * Get all {@link RemotePackage}s from given xml repository name.
	 *
	 * @param repoNameXml XML repository name.
	 */
	private List<RemotePackage> getAllRemotePackages(String repoNameXml, String repoNameZip) throws IOException, DocumentException {
		RepoXml lRepoXml = repoXmlDao.selectByName(repoNameXml);
		Validate.notNull(lRepoXml, "XML repository not found: " + repoNameXml);
		List<RepoXmlFile> lListRepoXmlFiles = repoXmlFileDao.selectDependsRepoXmlId(lRepoXml.getId());
		List<RemotePackage> lListRemotePackages = new LinkedList<>();
		File lFileZipRepo = ConfigurationUtil.getZipRepositoryDir(repoNameZip);
		for (RepoXmlFile repoXmlFile : lListRepoXmlFiles) {
			if (repoXmlFile.getFileName().startsWith("addons_list")) {
				continue;
			}
			InputStream lInputStream = new BufferedInputStream(new FileInputStream(new File(
					ConfigurationUtil.getXmlRepositoryDir(repoNameXml),
					repoXmlFile.getFileName()
			)));
			IRepoCommonEditor lEditor = RepoXmlEditorFactory.createRepoCommonEditor(repoXmlFile.getUrl(), lInputStream);
			IOUtils.closeQuietly(lInputStream);
			lListRemotePackages.addAll(lEditor.extractAll(lFileZipRepo));
		}
		return lListRemotePackages;
	}

	private static boolean isFileInDir(File dir, File file) {
		return file.getAbsolutePath().startsWith(dir.getAbsolutePath()) && file.exists();
	}
}

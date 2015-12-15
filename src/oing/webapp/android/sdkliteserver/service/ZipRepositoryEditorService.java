package oing.webapp.android.sdkliteserver.service;

import oing.webapp.android.sdkliteserver.model.SdkArchive;

import java.io.IOException;
import java.util.List;

public interface ZipRepositoryEditorService {
	/**
	 * Change dependency to another xml repository.
	 *
	 * @param repositoryName Who will be changed.
	 * @param targetRepoId   Target xml repository id.
	 */
	void updateRepositoryDependency(String repositoryName, Long targetRepoId);

	/**
	 * Load archive info from xml repository, then group it.
	 *
	 * @param repositoryName     ZIP repository name
	 * @param isIncludeSysLinux  Include linux archives.
	 * @param isIncludeSysOSX    Include Mac OSX archives.
	 * @param isIncludeSysWin    Include Windows archives.
	 * @param isIncludeObsoleted Include obsoleted archives.
	 * @param isIncludeExisted   Include existing archives.
	 */
	List<SdkArchive> getAllSdkArchiveInfo(String repositoryName, boolean isIncludeSysLinux, boolean isIncludeSysOSX,
	                                      boolean isIncludeSysWin, boolean isIncludeObsoleted, boolean isIncludeExisted);

	/**
	 * Load archive info from xml repository, then find out which archives are not needed.
	 *
	 * @param repositoryName      ZIP repository name
	 * @param isIncludeObsoleted  Include obsoleted archives.
	 * @param isIncludeNotExisted Include existing archives.
	 */
	List<SdkArchive> getNoLongerNeededArchives(String repositoryName, boolean isIncludeObsoleted, boolean isIncludeNotExisted);

	/**
	 * Redundancy cleanup form specific ZIP repository.
	 *
	 * @param repositoryName ZIP repository name
	 * @param fileNames      Files name which will be deleted.
	 */
	void doRedundancyCleanup(String repositoryName, String[] fileNames) throws IOException;
}

package oing.webapp.android.sdkliteserver.service;

import oing.webapp.android.sdkliteserver.tools.xmleditor.Archive;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RemotePackage;
import org.dom4j.DocumentException;

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
	 * Load archive info from xml repository, then filter it.
	 *
	 * @param repositoryName     ZIP repository name
	 * @param isIncludeSysLinux  Include linux archives.
	 * @param isIncludeSysOSX    Include Mac OSX archives.
	 * @param isIncludeSysWin    Include Windows archives.
	 * @param isIncludeObsoleted Include obsoleted archives.
	 * @param isIncludeExisted   Include existing archives.
	 */
	List<RemotePackage> getAllRemotePackages(String repositoryName, boolean isIncludeSysLinux, boolean isIncludeSysOSX,
	                                         boolean isIncludeSysWin, boolean isIncludeObsoleted, boolean isIncludeExisted) throws IOException, DocumentException;

	/**
	 * Load archive info from xml repository, then find out which archives are not needed.
	 *  @param repositoryName     ZIP repository name
	 * @param isIncludeObsoleted If an archive is obsoleted and exists on local storage, it will be included.
	 * @param isIncludeNotInRepo If the definition of an archive can not be found in xml repository which given zip
	 */
	List<Archive> getNoLongerNeededArchives(String repositoryName, boolean isIncludeObsoleted, boolean isIncludeNotInRepo) throws IOException, DocumentException;

	/**
	 * Redundancy cleanup form specific ZIP repository.
	 *
	 * @param repositoryName ZIP repository name
	 * @param fileNames      Files name which will be deleted.
	 */
	void doRedundancyCleanup(String repositoryName, String[] fileNames) throws IOException;
}

package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.xmleditor.RemotePackage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Provide abilities to edit xml files that contains repository information.
 */
public interface IRepoCommonEditor {
	/**
	 * Extract all {@link RemotePackage}s from given xml document.
	 */
	List<RemotePackage> extractAll();

	/**
	 * Update URLs into xml document.<br/>
	 * For repo-common-v1.x: Under <code>sdk:archives/sdk:archive/sdk:url</code>.<br/>
	 * For repo-common-v2.x: Under <code>remotePackage/archives/*&#47;url</code>.<br/>
	 * @param listUrls URLs will update into xml document.
	 */
	void updateArchivesUrl(List<String> listUrls);

	/**
	 * Save XML file to local storage.
	 */
	void save() throws IOException;

	/**
	 * Save XML file to another destination.
	 * @param targetFile Destination file.
	 */
	void save(File targetFile) throws IOException;
}

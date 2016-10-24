package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import oing.webapp.android.sdkliteserver.model.RepoXmlFile;
import oing.webapp.android.sdkliteserver.model.RepoZip;
import oing.webapp.android.sdkliteserver.tools.xmleditor.RemotePackage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Provide abilities to edit xml files that contains repository information.
 */
public interface IRepoCommonEditor {
	/**
	 * Let editor knows more information about xml file, such as "zipSubDirectory".
	 *
	 * @param repoXmlFile A {@link RepoXmlFile} instance.
	 */
	void setRepoXmlFile(RepoXmlFile repoXmlFile);

	/**
	 * To detect an zip file exists or not, we need this property.
	 *
	 * @param repoZip A {@link RepoZip} instance.
	 */
	void setRepoZip(RepoZip repoZip);

	/**
	 * Extract all {@link RemotePackage}s from given xml document.
	 */
	List<RemotePackage> extractAll();

	/**
	 * Update URLs into xml document.<br/>
	 * For repo-common-v1.x: Under <code>sdk:archives/sdk:archive/sdk:url</code>.<br/>
	 * For repo-common-v2.x: Under <code>remotePackage/archives/*&#47;url</code>.<br/>
	 *
	 * @param listUrls URLs will update into xml document.
	 */
	void updateArchivesUrl(List<String> listUrls);

	/**
	 * Write XML text to out.
	 */
	void write(OutputStream out) throws IOException;
}

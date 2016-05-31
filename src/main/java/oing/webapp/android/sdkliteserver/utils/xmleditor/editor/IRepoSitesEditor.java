package oing.webapp.android.sdkliteserver.utils.xmleditor.editor;

import oing.webapp.android.sdkliteserver.utils.xmleditor.AddonSite;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IRepoSitesEditor {
	/**
	 * Extract all {@link AddonSite}s from given xml document.
	 */
	List<AddonSite> extractAll();

	/**
	 * Update URLs into xml document.
	 *
	 * @param listAddonSite URLs will update into xml document.
	 */
	void rebuild(List<AddonSite> listAddonSite);

	/**
	 * Save XML file to local storage.
	 */
	void save() throws IOException;

	/**
	 * Save XML file to another destination.
	 *
	 * @param targetFile Destination file.
	 */
	void save(File targetFile) throws IOException;
}

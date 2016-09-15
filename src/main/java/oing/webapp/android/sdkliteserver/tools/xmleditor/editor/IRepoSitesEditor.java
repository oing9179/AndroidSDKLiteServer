package oing.webapp.android.sdkliteserver.tools.xmleditor.editor;

import oing.webapp.android.sdkliteserver.tools.xmleditor.AddonSite;

import java.io.IOException;
import java.io.OutputStream;
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
	 * Save XML file to another destination.
	 */
	void write(OutputStream out) throws IOException;
}

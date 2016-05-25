package oing.webapp.android.sdkliteserver.service;

import jodd.http.ProxyInfo;

import java.util.EventListener;

public interface AutomaticAdditionEventListener extends EventListener {
	/**
	 * The event listener let controller knows how many jobs completed in this service.
	 *
	 * @param progress The percent of progress from 0(%0) to 1(%100),
	 *                 negative if error occurs.
	 * @param message  The message that you want service knows.
	 * @see XmlRepositoryEditorService#automaticAddition(String, boolean, ProxyInfo, AutomaticAdditionEventListener)
	 */
	void onPublish(float progress, String message);
}

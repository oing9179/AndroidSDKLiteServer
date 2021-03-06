package oing.webapp.android.sdkliteserver.misc;

import oing.webapp.android.sdkliteserver.io.LimitedBandwidthInputStream;

public class ApplicationConstants {
	/**
	 * Where configuration file stores, in relative path to web application root.
	 */
	public static final String FILE_PATH_CONFIG = "/WEB-INF/config.xml";
	/**
	 * Session attribute for {@link oing.webapp.android.sdkliteserver.controller.interceptor.AdminAuthHandlerInterceptorAdapter}
	 * In Unix-Timestamp.
	 */
	public static final String KEY_SESSION_EXPIRES_ON = "session_expires_on";
	/**
	 * ApplicationContext attribute key for "repository_xml_id".
	 * Actual data type: Long
	 */
	public static final String KEY_REPOSITORY_XML_ID = "repository_xml_id";
	/**
	 * ApplicationContext attribute key for "repository_zip_id".
	 * Actual data type: Long
	 */
	public static final String KEY_REPOSITORY_ZIP_ID = "repository_zip_id";
	/**
	 * ApplicationContext attribute key for "upstream_speed_limit".
	 * Upstream speed limit in Bytes/s.
	 * Actual data type: Long
	 */
	public static final String KEY_UPSTREAM_SPEED_LIMIT = "upstream_speed_limit";

	/**
	 * Session attribute key for {@link LimitedBandwidthInputStream}
	 * Actual data type: Long
	 */
	public static final String KEY_BANDWIDTH_LIMIT_REMAINING_BYTES = "bandwidth_limit_remaining_bytes";
	/**
	 * Session attribute key for {@link LimitedBandwidthInputStream}
	 * Actual data type: Long
	 *
	 * @see System#currentTimeMillis()
	 */
	public static final String KEY_BANDWIDTH_LIMIT_LAST_RESET_TIME = "bandwidth_limit_last_reset_time";
}

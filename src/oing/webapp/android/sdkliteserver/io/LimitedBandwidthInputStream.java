package oing.webapp.android.sdkliteserver.io;

import oing.webapp.android.sdkliteserver.controller.ApplicationConstants;
import oing.webapp.android.sdkliteserver.controller.interceptor.HttpSessionListenerImpl;
import org.springframework.web.HttpSessionRequiredException;

import javax.servlet.http.HttpSession;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream with limited read-bandwidth.<br/>
 * Actually, it is not that accurate,
 * for instance: say bandwidth limited at 5MiB/s, we got download speed about 5.75MiB/s.<br/>
 * I don't want lower my webapp performance to get a accurate bandwidth limitation,
 * So we don't uses things like "SessionLocal-ish (think about ThreadLocal)".
 */
public class LimitedBandwidthInputStream extends FilterInputStream {
	private final String SESSION_ID;
	private final long BANDWIDTH;

	/**
	 * Creates a <code>FilterInputStream</code>
	 * by assigning the  argument <code>in</code>
	 * to the field <code>this.in</code> so as
	 * to remember it for later use.
	 *
	 * @param in        the underlying input stream, or <code>null</code> if
	 *                  this instance is to be created without an underlying stream.
	 * @param sessionId HttpSession ID.
	 * @param bandwidth Bandwidth limit in Bytes/s.
	 * @throws HttpSessionRequiredException Session ID not found.
	 */
	public LimitedBandwidthInputStream(InputStream in, String sessionId, long bandwidth) throws HttpSessionRequiredException {
		super(in);
		this.SESSION_ID = sessionId;
		this.BANDWIDTH = bandwidth;

		// Init Last reset time and Bandwidth.
		{
			HttpSession lSession = getHttpSessionOrThrow();
			lSession.setAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_LAST_RESET_TIME, System.currentTimeMillis());
			lSession.setAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_REMAINING_BYTES, BANDWIDTH);
		}
	}

	@Override
	public int read() throws IOException {
		try {
			int len = (int) reduceRemainingBytes(1);
			if (len == 0) {
				Thread.sleep(50);
				resetRemainingBytes();
				return 0;
			}
			return len;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		try {
			len = (int) reduceRemainingBytes(len);
			if (len == 0) {
				Thread.sleep(50);
				resetRemainingBytes();
				return len;
			}
			return super.read(b, off, len);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	private long getRemainingBytes() throws HttpSessionRequiredException {
		HttpSession lSession = getHttpSessionOrThrow();
		Long ljRemainingBytes = (Long) lSession.getAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_REMAINING_BYTES);
		if (ljRemainingBytes == null) {
			ljRemainingBytes = BANDWIDTH;
			lSession.setAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_REMAINING_BYTES, ljRemainingBytes);
		}
		return ljRemainingBytes;
	}

	/**
	 * Reduce remaining bytes length
	 *
	 * @param size Length of bytes try to read from InputStream.
	 * @return The actual length of bytes can read from InputStream.
	 * @throws HttpSessionRequiredException Session ID not found
	 */
	private long reduceRemainingBytes(long size) throws HttpSessionRequiredException {
		HttpSession lSession = getHttpSessionOrThrow();
		Long ljRemainingBytes = getRemainingBytes();

		long temp = ljRemainingBytes - size;
		if (temp < 0) {
			size = ljRemainingBytes.intValue();
			ljRemainingBytes = 0L;
		} else {
			ljRemainingBytes = temp;
		}

		lSession.setAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_REMAINING_BYTES, ljRemainingBytes);
		return size;
	}

	private boolean resetRemainingBytes() throws HttpSessionRequiredException {
		HttpSession lSession = getHttpSessionOrThrow();
		Long ljLastResetTimeMillis = (Long) lSession.getAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_LAST_RESET_TIME);
		long ljCurrentTimeMillis = System.currentTimeMillis();
		if (ljCurrentTimeMillis - ljLastResetTimeMillis < 1000) {
			return false;// Limit bandwidth per second.
		}

		lSession.setAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_LAST_RESET_TIME, ljCurrentTimeMillis);
		lSession.setAttribute(ApplicationConstants.KEY_BANDWIDTH_LIMIT_REMAINING_BYTES, BANDWIDTH);
		return true;
	}

	private HttpSession getHttpSessionOrThrow() throws HttpSessionRequiredException {
		HttpSession lSession = HttpSessionListenerImpl.getSessionById(SESSION_ID);
		if (lSession == null) throw new HttpSessionRequiredException("Session not found: " + SESSION_ID);
		return lSession;
	}
}

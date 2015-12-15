package oing.webapp.android.sdkliteserver.controller.interceptor;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;

public class HttpSessionListenerImpl implements HttpSessionListener {
	private static HashMap<String, HttpSession> mMapSessions = new HashMap<>();

	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		HttpSession session = httpSessionEvent.getSession();
		mMapSessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		mMapSessions.remove(httpSessionEvent.getSession().getId());
	}

	public static HttpSession getSessionById(String id) {
		return mMapSessions.get(id);
	}
}

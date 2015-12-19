package oing.webapp.android.sdkliteserver.controller.interceptor;

import oing.webapp.android.sdkliteserver.misc.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Calendar;

/**
 * Interceptor that intercepts some want to access admin area.
 */
public class AdminAuthHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
	private static final Logger mLogger = LoggerFactory.getLogger(AdminAuthHandlerInterceptorAdapter.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		final long ljCurrentTimeMillis = System.currentTimeMillis();
		HttpSession session = request.getSession();
		Long lLongExpiresOn = (Long) session.getAttribute(ApplicationConstants.KEY_SESSION_EXPIRES_ON);

		if (lLongExpiresOn != null && lLongExpiresOn > ljCurrentTimeMillis) {
			Calendar lCalendar = Calendar.getInstance();
			lCalendar.setTimeInMillis(ljCurrentTimeMillis);
			// Add 3 minutes from now, so session will expired after 3 minutes.
			lCalendar.add(Calendar.MINUTE, 3);
			session.setAttribute(ApplicationConstants.KEY_SESSION_EXPIRES_ON, lCalendar.getTimeInMillis());
			return true;
		}
		mLogger.info("Someone try to access admin area." +
				"IP: " + request.getRemoteAddr() + ":" + request.getRemotePort());
		response.sendRedirect("/admin/login.do");
		return false;
	}
}

package oing.webapp.android.sdkliteserver.controller;

import oing.webapp.android.sdkliteserver.misc.ApplicationConstants;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {
	private static final Logger mLogger = LoggerFactory.getLogger(AdminAuthController.class);

	@RequestMapping
	public String _index() {
		return "redirect:/admin/dashboard/";
	}

	@RequestMapping(value = "/login.do", method = RequestMethod.GET)
	public String login_view() {
		return "admin/login";
	}

	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public String login(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response,
	                    @RequestParam("passwordSHA256") String passwordSHA256) {
		// 因为这个登录逻辑很简单, 于是直接写在Controller里 而不是写在专门的Service里.
		try {
			final String lStrPasswordSha256 = ConfigurationUtil.get("admin.password_sha256").trim();
			if (passwordSHA256.equalsIgnoreCase(lStrPasswordSha256)) {
				Calendar lCalendar = Calendar.getInstance();
				lCalendar.setTimeInMillis(System.currentTimeMillis());
				// Add 3 minutes from now, so session will expired after 3 minutes.
				lCalendar.add(Calendar.MINUTE, 3);
				request.getSession().setAttribute(ApplicationConstants.KEY_SESSION_EXPIRES_ON, lCalendar.getTimeInMillis());
				return "redirect:/admin/dashboard/";
			}
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			throw new PermissionDeniedDataAccessException("Wrong password", null);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			String lStrViewPath = login_view();
			modelMap.put("objException", e);
			return lStrViewPath;
		}
	}
}

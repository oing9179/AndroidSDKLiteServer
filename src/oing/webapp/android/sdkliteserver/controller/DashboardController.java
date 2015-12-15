package oing.webapp.android.sdkliteserver.controller;

import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/dashboard/")
public class DashboardController {
	private static final Logger mLogger = LoggerFactory.getLogger(DashboardController.class);
	@Autowired
	private XmlRepositoryListService xmlRepositoryListService;
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index_view(ModelMap modelMap) {
		try {
			modelMap.put("xmlRepositories", xmlRepositoryListService.getAll());
			modelMap.put("zipRepositories", zipRepositoryListService.getAll());
		} catch (Exception e) {
			mLogger.warn(e.toString(), e);
			modelMap.put("objException", e);
		}
		return "dashboard/index";
	}

	@RequestMapping(value = "/deploy.do", method = RequestMethod.GET)
	public String deploy_repository(ModelMap modelMap, HttpSession session,
	                                @RequestParam("upstreamSpeedLimit") Long upstreamSpeedLimit,
	                                @RequestParam("xmlRepositoryId") Long repoXmlId,
	                                @RequestParam("zipRepositoryId") Long repoZipId) {
		ServletContext servletContext = session.getServletContext();
		try {
			_index_view(modelMap);
			servletContext.setAttribute(ApplicationConstants.KEY_UPSTREAM_SPEED_LIMIT, upstreamSpeedLimit);
			servletContext.setAttribute(ApplicationConstants.KEY_REPOSITORY_XML_ID, repoXmlId);
			servletContext.setAttribute(ApplicationConstants.KEY_REPOSITORY_ZIP_ID, repoZipId);
		} catch (Exception e) {
			mLogger.warn(e.toString(), e);
			String lStrViewPath = _index_view(modelMap);
			modelMap.put("objException", e);
			return lStrViewPath;
		}
		return "redirect:/dashboard/";
	}
}

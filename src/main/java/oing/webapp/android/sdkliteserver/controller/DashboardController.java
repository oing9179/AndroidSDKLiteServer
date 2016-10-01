package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSONObject;
import oing.webapp.android.sdkliteserver.misc.ApplicationConstants;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryListService;
import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import oing.webapp.android.sdkliteserver.utils.ConfigurationUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

/**
 * This controller does not do the "actual deployment-things",
 * instead, this Controller just put 3 values to HttpSession Attribute,
 * then {@link DataRepositoryController} will help "Android SDK Manager" to fetch repositories.
 */
@Controller
@RequestMapping("/admin/dashboard/")
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
		return "admin/dashboard/index";
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
		return "redirect:/admin/dashboard/";
	}

	@RequestMapping(value = "/reload_configuration.do", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject reload_configuration(HttpServletRequest request) {
		ServletContext servletContext = request.getServletContext();
		JSONObject lJsonObjResponse = new JSONObject();
		try {
			ConfigurationUtil.load(new File(servletContext.getRealPath(ApplicationConstants.FILE_PATH_CONFIG)));
			lJsonObjResponse.put("success", true);
		} catch (DocumentException | IOException e) {
			lJsonObjResponse.put("success", false);
			lJsonObjResponse.put("errorMessage", e.toString());
		}
		return lJsonObjResponse;
	}
}

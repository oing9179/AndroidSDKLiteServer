package oing.webapp.android.sdkliteserver.controller;

import com.alibaba.fastjson.JSON;
import oing.webapp.android.sdkliteserver.model.RepoXml;
import oing.webapp.android.sdkliteserver.service.XmlRepositoryService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/repository/xml/")
public class XmlRepositoryController {
	private static Logger mLogger = LoggerFactory.getLogger(XmlRepositoryController.class);
	@Autowired
	private XmlRepositoryService xmlRepositoryService;

	/**
	 * Show all xml repository
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap) {
		List<RepoXml> lListRepoXml = xmlRepositoryService.getAll();

		modelMap.put("data", JSON.toJSONString(lListRepoXml));
		return "repository/xml/index";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.GET)
	public String creation_view(ModelMap modelMap) {
		modelMap.put("xmlRepositories", xmlRepositoryService.getAll());
		return "repository/xml/creation";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.POST)
	public String creation(ModelMap modelMap,
						   @RequestParam String repositoryName, @RequestParam(required = false) Long createFrom) {
		/**
		 * Validate:
		 * 1. At least 6 characters.
		 * 2. At most 32 characters.
		 * 3. Alphabets, numbers and underscores are allowed.
		 */
		try {
			Validate.matchesPattern(repositoryName, "^\\w{6,32}$",
					"At least 6 chars, at most 32 chars, alphabets numbers and underscores are allowed.");
		} catch (IllegalArgumentException e) {
			mLogger.info("New repository name does not match the regex pattern \"^\\w{6,32}$\"", e);
			modelMap.put("errorMessage", e);
		}
		if (modelMap.containsKey("errorMessage")) return "/repository/xml/creation";
		// Validation complete, create xml repository.
		try {
			xmlRepositoryService.repositoryCreate_step1(repositoryName, createFrom);
			xmlRepositoryService.repositoryCreate_step2(repositoryName, createFrom);
		} catch (Exception e) {
			mLogger.error("Failed to process service: XmlRepositoryService.repositoryCreate.", e);
			modelMap.put("errorMessage", e);
		}
		if (modelMap.containsKey("errorMessage")) return "/repository/xml/creation";
		return "redirect:/repository/xml/";
	}
}

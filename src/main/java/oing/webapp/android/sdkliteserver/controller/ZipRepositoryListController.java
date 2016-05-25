package oing.webapp.android.sdkliteserver.controller;

import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/repository/zip/")
public class ZipRepositoryListController {
	private static Logger mLogger = LoggerFactory.getLogger(ZipRepositoryListController.class);
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap) {
		modelMap.put("zipRepositories", zipRepositoryListService.getAll());
		return "admin/repository/zip/index";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.GET)
	public String creation_view() {
		return "admin/repository/zip/creation";
	}

	@RequestMapping(value = "/creation.do", method = RequestMethod.POST)
	public String creation(ModelMap modelMap, @RequestParam("name") String name) {
		try {
			Validate.matchesPattern(name, "^\\w{6,32}$",
					"Validation failed: repository name: At least 6 chars, at most 32 chars, alphabets numbers and underscores are allowed.");
			zipRepositoryListService.create(name);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			modelMap.put("objException", e);
			return creation_view();
		}
		return "redirect:/admin/repository/zip/";
	}

	@RequestMapping(value = "/deletion.do", method = RequestMethod.GET)
	public String deletion_view(ModelMap modelMap, @RequestParam("id") Long id) {
		modelMap.put("zipRepository", zipRepositoryListService.getById(id));
		return "admin/repository/zip/deletion";
	}

	@RequestMapping(value = "/deletion.do", method = RequestMethod.POST)
	public String deletion(ModelMap modelMap, @RequestParam("id") Long id, @RequestParam("name") String name) {
		try {
			zipRepositoryListService.delete(id, name);
		} catch (Exception e) {
			mLogger.info(e.toString(), e);
			String lStrUrl = deletion_view(modelMap, id);
			modelMap.put("objException", e);
			return lStrUrl;
		}
		return "redirect:/admin/repository/zip/";
	}
}

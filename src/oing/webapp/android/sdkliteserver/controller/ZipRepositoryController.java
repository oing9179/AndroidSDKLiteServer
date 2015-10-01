package oing.webapp.android.sdkliteserver.controller;

import oing.webapp.android.sdkliteserver.service.ZipRepositoryListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/repository/zip/")
public class ZipRepositoryController {
	@Autowired
	private ZipRepositoryListService zipRepositoryListService;

	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap) {
		modelMap.put("zipRepositories", zipRepositoryListService.getAll());
		return "repository/zip/index";
	}
}

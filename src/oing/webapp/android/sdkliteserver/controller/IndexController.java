package oing.webapp.android.sdkliteserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
public class IndexController {
	@RequestMapping(method = RequestMethod.GET)
	public String _index(ModelMap modelMap, HttpServletRequest request) {
		// Now focus on function of XML Repository
		// return "redirect:/repository/zip/";
		return "index";
	}
}

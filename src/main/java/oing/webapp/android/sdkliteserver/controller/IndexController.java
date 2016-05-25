package oing.webapp.android.sdkliteserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Welcome newbee, you find the index controller of this web app,
 * Go check out {@link DashboardController} to get started.
 */
@Controller
@RequestMapping("/")
public class IndexController {
	@RequestMapping(method = RequestMethod.GET)
	public String _index() {
		return "index";
	}
}

package oing.webapp.android.sdkliteserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/repository/zip/")
public class ZipRepositoryController {
    @RequestMapping(method = RequestMethod.GET)
    public String _index(HttpServletRequest request) {
        return "repository/zip/index";
    }
}

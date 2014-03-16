package com.startupweekend.ubc.sldshw.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
	@RequestMapping("/")
	public String home() {
		return "home";
	}
    
	@RequestMapping("/client/")
	public String client() {
		return "client";
	}

    @RequestMapping("/host/")
    public String host() {
        return "host";
    }
    
}

package com.startupweekend.ubc.sldshw.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TinyUrlController {
	@RequestMapping("/p/{tinyUrl}")
	public String tinyUrlLookup(@PathVariable("tinyUrl") String tinyUrl) {
		return "redirect:/mobile";
	}
}

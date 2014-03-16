package com.startupweekend.ubc.sldshw.datamodel;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PageAnnotation {
	private String pageId;
	private Boolean heart;
	private Boolean question;
	private List<String> comments = new ArrayList<String>();
	private String vote;
}

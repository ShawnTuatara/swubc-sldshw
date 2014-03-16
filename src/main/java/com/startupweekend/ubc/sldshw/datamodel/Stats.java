package com.startupweekend.ubc.sldshw.datamodel;

import lombok.Data;

@Data
public class Stats {
	private String pageId;
	private int heartCount;
	private int questionCount;
	private String[] comments;
}

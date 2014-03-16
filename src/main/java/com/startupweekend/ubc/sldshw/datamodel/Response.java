package com.startupweekend.ubc.sldshw.datamodel;

import java.util.List;

import lombok.Data;

@Data
public class Response {

	private List<PageAnnotation> annotations;
	private Stats stats;
	
	public Response(List<PageAnnotation> annotations, Stats stats) {
		this.annotations = annotations;
		this.stats = stats;
	}
}

package com.startupweekend.ubc.sldshw.datamodel;

import java.util.List;

import lombok.Data;

@Data
public class Response {

	private List<PageAnnotation> annotations;
	private List<Stats> stats;
	
	public Response(List<PageAnnotation> annotations, List<Stats> stats) {
		this.annotations = annotations;
		this.stats = stats;
	}
}

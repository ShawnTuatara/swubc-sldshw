package com.startupweekend.ubc.sldshw.datamodel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Stats {
	
	@Getter
	private final String pageId;
	
	private int heartCount;
	private int questionCount;
	
	public Stats(String pageId) {
		this.pageId = pageId;
	}
	
	public void collect(PageAnnotation annotation) {
		if (!annotation.getPageId().equals(pageId)) {
			throw new IllegalArgumentException("wrong page id");
		}
		
		if (annotation.getHeart() != null && annotation.getHeart()) {
			this.heartCount++;
		}
		
		if (annotation.getQuestion() != null && annotation.getQuestion()) {
			this.questionCount++;
		}
	}
}

package com.startupweekend.ubc.sldshw.datamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Collects stats on page annotations. Thread-safe.
 * @author shu
 *
 */
@ToString
@EqualsAndHashCode
public class Stats implements Comparable<Stats> {
	
	@Getter
	private final String pageId;
	
	@Getter
	private AtomicInteger heartCount = new AtomicInteger(0);
	@Getter
	private AtomicInteger questionCount = new AtomicInteger(0);
	
	private Object lock = new Object();
	@Getter
	private Map<String, AtomicInteger> voteCounts = new HashMap<String, AtomicInteger>();
	
	public Stats(String pageId) {
		this.pageId = pageId;
	}
	
	public void collect(PageAnnotation annotation) {
		if (!annotation.getPageId().equals(pageId)) {
			throw new IllegalArgumentException("wrong page id");
		}
		
		if (annotation.getHeart() != null && annotation.getHeart()) {
			this.heartCount.incrementAndGet();
		}
		
		if (annotation.getQuestion() != null && annotation.getQuestion()) {
			this.questionCount.incrementAndGet();
		}
		
		if (annotation.getVote() != null && !annotation.getVote().isEmpty()) {
			if (!voteCounts.containsKey(annotation.getVote())) {
				synchronized(lock) {
					if (!voteCounts.containsKey(annotation.getVote())) {
						voteCounts.put(annotation.getVote(), new AtomicInteger(0));
					}
				}
			}
			voteCounts.get(annotation.getVote()).incrementAndGet();
		}
	}

	@Override
	public int compareTo(Stats o) {
		int engagement = heartCount.get() + questionCount.get();
		int otherEngagement = o.heartCount.get() + o.questionCount.get();
		if (engagement == otherEngagement) {
			return 0;
		}
		if (engagement > otherEngagement) {
			return 1;
		}
		return -1;
	}
}

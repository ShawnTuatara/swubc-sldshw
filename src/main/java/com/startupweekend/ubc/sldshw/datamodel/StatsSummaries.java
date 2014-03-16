package com.startupweekend.ubc.sldshw.datamodel;

import java.util.List;

import lombok.Data;

@Data
public class StatsSummaries {

	private List<Stats> presentationStats;
	private Stats userStats;
	
	public StatsSummaries(List<Stats> presentationStats, Stats userStats) {
		this.userStats = userStats;
		this.presentationStats = presentationStats;
	}
}

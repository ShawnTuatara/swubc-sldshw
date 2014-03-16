package com.startupweekend.ubc.sldshw.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import com.startupweekend.ubc.sldshw.SldShwUserDetailsService;
import com.startupweekend.ubc.sldshw.datamodel.PageAnnotation;
import com.startupweekend.ubc.sldshw.datamodel.SlideMeta;
import com.startupweekend.ubc.sldshw.datamodel.Stats;
import com.startupweekend.ubc.sldshw.datamodel.StatsSummaries;

@Controller
public class MessagingController {
	@Autowired
	private SldShwUserDetailsService userDetails = null;
	
	private final Object lock = new Object();
	private final Object presentationPageLock = new Object();
	private final Object presentationPageStatsLock = new Object();
	private final Object presentationPageStatsLock2 = new Object();
	
	/** Maps presentation -> user -> pageId -> annotations. */
	private Map<String, Map<String, Map<String, PageAnnotation>>> data
		= new HashMap<String, Map<String, Map<String, PageAnnotation>>>();
	/** email, sessionId **/
	private Map<String, String> userSessionMappingHack = new HashMap<String, String>();
	private Map<String, SlideMeta> presentationPage = new HashMap<String, SlideMeta>();
	
	/** Maps presentation -> pageId -> summary stats */
	private Map<String, Map<String, Stats>> presentationPageStats = new HashMap<String, Map<String, Stats>>();
	
	@MessageMapping("/presentation/{id}/page")
	public SlideMeta updatePage(
			@DestinationVariable("id") String presentationId,
			SlideMeta slide) {
		System.out.println("updatePageId - presentationId: " + presentationId + ", slide: " + slide);
		synchronized(presentationPageLock) {
			presentationPage.put(presentationId, slide);
		}
		return slide;
	}

	@SubscribeMapping("/topic/presentation/{id}/page")
	public SlideMeta subscribeToUpdatePage(@DestinationVariable("id") String presentationId) {
		System.out.println("subscribeToUpdatePage - presentationId: " + presentationId);
		return presentationPage.get(presentationId);
	}
	
	@SubscribeMapping("/topic/presentation/{id}/page/{pageId}/user")
	public PageAnnotation subscribeToPage(
			@DestinationVariable("id") String presentationId,
			@DestinationVariable("pageId") String pageId,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId) {
		System.out.println("subscribeToUpdatePage - presentationId: "
				+ presentationId + ", pageId: " + pageId);
		Map<String, Map<String, PageAnnotation>> presentationData = data
				.get(presentationId);
		PageAnnotation pageAnnotation = null;
		if (presentationData != null) {
			Map<String, PageAnnotation> existingAnnotations = presentationData
					.get(userId);
			if (existingAnnotations != null) {
				pageAnnotation = existingAnnotations.get(pageId);
			}
		}
		return pageAnnotation;
	}
	
	@SubscribeMapping("/topic/presentation/{id}/page/{pageId}")
	public Stats subscribeToPageUpdates(
			@DestinationVariable("id") String presentationId,
			@DestinationVariable("pageId") String pageId) {
		System.out.println("subscribe to page updates: " + presentationId + ", " + pageId);
		Map<String, Stats> pageStats = presentationPageStats.get(presentationId);
		if (pageStats == null) {
			return new Stats(pageId);
		}
		Stats stats = pageStats.get(pageId);
		if (stats == null) {
			return new Stats(pageId);
		}
		return stats;
	}
	
	@MessageMapping("/presentation/{id}/page/{pageId}")
	public Stats annotatePage(
			@DestinationVariable("id") String presentationId,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId,
			PageAnnotation annotation) {
		System.out.println("annotatePage - presentationId: " + presentationId + ", userId: " + userId + ", annotation: " + annotation);
		
		/* update stats tracking for this page */
		if (!presentationPageStats.containsKey(presentationId)) {
			synchronized(presentationPageStatsLock) {
				if (!presentationPageStats.containsKey(presentationId)) {
					presentationPageStats.put(presentationId, new HashMap<String, Stats>());
				}
			}
		}
		Map<String, Stats> pageStats = presentationPageStats.get(presentationId);
		
		if (!pageStats.containsKey(annotation.getPageId())) {
			synchronized(presentationPageStatsLock2) {
				if (!pageStats.containsKey(annotation.getPageId())) {
					pageStats.put(annotation.getPageId(), new Stats(annotation.getPageId()));
				}
			}
		}
		Stats summary = pageStats.get(annotation.getPageId());
		summary.collect(annotation);
		
		/* update fine grained data */
		// get the per presentation data set
		if (!data.containsKey(presentationId)) {
			synchronized(lock) {
				if (!data.containsKey(presentationId)) {
					data.put(presentationId, new HashMap<String, Map<String, PageAnnotation>>());
				}
			}
		}
		
		Map<String, Map<String, PageAnnotation>>presentationData = data.get(presentationId);
		
		// get the per user data set
		Map<String, PageAnnotation> existingAnnotations;
		if (!presentationData.containsKey(userId)) {
			existingAnnotations = new HashMap<String, PageAnnotation>();
			presentationData.put(userId, existingAnnotations);
		}
		else {
			existingAnnotations = presentationData.get(userId);
		}
		
		// look up existing annotation and 'update' it with new info only
		if (!existingAnnotations.containsKey(annotation.getPageId())) {
			existingAnnotations.put(annotation.getPageId(), annotation);
			return summary;
		}
		
		PageAnnotation existingAnnotation = existingAnnotations.get(annotation.getPageId());
		if (annotation.getHeart() != null) {
			existingAnnotation.setHeart(annotation.getHeart());
		}
		if (annotation.getQuestion() != null) {
			existingAnnotation.setQuestion(annotation.getQuestion());
		}
		if (annotation.getComments() != null) {
			existingAnnotation.getComments().addAll(annotation.getComments());
		}
		if (annotation.getVote() != null && !annotation.getVote().isEmpty()) {
			existingAnnotation.setVote(annotation.getVote());
		}
		
		System.out.println("updated annotations: " + existingAnnotation);
		return summary;
	}
	
	/*
	 * Response with 
	 * - a list of stats ordered by heart count + question count, limit 5.
	 * - a single Stats with heart count, question count, comment count of the user
	 */
	@MessageMapping("/presentation/{id}/summary*")
	//@SendToUser
	public StatsSummaries getSummary(
			@DestinationVariable("id") String presentationId,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId,
			String email) {
		System.out.println("summary - presentationId: " + presentationId + ", userId: " + userId);

		Map<String, Map<String, PageAnnotation>> presentationData = data.get(presentationId);
		if (presentationData == null) {
			return new StatsSummaries(null, null);
		}

		if (userSessionMappingHack.containsKey(email)) {
			userId = userSessionMappingHack.get(email);
		}
		
		List<Stats> stats = null;
		Map<String, Stats> pageStats = presentationPageStats.get(presentationId);
		if (pageStats != null) {
			stats = new ArrayList<Stats>(pageStats.size());
			stats.addAll(pageStats.values());

			Collections.sort(stats);
			if (stats.size() > 5) {
				stats = stats.subList(0, 5);
			}
		}
		
		Stats userStats = new Stats("special");
		Map<String, PageAnnotation> savedAnnotations = presentationData.get(userId);
		if (savedAnnotations != null) {
			for (PageAnnotation annotation : savedAnnotations.values()) {
				userStats.collect(annotation);
			}
		}
		
		StatsSummaries response = new StatsSummaries(stats, userStats);
		System.out.println("result: " + response);
		return response;
	}
	
	@MessageMapping("/allData")
	public String getSummary() {
		return data.toString() + "\n" + userSessionMappingHack.toString();
	}
	
	@MessageMapping("/register")
	public void register(
			String email,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId) {
		UserDetails userDetail = null;
		try {
			userDetail = userDetails.loadUserByUsername(email);
		} catch (UsernameNotFoundException e) {
			// will take action after
		}
		if(userDetail == null) {
			userDetails.addUsername(email);
		}
		userSessionMappingHack.put(email, userId);
	}
	
	@MessageMapping("/presentation/{id}/relay")
	public Object relay(@DestinationVariable("id") String presentationId,
			@Payload Map message) {
		return message;
	}
}

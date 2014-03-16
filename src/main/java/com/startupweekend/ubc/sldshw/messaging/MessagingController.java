package com.startupweekend.ubc.sldshw.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.startupweekend.ubc.sldshw.datamodel.PageAnnotation;
import com.startupweekend.ubc.sldshw.datamodel.Response;
import com.startupweekend.ubc.sldshw.datamodel.Stats;

@Controller
public class MessagingController {
	
	private final Object lock = new Object();
	
	/** Maps presentation -> user -> pageId -> annotations. */
	private Map<String, Map<String, Map<String, PageAnnotation>>> data
		= new HashMap<String, Map<String, Map<String, PageAnnotation>>>();
	private Map<String, String> userSessionMappingHack = new HashMap<String, String>();

	@MessageMapping("/presentation/{id}/page")
	public String updatePageId(
			@DestinationVariable("id") String presentationId,
			String pageId) {
		System.out.println("updatePageId - presentationId: " + presentationId + ", pageId: " + pageId);
		return pageId;
	}
	
	@MessageMapping("/presentation/{id}")
	public void annotatePage(
			@DestinationVariable("id") String presentationId,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId,
			PageAnnotation annotation) {
		System.out.println("annotatePage - presentationId: " + presentationId + ", userId: " + userId + ", annotation: " + annotation);
		
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
			return;
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
		System.out.println("updated annotations: " + existingAnnotation);
	}
	
	@MessageMapping("/presentation/{id}/summary")
	//@SendToUser
	public Response getSummary(
			@DestinationVariable("id") String presentationId,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId,
			String email) {
		System.out.println("summary - presentationId: " + presentationId + ", userId: " + userId);
		
		if (userSessionMappingHack.containsKey(email)) {
			userId = userSessionMappingHack.get(email);
		}

		List<PageAnnotation> userAnnotations = new ArrayList<PageAnnotation>();
		Map<String, Stats> presentationStats = new HashMap<String, Stats>();
		
		Map<String, Map<String, PageAnnotation>> presentationData = data.get(presentationId);
		if (presentationData == null) {
			return null;
		}
		
		Map<String, PageAnnotation> savedAnnotations = presentationData.get(userId);
		if (savedAnnotations != null) {
			userAnnotations.addAll(savedAnnotations.values());
		}
		
		for (Map<String, PageAnnotation> annotationsForUser : presentationData.values()) {
			for (PageAnnotation annotation: annotationsForUser.values()) {
				if (!presentationStats.containsKey(annotation.getPageId())) {
					presentationStats.put(annotation.getPageId(), new Stats(annotation.getPageId()));
				}
				presentationStats.get(annotation.getPageId()).collect(annotation);
			}
		}
		
		List<Stats> stats = new ArrayList<Stats>(presentationStats.size());
		stats.addAll(presentationStats.values());
		Response response = new Response(userAnnotations, stats);
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
		userSessionMappingHack.put(email, userId);
	}
}

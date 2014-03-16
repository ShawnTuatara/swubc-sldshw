package com.startupweekend.ubc.sldshw.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.startupweekend.ubc.sldshw.datamodel.PageAnnotation;
import com.startupweekend.ubc.sldshw.datamodel.Pair;
import com.startupweekend.ubc.sldshw.datamodel.Response;

@Controller
@Slf4j
public class MessagingController {
	
	/** Maps presentation/user -> pageId -> annotations. */
	private Map<Pair<String, String>, Map<String, PageAnnotation>> data
		= new HashMap<Pair<String, String>, Map<String, PageAnnotation>>();

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
		Pair<String, String> id = new Pair<String, String>(presentationId, userId);
		Map<String, PageAnnotation> existingAnnotations;
		if (!data.containsKey(id)) {
			existingAnnotations = new HashMap<String, PageAnnotation>();
			data.put(id, existingAnnotations);
		}
		else {
			existingAnnotations = data.get(id);
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
	public String getSummary(
			@DestinationVariable("id") String presentationId,
			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId) {
		System.out.println("summary - presentationId: " + presentationId + ", userId: " + userId);

		List<PageAnnotation> annotations = new ArrayList<PageAnnotation>();
		
		Pair<String, String> id = new Pair<String, String>(presentationId, userId);
		Map<String, PageAnnotation> savedAnnotations = data.get(id);
		if (savedAnnotations != null) {
			annotations.addAll(savedAnnotations.values());
		}
		
		Response response = new Response(annotations, null);
		System.out.println("result: " + response);
		return response.toString();
	}
	
}

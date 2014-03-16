package com.startupweekend.ubc.sldshw.messaging;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.startupweekend.ubc.sldshw.datamodel.PageAnnotation;
import com.startupweekend.ubc.sldshw.datamodel.Pair;

@Controller
@Slf4j
public class MessagingController {
	
	/** Maps presentation/user -> pageId -> annotations. */
	private Map<Pair<String, String>, Map<String, PageAnnotation>> data
		= new HashMap<Pair<String, String>, Map<String, PageAnnotation>>();
	
	@MessageMapping("/comment")
	@SendTo("/topic/comments")
	public Comment recieveComment(Comment comment) {
		log.debug("Comment received: {}", comment);
		System.out.println("Comment received: " + comment);
		return comment;
	}
	
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
	
//	@MessageMapping("/presentation/{id}/page")
//	public Response getSummary(
//			@DestinationVariable("id") String presentationId,
//			@Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String userId) {
//		
//	}
	
}

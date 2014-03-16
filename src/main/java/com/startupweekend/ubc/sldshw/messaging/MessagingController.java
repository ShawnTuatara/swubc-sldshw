package com.startupweekend.ubc.sldshw.messaging;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class MessagingController {
	
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
		System.out.println("presentationId: " + presentationId + ", pageId: " + pageId);
		return pageId;
	}
}

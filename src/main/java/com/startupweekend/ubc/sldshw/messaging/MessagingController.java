package com.startupweekend.ubc.sldshw.messaging;

import lombok.extern.slf4j.Slf4j;

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
		return comment;
	}
}

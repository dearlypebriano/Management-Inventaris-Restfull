package com.management.ManagementInventaris.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class WebSocketController {

    private final SimpMessagingTemplate template;

    public WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @RequestMapping("/send")
    public void sendMessage() {
        this.template.convertAndSend("/topic/employees", "New employee added!");
    }
}
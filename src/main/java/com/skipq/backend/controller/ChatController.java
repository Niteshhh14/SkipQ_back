package com.skipq.backend.controller;

import com.skipq.backend.dto.ChatAssistRequest;
import com.skipq.backend.dto.ChatAssistResponse;
import com.skipq.backend.service.ChatAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatAssistantService chatAssistantService;

    @PostMapping("/assist")
    public ResponseEntity<ChatAssistResponse> assist(@RequestBody ChatAssistRequest request) {
        return ResponseEntity.ok(chatAssistantService.assist(request));
    }
}

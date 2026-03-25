package com.skipq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatAssistResponse {
    private String reply;
    private String nextStep;
    private List<String> suggestedActions;
    private String source;
}

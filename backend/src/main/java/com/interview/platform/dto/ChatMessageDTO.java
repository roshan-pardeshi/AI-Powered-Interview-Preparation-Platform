package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private Long id;
    private Long interviewId;
    private String senderType;
    private String message;
    private Integer messageOrder;
    private LocalDateTime createdAt;
}

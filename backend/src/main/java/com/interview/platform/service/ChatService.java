package com.interview.platform.service;

import com.interview.platform.dto.ChatMessageDTO;
import com.interview.platform.dto.ChatMessageRequest;
import com.interview.platform.entity.ChatMessage;
import com.interview.platform.entity.Interview;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.ChatMessageRepository;
import com.interview.platform.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class ChatService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final InterviewRepository interviewRepository;
    private final GeminiService geminiService;
    
    @Transactional
    public ChatMessageDTO sendMessage(Long interviewId, ChatMessageRequest request) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        
        // Get last message order
        List<ChatMessage> messages = chatMessageRepository.findByInterviewIdOrderByCreatedAt(interviewId);
        int nextOrder = messages.isEmpty() ? 0 : messages.get(messages.size() - 1).getMessageOrder() + 1;
        
        // Save user message
        ChatMessage userMessage = ChatMessage.builder()
                .interview(interview)
                .senderType(ChatMessage.SenderType.USER)
                .message(request.getMessage())
                .messageOrder(nextOrder)
                .build();
        
        chatMessageRepository.save(userMessage);
        
        // Generate AI response
        String aiResponse = generateAIResponse(interview, request.getMessage(), messages);
        
        // Save AI message
        ChatMessage aiMessage = ChatMessage.builder()
                .interview(interview)
                .senderType(ChatMessage.SenderType.AI)
                .message(aiResponse)
                .messageOrder(nextOrder + 1)
                .build();
        
        chatMessageRepository.save(aiMessage);
        
        return mapToDTO(aiMessage);
    }
    
    public List<ChatMessageDTO> getInterviewChat(Long interviewId) {
        return chatMessageRepository.findByInterviewIdOrderByCreatedAt(interviewId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private String generateAIResponse(Interview interview, String userMessage, List<ChatMessage> previousMessages) {
        StringBuilder context = new StringBuilder();
        context.append("Company: ").append(interview.getCompany()).append("\n");
        context.append("Technology: ").append(interview.getTechnology()).append("\n");
        context.append("Interview Type: ").append(interview.getInterviewType()).append("\n");
        
        try {
            return geminiService.generateFollowUpQuestion(userMessage, context.toString());
        } catch (Exception e) {
            log.error("Error generating AI response", e);
            return "Thank you for your answer. Can you tell me more about your experience with " + 
                    interview.getTechnology() + "?";
        }
    }
    
    private ChatMessageDTO mapToDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .interviewId(message.getInterview().getId())
                .senderType(message.getSenderType().name())
                .message(message.getMessage())
                .messageOrder(message.getMessageOrder())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

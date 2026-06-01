package com.interview.platform.controller;

import com.interview.platform.dto.*;
import com.interview.platform.service.ChatService;
import com.interview.platform.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/interview")
@Tag(name = "Interview Management", description = "Interview and chat operations")
@RequiredArgsConstructor
public class InterviewController {
    
    private final InterviewService interviewService;
    private final ChatService chatService;
    
    @PostMapping("/start")
    @Operation(summary = "Start new interview")
    public ResponseEntity<ApiResponse> startInterview(
            @RequestBody InterviewRequest request,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        InterviewDTO interview = interviewService.startInterview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Interview started", interview));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get interview details")
    public ResponseEntity<ApiResponse> getInterview(@PathVariable Long id) {
        InterviewDTO interview = interviewService.getInterview(id);
        return ResponseEntity.ok(new ApiResponse(true, "Interview retrieved", interview));
    }
    
    @GetMapping("/my-interviews")
    @Operation(summary = "Get user's interviews")
    public ResponseEntity<ApiResponse> getUserInterviews(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        List<InterviewDTO> interviews = interviewService.getUserInterviews(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Interviews retrieved", interviews));
    }
    
    @PostMapping("/{interviewId}/end")
    @Operation(summary = "End interview with score")
    public ResponseEntity<ApiResponse> endInterview(
            @PathVariable Long interviewId,
            @RequestParam Integer score) {
        interviewService.endInterview(interviewId, score);
        return ResponseEntity.ok(new ApiResponse(true, "Interview ended", null));
    }
    
    @PostMapping("/{interviewId}/chat")
    @Operation(summary = "Send chat message")
    public ResponseEntity<ApiResponse> sendMessage(
            @PathVariable Long interviewId,
            @RequestBody ChatMessageRequest request) {
        ChatMessageDTO message = chatService.sendMessage(interviewId, request);
        return ResponseEntity.ok(new ApiResponse(true, "Message sent", message));
    }
    
    @GetMapping("/{interviewId}/chat")
    @Operation(summary = "Get interview chat history")
    public ResponseEntity<ApiResponse> getChat(@PathVariable Long interviewId) {
        List<ChatMessageDTO> messages = chatService.getInterviewChat(interviewId);
        return ResponseEntity.ok(new ApiResponse(true, "Chat retrieved", messages));
    }

    @GetMapping("/{interviewId}/questions")
    @Operation(summary = "Get interview questions")
    public ResponseEntity<ApiResponse> getInterviewQuestions(@PathVariable Long interviewId) {
        List<InterviewQuestionDTO> questions = interviewService.getInterviewQuestions(interviewId);
        return ResponseEntity.ok(new ApiResponse(true, "Questions retrieved", questions));
    }

    @GetMapping("/{interviewId}/answers")
    @Operation(summary = "Get interview answers")
    public ResponseEntity<ApiResponse> getInterviewAnswers(@PathVariable Long interviewId) {
        List<UserAnswerDTO> answers = interviewService.getInterviewAnswers(interviewId);
        return ResponseEntity.ok(new ApiResponse(true, "Answers retrieved", answers));
    }

    @PostMapping("/{interviewId}/questions/{questionId}/answer")
    @Operation(summary = "Submit an answer to an interview question")
    public ResponseEntity<ApiResponse> submitAnswer(
            @PathVariable Long interviewId,
            @PathVariable Long questionId,
            @RequestBody AnswerRequest request,
            Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        UserAnswerDTO answer = interviewService.saveUserAnswer(userId, interviewId, questionId, request.getAnswerText());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Answer submitted", answer));
    }
}

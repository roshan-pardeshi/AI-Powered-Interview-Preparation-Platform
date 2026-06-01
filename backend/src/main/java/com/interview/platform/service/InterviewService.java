package com.interview.platform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.InterviewDTO;
import com.interview.platform.dto.InterviewQuestionDTO;
import com.interview.platform.dto.InterviewRequest;
import com.interview.platform.dto.UserAnswerDTO;
import com.interview.platform.entity.*;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class InterviewService {
    
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AnalyticsRepository analyticsRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public InterviewDTO startInterview(Long userId, InterviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Interview interview = Interview.builder()
                .user(user)
                .company(request.getCompany())
                .technology(request.getTechnology())
                .difficulty(Interview.DifficultyLevel.valueOf(request.getDifficulty()))
                .interviewType(Interview.InterviewType.valueOf(request.getInterviewType()))
                .build();
        
        interview = interviewRepository.save(interview);
        
        // Generate questions from Gemini
        String questionsJson = geminiService.generateInterviewQuestions(
                request.getCompany(),
                request.getTechnology(),
                request.getDifficulty()
        );
        
        // Parse and save questions
        saveInterviewQuestions(interview, questionsJson);
        
        // Add initial greeting message
        ChatMessage greeting = ChatMessage.builder()
                .interview(interview)
                .senderType(ChatMessage.SenderType.AI)
                .message("Hello! I'm your interview assistant. Let's start with a few questions. Tell me about yourself.")
                .messageOrder(0)
                .build();
        
        chatMessageRepository.save(greeting);
        
        return mapToDTO(interview);
    }
    
    public InterviewDTO getInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        return mapToDTO(interview);
    }
    
    public List<InterviewDTO> getUserInterviews(Long userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<InterviewQuestionDTO> getInterviewQuestions(Long interviewId) {
        return questionRepository.findByInterviewIdOrderByQuestionNumber(interviewId)
                .stream()
                .map(this::mapQuestionToDTO)
                .collect(Collectors.toList());
    }
    
    public List<UserAnswerDTO> getInterviewAnswers(Long interviewId) {
        return answerRepository.findByInterviewId(interviewId)
                .stream()
                .map(this::mapAnswerToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void saveInterviewQuestions(Interview interview, String questionsJson) {
        try {
            List<Map<String, Object>> questions = objectMapper.readValue(questionsJson, 
                    new TypeReference<List<Map<String, Object>>>() {});
            
            for (int i = 0; i < questions.size(); i++) {
                Map<String, Object> q = questions.get(i);
                InterviewQuestion question = InterviewQuestion.builder()
                        .interview(interview)
                        .questionNumber(i + 1)
                        .questionText((String) q.get("text"))
                        .category(InterviewQuestion.QuestionCategory.valueOf((String) q.get("category")))
                        .build();
                
                questionRepository.save(question);
            }
        } catch (Exception e) {
            log.error("Error parsing interview questions", e);
        }
    }
    
    @Transactional
    public UserAnswerDTO saveUserAnswer(Long userId, Long interviewId, Long questionId, String answerText) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        
        if (!interview.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Interview not found for this user");
        }
        
        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        
        if (!question.getInterview().getId().equals(interviewId)) {
            throw new ResourceNotFoundException("Question does not belong to the interview");
        }
        
        // Evaluate answer using Gemini
        String evaluation = geminiService.evaluateAnswer(
                question.getQuestionText(),
                answerText,
                question.getCategory().name()
        );
        
        // Parse evaluation
        Map<String, Object> feedbackMap = parseGeminiResponse(evaluation);
        
        String feedbackJson;
        try {
            feedbackJson = objectMapper.writeValueAsString(feedbackMap);
        } catch (JsonProcessingException e) {
            log.error("Error serializing feedback", e);
            feedbackJson = "{}";
        }

        UserAnswer answer = UserAnswer.builder()
                .question(question)
                .interview(interview)
                .answerText(answerText)
                .feedback(feedbackJson)
                .confidenceScore(toInt(feedbackMap.getOrDefault("confidenceScore", 0)))
                .communicationScore(toInt(feedbackMap.getOrDefault("communicationScore", 0)))
                .technicalScore(toInt(feedbackMap.getOrDefault("technicalScore", 0)))
                .build();
        
        answer = answerRepository.save(answer);
        return mapAnswerToDTO(answer);
    }
    
    @Transactional
    public void endInterview(Long interviewId, Integer score) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        
        interview.setScore(score);
        interviewRepository.save(interview);
        
        // Add closing message
        ChatMessage closing = ChatMessage.builder()
                .interview(interview)
                .senderType(ChatMessage.SenderType.AI)
                .message("Thank you for the interview! Your score: " + score + "/100. Great job!")
                .messageOrder(100)
                .build();
        
        chatMessageRepository.save(closing);
        updateInterviewAnalytics(interview.getUser().getId());
    }
    
    private void updateInterviewAnalytics(Long userId) {
        var analyticsOpt = analyticsRepository.findByUserId(userId);
        if (analyticsOpt.isPresent()) {
            var analytics = analyticsOpt.get();
            List<Interview> interviews = interviewRepository.findByUserId(userId);
            int completedCount = (int) interviews.stream()
                    .filter(interview -> interview.getScore() != null)
                    .count();
            int averageScore = (int) Math.round(interviews.stream()
                    .filter(interview -> interview.getScore() != null)
                    .mapToInt(Interview::getScore)
                    .average()
                    .orElse(0));
            analytics.setTotalInterviews(completedCount);
            analytics.setAverageScore(averageScore);
            analytics.setLastInterviewDate(java.time.LocalDateTime.now());
            analyticsRepository.save(analytics);
        }
    }
    
    private Map<String, Object> parseGeminiResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
        }
        return result;
    }
    
    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private InterviewQuestionDTO mapQuestionToDTO(InterviewQuestion question) {
        return InterviewQuestionDTO.builder()
                .id(question.getId())
                .interviewId(question.getInterview().getId())
                .questionNumber(question.getQuestionNumber())
                .questionText(question.getQuestionText())
                .category(question.getCategory().name())
                .createdAt(question.getCreatedAt())
                .build();
    }
    
    private UserAnswerDTO mapAnswerToDTO(UserAnswer answer) {
        return UserAnswerDTO.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .interviewId(answer.getInterview().getId())
                .answerText(answer.getAnswerText())
                .feedback(answer.getFeedback())
                .confidenceScore(answer.getConfidenceScore())
                .communicationScore(answer.getCommunicationScore())
                .technicalScore(answer.getTechnicalScore())
                .createdAt(answer.getCreatedAt())
                .build();
    }
    
    private InterviewDTO mapToDTO(Interview interview) {
        return InterviewDTO.builder()
                .id(interview.getId())
                .userId(interview.getUser().getId())
                .company(interview.getCompany())
                .technology(interview.getTechnology())
                .difficulty(interview.getDifficulty().name())
                .interviewType(interview.getInterviewType().name())
                .score(interview.getScore())
                .feedback(interview.getFeedback())
                .durationSeconds(interview.getDurationSeconds())
                .createdAt(interview.getCreatedAt())
                .build();
    }
}

package com.interview.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.VoiceAnalysisDTO;
import com.interview.platform.entity.Interview;
import com.interview.platform.entity.User;
import com.interview.platform.entity.VoiceAnalysis;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.InterviewRepository;
import com.interview.platform.repository.UserRepository;
import com.interview.platform.repository.VoiceAnalysisRepository;
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
public class VoiceAnalysisService {
    
    private final VoiceAnalysisRepository voiceAnalysisRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public VoiceAnalysisDTO analyzeVoice(Long userId, Long interviewId, String voiceText, Integer duration) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        
        // Parse voice text for filler words and repetitions
        VoiceAnalysis voiceAnalysis = VoiceAnalysis.builder()
                .user(user)
                .interview(interview)
                .confidenceScore(calculateConfidenceScore(voiceText))
                .communicationScore(calculateCommunicationScore(voiceText))
                .technicalScore(75) // Would be calculated based on content
                .fillerWords(detectFillerWords(voiceText))
                .speakingSpeed(calculateSpeakingSpeed(voiceText, duration))
                .repeatedWords(detectRepeatedWords(voiceText))
                .suggestions(generateSuggestions(voiceText))
                .audioDurationSeconds(duration)
                .build();
        
        voiceAnalysis = voiceAnalysisRepository.save(voiceAnalysis);
        return mapToDTO(voiceAnalysis);
    }
    
    public List<VoiceAnalysisDTO> getUserVoiceAnalyses(Long userId) {
        return voiceAnalysisRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public VoiceAnalysisDTO getVoiceAnalysis(Long voiceAnalysisId) {
        VoiceAnalysis voiceAnalysis = voiceAnalysisRepository.findById(voiceAnalysisId)
                .orElseThrow(() -> new ResourceNotFoundException("Voice analysis not found"));
        return mapToDTO(voiceAnalysis);
    }
    
    private Integer calculateConfidenceScore(String voiceText) {
        // Simple confidence scoring based on text length and completeness
        return Math.min(100, (voiceText.length() / 10));
    }
    
    private Integer calculateCommunicationScore(String voiceText) {
        // Check for good structure and clarity
        String[] sentences = voiceText.split("\\.");
        int score = Math.min(100, sentences.length * 10);
        return score;
    }
    
    private String detectFillerWords(String voiceText) {
        try {
            int ummCount = countOccurrences(voiceText.toLowerCase(), "umm");
            int uhCount = countOccurrences(voiceText.toLowerCase(), "uh");
            int likeCount = countOccurrences(voiceText.toLowerCase(), "like");
            int youKnowCount = countOccurrences(voiceText.toLowerCase(), "you know");
            
            return objectMapper.writeValueAsString(new Object() {
                public int umm = ummCount;
                public int uh = uhCount;
                public int like = likeCount;
                public int youKnow = youKnowCount;
            });
        } catch (Exception e) {
            log.error("Error detecting filler words", e);
            return "{}";
        }
    }
    
    private String detectRepeatedWords(String voiceText) {
        try {
            String[] words = voiceText.toLowerCase().split("\\s+");
            return objectMapper.writeValueAsString(new Object() {
                public String[] topWords = words.length > 5 ? java.util.Arrays.copyOf(words, 5) : words;
            });
        } catch (Exception e) {
            log.error("Error detecting repeated words", e);
            return "{}";
        }
    }
    
    private String calculateSpeakingSpeed(String voiceText, Integer duration) {
        if (duration == null || duration == 0) return "NORMAL";
        
        int wordCount = voiceText.split("\\s+").length;
        int wordsPerMinute = (wordCount * 60) / duration;
        
        if (wordsPerMinute < 100) return "SLOW";
        if (wordsPerMinute > 160) return "FAST";
        return "NORMAL";
    }
    
    private String generateSuggestions(String voiceText) {
        try {
            return objectMapper.writeValueAsString(new Object() {
                public String[] suggestions = {
                    "Reduce usage of filler words",
                    "Maintain a steady speaking pace",
                    "Use pauses for emphasis",
                    "Take a breath between sentences",
                    "Avoid repetitive phrases"
                };
            });
        } catch (Exception e) {
            log.error("Error generating suggestions", e);
            return "{}";
        }
    }
    
    private int countOccurrences(String str, String word) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }
    
    private VoiceAnalysisDTO mapToDTO(VoiceAnalysis voiceAnalysis) {
        return VoiceAnalysisDTO.builder()
                .id(voiceAnalysis.getId())
                .userId(voiceAnalysis.getUser().getId())
                .interviewId(voiceAnalysis.getInterview() != null ? voiceAnalysis.getInterview().getId() : null)
                .confidenceScore(voiceAnalysis.getConfidenceScore())
                .communicationScore(voiceAnalysis.getCommunicationScore())
                .technicalScore(voiceAnalysis.getTechnicalScore())
                .fillerWords(voiceAnalysis.getFillerWords())
                .speakingSpeed(voiceAnalysis.getSpeakingSpeed())
                .repeatedWords(voiceAnalysis.getRepeatedWords())
                .suggestions(voiceAnalysis.getSuggestions())
                .audioDurationSeconds(voiceAnalysis.getAudioDurationSeconds())
                .build();
    }
}

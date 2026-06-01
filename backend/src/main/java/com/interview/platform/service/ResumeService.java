package com.interview.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.platform.dto.ResumeDTO;
import com.interview.platform.entity.Analytics;
import com.interview.platform.entity.Resume;
import com.interview.platform.entity.User;
import com.interview.platform.exception.BadRequestException;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.AnalyticsRepository;
import com.interview.platform.repository.ResumeRepository;
import com.interview.platform.repository.UserRepository;
import com.interview.platform.util.PdfExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class ResumeService {
    
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final AnalyticsRepository analyticsRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Transactional
    @SuppressWarnings("null")
    public ResumeDTO uploadResume(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        String originalFilename = Optional.ofNullable(file.getOriginalFilename()).orElse("resume.pdf");
        if (!originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BadRequestException("Only PDF files are allowed");
        }
        
        if (file.getSize() > 5242880) { // 5MB
            throw new BadRequestException("File size exceeds 5MB limit");
        }
        
        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir, userId.toString());
        Files.createDirectories(uploadPath);
        
        // Save file
        String fileName = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());
        
        // Extract resume text and analyze with Gemini
        String resumeText = PdfExtractor.extractTextFromPdf(filePath.toString());
        String analysisResult = geminiService.analyzeResume(resumeText);
        
        // Parse analysis result
        Map<String, Object> analysis = parseGeminiResponse(analysisResult);
        
        // Save resume to database
        Resume resume = Resume.builder()
                .user(user)
                .fileName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .resumeScore(toInt(analysis.getOrDefault("score", 0)))
                .strongSkills(objectMapper.writeValueAsString(analysis.getOrDefault("strongSkills", new ArrayList<>())))
                .weakSkills(objectMapper.writeValueAsString(analysis.getOrDefault("weakSkills", new ArrayList<>())))
                .missingSkills(objectMapper.writeValueAsString(analysis.getOrDefault("missingSkills", new ArrayList<>())))
                .suggestions(objectMapper.writeValueAsString(analysis.getOrDefault("suggestions", new ArrayList<>())))
                .build();
        
        resume = resumeRepository.save(resume);
        
        // Update analytics
        updateResumeAnalytics(userId);
        
        return mapToDTO(resume);
    }
    
    public ResumeDTO getResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        return mapToDTO(resume);
    }
    
    public List<ResumeDTO> getUserResumes(Long userId) {
        return resumeRepository.findByUserIdOrderByUploadDateDesc(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    @SuppressWarnings("null")
    public void deleteResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        
        try {
            Files.deleteIfExists(Paths.get(resume.getFilePath()));
        } catch (IOException e) {
            log.error("Error deleting file: {}", resume.getFilePath(), e);
        }
        
        resumeRepository.delete(resume);
    }
    
    private void updateResumeAnalytics(Long userId) {
        Optional<Analytics> analyticsOpt = analyticsRepository.findByUserId(userId);
        if (analyticsOpt.isPresent()) {
            Analytics analytics = analyticsOpt.get();
            List<Resume> resumes = resumeRepository.findByUserId(userId);
            int averageScore = resumes.isEmpty() ? 0 :
                    (int) Math.round(resumes.stream()
                            .mapToInt(Resume::getResumeScore)
                            .average()
                            .orElse(0));
            analytics.setResumeScoreAvg(averageScore);
            analyticsRepository.save(analytics);
        }
    }
    
    private Map<String, Object> parseGeminiResponse(String response) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Parse JSON response from Gemini
            result = objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error parsing Gemini response", e);
            result.put("score", 0);
            result.put("strongSkills", new ArrayList<>());
            result.put("weakSkills", new ArrayList<>());
            result.put("missingSkills", new ArrayList<>());
            result.put("suggestions", new ArrayList<>());
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
    
    private ResumeDTO mapToDTO(Resume resume) {
        return ResumeDTO.builder()
                .id(resume.getId())
                .userId(resume.getUser().getId())
                .fileName(resume.getFileName())
                .filePath(resume.getFilePath())
                .resumeScore(resume.getResumeScore())
                .strongSkills(resume.getStrongSkills())
                .weakSkills(resume.getWeakSkills())
                .missingSkills(resume.getMissingSkills())
                .suggestions(resume.getSuggestions())
                .uploadDate(resume.getUploadDate())
                .createdAt(resume.getCreatedAt())
                .build();
    }
}

package com.interview.platform.service;

import com.interview.platform.dto.*;
import com.interview.platform.entity.*;
import com.interview.platform.exception.BadRequestException;
import com.interview.platform.exception.ResourceNotFoundException;
import com.interview.platform.repository.*;
import com.interview.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class UserService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AnalyticsRepository analyticsRepository;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.UserRole.USER)
                .isActive(true)
                .build();
        
        user = userRepository.save(user);
        
        // Create analytics record
        Analytics analytics = Analytics.builder()
                .user(user)
                .totalInterviews(0)
                .averageScore(0)
                .resumeScoreAvg(0)
                .matchScoreAvg(0)
                .build();
        analyticsRepository.save(analytics);
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(mapToDTO(user))
                .message("User registered successfully")
                .build();
    }
    
    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmailAndIsActive(request.getEmail(), true)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(mapToDTO(user))
                .message("Login successful")
                .build();
    }
    
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDTO(user);
    }
    
    @Transactional
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());
        if (userDTO.getSkills() != null) user.setSkills(userDTO.getSkills());
        if (userDTO.getExperience() != null) user.setExperience(userDTO.getExperience());
        
        user = userRepository.save(user);
        return mapToDTO(user);
    }
    
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }
    
    @Transactional
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    @Transactional
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .skills(user.getSkills())
                .experience(user.getExperience())
                .profileImage(user.getProfileImage())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.request.UserLoginDTO;
import iuh.fit.se.ace_store_www.dto.request.UserRegistrationDTO;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.UserResponseDTO;
import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import iuh.fit.se.ace_store_www.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ApiResponse<UserResponseDTO> registerNewUser(UserRegistrationDTO registrationDTO) {
        return null;
    }

    @Override
    public ApiResponse<UserResponseDTO> loginUser(UserLoginDTO loginDTO) {
        return null;
    }

    @Override
    public ApiResponse<Boolean> verifyUser(String token) {
        return null;
    }

    @Override
    public ApiResponse<UserResponseDTO> findUserById(Long id) {
        return null;
    }

    @Override
    public ApiResponse<UserResponseDTO> updateRole(Long adminId, Long targetUserId, String newRole) {
        return null;
    }
}

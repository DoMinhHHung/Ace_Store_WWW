package iuh.fit.se.ace_store_www.service;

import iuh.fit.se.ace_store_www.dto.request.UserLoginDTO;
import iuh.fit.se.ace_store_www.dto.request.UserRegistrationDTO;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.UserResponseDTO;

public interface UserService {
    ApiResponse<UserResponseDTO> registerNewUser(UserRegistrationDTO registrationDTO);
    ApiResponse<UserResponseDTO> loginUser(UserLoginDTO loginDTO);
    ApiResponse<Boolean> verifyUser(String token);
    ApiResponse<UserResponseDTO> findUserById(Long id);
    ApiResponse<UserResponseDTO> updateRole(Long adminId, Long targetUserId, String newRole);
}
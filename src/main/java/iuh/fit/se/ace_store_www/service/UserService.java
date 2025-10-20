package iuh.fit.se.ace_store_www.service;

import iuh.fit.se.ace_store_www.dto.request.AddressRequest;
import iuh.fit.se.ace_store_www.dto.request.SignupRequest;
import iuh.fit.se.ace_store_www.dto.response.AddressResponse;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    ApiResponse<UserResponse> registerLocalUser(SignupRequest request);
    ApiResponse<UserResponse> getUserResponseByEmail(String email);
    ApiResponse<UserResponse> getUserResponseById(Long id);
    ApiResponse<UserResponse> updateProfile(Long userId, SignupRequest update);
    ApiResponse<AddressResponse> addAddress(Long userId, AddressRequest request);
    ApiResponse<String> removeAddress(Long userId, Long addressId);
    ApiResponse<List<AddressResponse>> listAddresses(Long userId);
    ApiResponse<String> grantAdminRole(Long userId);
}
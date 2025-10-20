package iuh.fit.se.ace_store_www.controller;

import iuh.fit.se.ace_store_www.config.security.JwtUtils;
import iuh.fit.se.ace_store_www.dto.request.AddressRequest;
import iuh.fit.se.ace_store_www.dto.request.SignupRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    // profile by token
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        if (token == null || !jwtUtils.validateJwtToken(token)) return ResponseEntity.status(401).body(ApiResponse.fail("Invalid token"));
        String email = jwtUtils.getEmailFromJwt(token);
        return ResponseEntity.ok(userService.getUserResponseByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateProfile(@PathVariable Long id, @Valid @RequestBody SignupRequest req) {
        return ResponseEntity.ok(userService.updateProfile(id, req));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<ApiResponse<?>> addAddress(@PathVariable Long id, @RequestBody AddressRequest req) {
        return ResponseEntity.ok(userService.addAddress(id, req));
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<ApiResponse<?>> removeAddress(@PathVariable Long id, @PathVariable Long addressId) {
        return ResponseEntity.ok(userService.removeAddress(id, addressId));
    }

    @GetMapping("/{id}/addresses")
    public ResponseEntity<ApiResponse<?>> listAddresses(@PathVariable Long id) {
        return ResponseEntity.ok(userService.listAddresses(id));
    }

    // Get all users - for admin interface
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Delete user - for admin interface
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
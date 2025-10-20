package iuh.fit.se.ace_store_www.controller;

import iuh.fit.se.ace_store_www.config.security.JwtUtils;
import iuh.fit.se.ace_store_www.dto.request.LoginRequest;
import iuh.fit.se.ace_store_www.dto.request.SignupRequest;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.LoginResponse;
import iuh.fit.se.ace_store_www.dto.response.SignupResponse;
import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import iuh.fit.se.ace_store_www.repository.VerificationTokenRepository;
import iuh.fit.se.ace_store_www.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtUtils jwtUtils,
                          UserRepository userRepository,
                          VerificationTokenRepository tokenRepository,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest req) {
        // userService.registerLocalUser trả ApiResponse<UserResponse> (hoặc ApiResponse<UserDto> trong impl của bạn)
        var serviceResp = userService.registerLocalUser(req);
        if (!serviceResp.isSuccess()) {
            // ép kiểu rõ ràng để tránh inference errors
            return ResponseEntity.badRequest().body(ApiResponse.<SignupResponse>fail(serviceResp.getMessage()));
        }
        var userResp = serviceResp.getData(); // kiểu UserResponse (entity -> dto) tùy impl
        SignupResponse sr = new SignupResponse(userResp.getId(), userResp.getEmail(), "User created, check email to verify");
        return ResponseEntity.ok(ApiResponse.ok(sr));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify(@RequestParam("token") String token) {
        return tokenRepository.findByToken(token)
                .map(vt -> {
                    if (vt.getExpiryDate().isBefore(Instant.now())) {
                        // explicit generic parameter to fix inference
                        return ResponseEntity.badRequest().body(ApiResponse.<String>fail("Token expired"));
                    }
                    User user = vt.getUser();
                    user.setEnabled(true);
                    userRepository.save(user);
                    tokenRepository.delete(vt);
                    return ResponseEntity.ok(ApiResponse.ok("Account verified"));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(ApiResponse.<String>fail("Invalid token")));
    }

    // Login -> returns ApiResponse<LoginResponse>
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        var maybe = userRepository.findByEmail(req.getEmail());
        if (maybe.isEmpty()) {
            return ResponseEntity.status(401).body(ApiResponse.<LoginResponse>fail("Invalid credentials"));
        }
        var user = maybe.get();
        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body(ApiResponse.<LoginResponse>fail("Account not verified"));
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(ApiResponse.<LoginResponse>fail("Invalid credentials"));
        }
        Set<String> roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtUtils.generateToken(user.getEmail(), roles);
        LoginResponse lr = new LoginResponse(token, "Bearer", user.getEmail(), roles);
        return ResponseEntity.ok(ApiResponse.ok(lr));
    }
}
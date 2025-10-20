package iuh.fit.se.ace_store_www.service.impl;

import iuh.fit.se.ace_store_www.dto.mapper.UserMapper;
import iuh.fit.se.ace_store_www.dto.request.AddressRequest;
import iuh.fit.se.ace_store_www.dto.request.SignupRequest;
import iuh.fit.se.ace_store_www.dto.response.AddressResponse;
import iuh.fit.se.ace_store_www.dto.response.ApiResponse;
import iuh.fit.se.ace_store_www.dto.response.UserResponse;
import iuh.fit.se.ace_store_www.entity.Address;
import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.entity.VerificationToken;
import iuh.fit.se.ace_store_www.entity.enums.AuthProvider;
import iuh.fit.se.ace_store_www.entity.enums.Role;
import iuh.fit.se.ace_store_www.repository.AddressRepository;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import iuh.fit.se.ace_store_www.repository.VerificationTokenRepository;
import iuh.fit.se.ace_store_www.service.EmailService;
import iuh.fit.se.ace_store_www.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           VerificationTokenRepository tokenRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<UserResponse> registerLocalUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.fail("Email already in use");
        }
        User u = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false)
                .provider(AuthProvider.LOCAL)
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                .build();
        if (request.getDob() != null && !request.getDob().isBlank()) {
            u.setDob(LocalDate.parse(request.getDob()));
        }
        User saved = userRepository.save(u);

        String token = UUID.randomUUID().toString();
        VerificationToken vt = VerificationToken.builder()
                .token(token)
                .user(saved)
                .expiryDate(Instant.now().plusSeconds(24 * 3600))
                .build();
        tokenRepository.save(vt);

        String verifyUrl = System.getenv().getOrDefault("APP_VERIFY_URL", "http://localhost:8080/api/auth/verify?token=") + token;
        emailService.sendSimpleMessage(saved.getEmail(), "Verify your account", "Click here to verify: " + verifyUrl);

        UserResponse resp = userMapper.toResponse(saved);
        return ApiResponse.ok("User created. Check email to verify.", resp);
    }

    @Override
    public ApiResponse<UserResponse> getUserResponseByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("User not found"));
    }

    @Override
    public ApiResponse<UserResponse> getUserResponseById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("User not found"));
    }

    @Override
    public ApiResponse<UserResponse> updateProfile(Long userId, SignupRequest update) {
        var opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ApiResponse.fail("User not found");
        User u = opt.get();
        if (update.getFirstName() != null) u.setFirstName(update.getFirstName());
        if (update.getLastName() != null) u.setLastName(update.getLastName());
        if (update.getPhone() != null) u.setPhone(update.getPhone());
        if (update.getDob() != null && !update.getDob().isBlank()) u.setDob(LocalDate.parse(update.getDob()));
        if (update.getPassword() != null && !update.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(update.getPassword()));
        }
        var saved = userRepository.save(u);
        return ApiResponse.ok(userMapper.toResponse(saved));
    }

    @Override
    public ApiResponse<AddressResponse> addAddress(Long userId, AddressRequest request) {
        var opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ApiResponse.fail("User not found");
        User u = opt.get();
        Address a = Address.builder()
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .user(u)
                .build();
        var saved = addressRepository.save(a);
        u.getAddresses().add(saved);
        userRepository.save(u);
        AddressResponse ar = new AddressResponse();
        ar.setId(saved.getId());
        ar.setLine1(saved.getLine1());
        ar.setLine2(saved.getLine2());
        ar.setCity(saved.getCity());
        ar.setState(saved.getState());
        ar.setPostalCode(saved.getPostalCode());
        ar.setCountry(saved.getCountry());
        return ApiResponse.ok(ar);
    }

    @Override
    public ApiResponse<String> removeAddress(Long userId, Long addressId) {
        var optU = userRepository.findById(userId);
        if (optU.isEmpty()) return ApiResponse.fail("User not found");
        var optA = addressRepository.findById(addressId);
        if (optA.isEmpty()) return ApiResponse.fail("Address not found");
        Address a = optA.get();
        if (!Objects.equals(a.getUser().getId(), userId)) return ApiResponse.fail("Address does not belong to user");
        addressRepository.delete(a);
        return ApiResponse.ok("Address removed");
    }

    @Override
    public ApiResponse<List<AddressResponse>> listAddresses(Long userId) {
        if (!userRepository.existsById(userId)) return ApiResponse.fail("User not found");
        var addrs = addressRepository.findByUserId(userId);
        var list = addrs.stream().map(a -> {
            AddressResponse ar = new AddressResponse();
            ar.setId(a.getId());
            ar.setLine1(a.getLine1());
            ar.setLine2(a.getLine2());
            ar.setCity(a.getCity());
            ar.setState(a.getState());
            ar.setPostalCode(a.getPostalCode());
            ar.setCountry(a.getCountry());
            return ar;
        }).collect(Collectors.toList());
        return ApiResponse.ok(list);
    }

    @Override
    public ApiResponse<String> grantAdminRole(Long userId) {
        var opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ApiResponse.fail("User not found");
        User u = opt.get();
        u.getRoles().add(Role.ROLE_ADMIN);
        userRepository.save(u);
        return ApiResponse.ok("Granted ROLE_ADMIN to user " + u.getEmail());
    }

    @Override
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(userResponses);
    }

    @Override
    public ApiResponse<String> deleteUser(Long userId) {
        var opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ApiResponse.fail("User not found");
        userRepository.deleteById(userId);
        return ApiResponse.ok("User deleted successfully");
    }
}

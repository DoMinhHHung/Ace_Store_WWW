package iuh.fit.se.ace_store_www.config.security;

import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.entity.enums.AuthProvider;
import iuh.fit.se.ace_store_www.entity.enums.Role;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = (String) oauthUser.getAttribute("email");
        String givenName = (String) oauthUser.getAttribute("given_name");
        String familyName = (String) oauthUser.getAttribute("family_name");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = User.builder()
                    .email(email)
                    .firstName(givenName == null ? "" : givenName)
                    .lastName(familyName == null ? "" : familyName)
                    .enabled(true)
                    .provider(AuthProvider.GOOGLE)
                    .roles(Set.of(Role.ROLE_USER))
                    .password("")
                    .build();
            userRepository.save(user);
        }

        var roles = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtUtils.generateToken(user.getEmail(), roles);

        response.setContentType("application/json");
        // Return ApiResponse<LoginResponse> shape without depending on DTO class here for simplicity
        String json = "{\"success\":true,\"message\":null,"
                + "\"data\":{\"token\":\"" + token + "\",\"tokenType\":\"Bearer\",\"email\":\"" + user.getEmail() + "\",\"roles\":"
                + roles.stream().map(r -> "\"" + r + "\"").collect(Collectors.joining(",", "[", "]"))
                + "}}";
        response.getWriter().write(json);
    }
}
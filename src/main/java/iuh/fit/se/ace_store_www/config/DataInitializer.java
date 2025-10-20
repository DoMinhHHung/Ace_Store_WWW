package iuh.fit.se.ace_store_www.config;

import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.entity.enums.Role;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner createDefaultAdmin(UserRepository userRepository,
                                                PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = System.getenv().getOrDefault("APP_ADMIN_EMAIL", "admin@ace_store.com");
            String adminPass = System.getenv().getOrDefault("APP_ADMIN_PASSWORD", "Admin@123");
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .firstName("System")
                        .lastName("Admin")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPass))
                        .enabled(true)
                        .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                        .build();
                userRepository.save(admin);
                System.out.println("Default admin created: " + adminEmail);
            }
        };
    }
}
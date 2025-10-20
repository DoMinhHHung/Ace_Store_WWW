package iuh.fit.se.ace_store_www.config;

import iuh.fit.se.ace_store_www.entity.User;
import iuh.fit.se.ace_store_www.entity.enums.Provider;
import iuh.fit.se.ace_store_www.entity.enums.Role;
import iuh.fit.se.ace_store_www.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ADMIN))
        {
            System.out.println("No admin account found. Creating default admin account...");

            User admin = User.builder()
                    .firstName("Super")
                    .lastName("Admin")
                    .email("admin@ace_store.com")
                    .password(passwordEncoder.encode("123456"))
                    .dob(LocalDate.of(2003,03,04))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .provider(Provider.LOCAL)
                    .build();
            userRepository.save(admin);
        }
    }
}

package iuh.fit.se.ace_store_www.dto.response;

import iuh.fit.se.ace_store_www.entity.enums.Provider;
import iuh.fit.se.ace_store_www.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;

    private String email;
    private String phone;
    private LocalDate dob;

    private Role role;
    private boolean enabled;
    private Provider provider;
}

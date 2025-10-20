package iuh.fit.se.ace_store_www.dto.response;

import iuh.fit.se.ace_store_www.entity.enums.AuthProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private boolean enabled;
    private AuthProvider provider;
    private Set<String> roles;
    private Set<AddressResponse> addresses;
}
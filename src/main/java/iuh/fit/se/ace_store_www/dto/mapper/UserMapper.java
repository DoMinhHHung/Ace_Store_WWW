package iuh.fit.se.ace_store_www.dto.mapper;

import iuh.fit.se.ace_store_www.dto.response.AddressResponse;
import iuh.fit.se.ace_store_www.dto.response.UserResponse;
import iuh.fit.se.ace_store_www.entity.Address;
import iuh.fit.se.ace_store_www.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User u) {
        if (u == null) return null;
        UserResponse r = new UserResponse();
        r.setId(u.getId());
        r.setFirstName(u.getFirstName());
        r.setLastName(u.getLastName());
        r.setFullName(u.getFullName());
        r.setEmail(u.getEmail());
        r.setPhone(u.getPhone());
        r.setDob(u.getDob());
        r.setEnabled(u.isEnabled());
        r.setProvider(u.getProvider());

        // roles null-safe
        Set<String> roles = u.getRoles() == null ? Collections.emptySet()
                : u.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        r.setRoles(roles);

        // addresses null-safe
        Set<AddressResponse> addrs = (u.getAddresses() == null ? Collections.<Address>emptySet() : u.getAddresses())
                .stream()
                .map(this::mapAddr)
                .collect(Collectors.toSet());
        r.setAddresses(addrs);

        return r;
    }

    private AddressResponse mapAddr(Address a) {
        AddressResponse ar = new AddressResponse();
        ar.setId(a.getId());
        ar.setLine1(a.getLine1());
        ar.setLine2(a.getLine2());
        ar.setCity(a.getCity());
        ar.setState(a.getState());
        ar.setPostalCode(a.getPostalCode());
        ar.setCountry(a.getCountry());
        return ar;
    }
}
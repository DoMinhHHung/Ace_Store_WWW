package iuh.fit.se.ace_store_www.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}
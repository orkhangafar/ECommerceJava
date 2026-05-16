package com.test.ecomm.modules.address.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private Long addressId;
    private String title;
    private String country;
    private String city;
    private String street;
    private String zipCode;
    private String phone;
    private boolean isDefault;
}

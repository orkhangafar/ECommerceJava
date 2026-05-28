package com.test.ecomm.modules.address.controller;


import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.modules.address.dto.AddressRequest;
import com.test.ecomm.modules.address.dto.AddressResponse;
import com.test.ecomm.modules.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
        return ResponseEntity.ok(addressService.getMyAddresses());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
        @Valid @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.addAddress(addressRequest));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, addressRequest));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Long>> deleteAddress(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.deleteAddress(addressId));
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddess(
            @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.setDefaultAddress(addressId));
    }
}

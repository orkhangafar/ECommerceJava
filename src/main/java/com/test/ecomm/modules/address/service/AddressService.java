package com.test.ecomm.modules.address.service;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.modules.address.dto.AddressRequest;
import com.test.ecomm.modules.address.dto.AddressResponse;

import java.util.List;

public interface AddressService {
    ApiResponse<AddressResponse> addAddress(AddressRequest addressRequest);
    ApiResponse<AddressResponse> updateAddress(Long addressId, AddressRequest addressRequest);
    ApiResponse<Long> deleteAddress(Long addressId);
    ApiResponse<List<AddressResponse>> getMyAddresses();
    ApiResponse<AddressResponse> setDefaultAddress(Long addressId);
}

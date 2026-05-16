package com.test.ecomm.modules.address.service.impl;

import com.test.ecomm.common.dto.ApiResponse;
import com.test.ecomm.common.exception.BadRequestException;
import com.test.ecomm.common.exception.ResourceNotFoundException;
import com.test.ecomm.common.util.SecurityUtils;
import com.test.ecomm.modules.address.dto.AddressRequest;
import com.test.ecomm.modules.address.dto.AddressResponse;
import com.test.ecomm.modules.address.entity.Address;
import com.test.ecomm.modules.address.repository.AddressRepository;
import com.test.ecomm.modules.address.service.AddressService;
import com.test.ecomm.modules.user.entity.User;
import com.test.ecomm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ApiResponse<AddressResponse> addAddress(AddressRequest addressRequest) {
        User user = getCurrentUser();

        if (addressRequest.isDefault()) {
            clearDefault(user);
        }

        Address address = Address.builder()
                .user(user)
                .title(addressRequest.getTitle())
                .country(addressRequest.getCountry())
                .city(addressRequest.getCity())
                .street(addressRequest.getStreet())
                .zipCode(addressRequest.getZipCode())
                .phone(addressRequest.getPhone())
                .isDefault(addressRequest.isDefault())
                .build();

        return ApiResponse.success(mapToResponse(addressRepository.save(address)), "Ünvan əlavə edildi.");
    }

    @Override
    @Transactional
    public ApiResponse<AddressResponse> updateAddress(Long addressId, AddressRequest addressRequest) {
        Address address = findAddressByIdAndUser(addressId);

        if (addressRequest.isDefault()) {
            clearDefault(address.getUser());
        }

        address.setTitle(addressRequest.getTitle());
        address.setCountry(addressRequest.getCountry());
        address.setCity(addressRequest.getCity());
        address.setStreet(addressRequest.getStreet());
        address.setZipCode(addressRequest.getZipCode());
        address.setPhone(addressRequest.getPhone());
        address.setDefault(addressRequest.isDefault());

        return ApiResponse.success(mapToResponse(addressRepository.save(address)), "Ünvan yeniləndi.");
    }

    @Override
    @Transactional
    public ApiResponse<Long> deleteAddress(Long addressId) {
        Address address = findAddressByIdAndUser(addressId);
        addressRepository.delete(address);
        return ApiResponse.success(addressId, "Ünvan silindi.");
    }

    @Override
    public ApiResponse<List<AddressResponse>> getMyAddresses() {
        User user = getCurrentUser();
        List<AddressResponse> addresses = addressRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
        return ApiResponse.success(addresses, "Ünvanlarınız.");
    }

    @Override
    @Transactional
    public ApiResponse<AddressResponse> setDefaultAddress(Long addressId) {
        Address address = findAddressByIdAndUser(addressId);
        clearDefault(address.getUser());
        address.setDefault(true);
        return ApiResponse.success(mapToResponse(addressRepository.save(address)), "Əsas ünvan təyin edildi.");
    }

    // Köməkçi metodlar--------------------

    private void clearDefault(User user) {
        addressRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(a -> {
                    a.setDefault(false);
                    addressRepository.save(a);
                });
    }

    private Address findAddressByIdAndUser(Long addressId) {
        User user = getCurrentUser();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("address", "addressId", addressId));
        if (!address.getUser().getUserId().equals(user.getUserId())) {
            throw new BadRequestException("Bu ünvana müdaxilə etmək icazəniz yoxdur.");
        }
        return address;
    }

    private User getCurrentUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", SecurityUtils.getCurrentUserEmail()));
    }

    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .title(address.getTitle())
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .zipCode(address.getZipCode())
                .phone(address.getPhone())
                .isDefault(address.isDefault())
                .build();
    }
}
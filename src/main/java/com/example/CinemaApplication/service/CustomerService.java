package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.request.ChangePasswordRequest;
import com.example.CinemaApplication.dto.request.CustomerUpdateRequest;
import com.example.CinemaApplication.dto.response.CustomerResponse;
import com.example.CinemaApplication.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse getMyProfile(String username);

    CustomerResponse updateMyProfile(String username, CustomerUpdateRequest request);

    void changePassword(String username, ChangePasswordRequest request);

    Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable);

    void lockOrDeleteCustomer(Long customerId);

    void updateAccountStatus(Long customerId, AccountStatus status);
}

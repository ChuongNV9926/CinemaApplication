package com.example.CinemaApplication.service.impl;

import com.example.CinemaApplication.dto.request.ChangePasswordRequest;
import com.example.CinemaApplication.dto.request.CustomerUpdateRequest;
import com.example.CinemaApplication.dto.response.CustomerResponse;
import com.example.CinemaApplication.enums.AccountStatus;
import com.example.CinemaApplication.exception.BusinessException;
import com.example.CinemaApplication.exception.ResourceNotFoundException;
import com.example.CinemaApplication.mapper.CustomerMapper;
import com.example.CinemaApplication.model.Account;
import com.example.CinemaApplication.model.Customer;
import com.example.CinemaApplication.repository.AccountRepository;
import com.example.CinemaApplication.repository.BookingRepository;
import com.example.CinemaApplication.repository.CustomerRepository;
import com.example.CinemaApplication.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getMyProfile(String username) {
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));
        Customer customer = customerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new BusinessException("Hồ sơ khách hàng chưa có!"));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateMyProfile(String username, CustomerUpdateRequest request) {
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));
        Customer customer = customerRepository.findByAccount_Id(account.getId())
                .orElseThrow(() -> new BusinessException("Hồ sơ khách hàng chưa có!"));

        // BR-01: Kiểm tra Phone, IdentityCard, Email không trùng với người khác
        if (customerRepository.existsByPhoneAndIdNot(request.phone().trim(), customer.getId())) {
            throw new BusinessException("Số điện thoại đã được người khác sử dụng!");
        }
        if (customerRepository.existsByIdentityCardAndIdNot(request.identityCard().trim(), customer.getId())) {
            throw new BusinessException("Số căn cước đã được người khác sử dụng!");
        }
        if (customerRepository.existsByEmailIgnoreCaseAndIdNot(request.email().trim(), customer.getId())) {
            throw new BusinessException("Email đã được người khác sử dụng!");
        }

        customer.setFullName(request.fullName().trim());
        customer.setPhone(request.phone().trim());
        customer.setBirthday(request.birthday());
        customer.setIdentityCard(request.identityCard().trim());
        customer.setEmail(request.email().trim());

        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        Account account = accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại!"));

        if (!passwordEncoder.matches(request.oldPassword(), account.getPassword())) {
            throw new BusinessException("Mật khẩu hiện tại không chính xác!");
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp!");
        }

        // Đổi mật khẩu và tăng token_version để thu hồi JWT cũ trên các thiết bị khác
        account.setPassword(passwordEncoder.encode(request.newPassword()));
        account.setTokenVersion(account.getTokenVersion() + 1);
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String keyword, Pageable pageable) {
        Page<Customer> page;
        if (keyword != null && !keyword.isBlank()) {
            page = customerRepository.searchKeyword(keyword.trim(), pageable);
        } else {
            page = customerRepository.findAll(pageable);
        }

        List<CustomerResponse> content = page.getContent().stream().map(customerMapper::toResponse).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public void lockOrDeleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng ID: " + customerId));

        // BR-04: Khách chưa có Booking thì xóa hẳn Customer và Account; đã có Booking thì khóa Account
        boolean hasBooking = bookingRepository.existsByCustomer_Id(customerId);
        Account account = customer.getAccount();

        if (hasBooking) {
            account.setStatus(AccountStatus.LOCKED);
            account.setTokenVersion(account.getTokenVersion() + 1); // Thu hồi token
            accountRepository.save(account);
        } else {
            customerRepository.delete(customer);
            if (account != null) {
                accountRepository.delete(account);
            }
        }
    }

    @Override
    @Transactional
    public void updateAccountStatus(Long customerId, AccountStatus status) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng ID: " + customerId));

        Account account = customer.getAccount();
        if (account != null) {
            account.setStatus(status);
            if (status == AccountStatus.LOCKED) {
                account.setTokenVersion(account.getTokenVersion() + 1);
            }
            accountRepository.save(account);
        }
    }
}

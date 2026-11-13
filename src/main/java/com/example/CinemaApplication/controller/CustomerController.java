package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.request.ChangePasswordRequest;
import com.example.CinemaApplication.dto.request.CustomerUpdateRequest;
import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.CustomerResponse;
import com.example.CinemaApplication.enums.AccountStatus;
import com.example.CinemaApplication.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CustomerController {

    private final CustomerService customerService;

    // Customer API: Xem hồ sơ cá nhân
    @GetMapping("/customer/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(@AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin hồ sơ thành công", customerService.getMyProfile(principal.getUsername())));
    }

    // Customer API: Cập nhật hồ sơ cá nhân
    @PutMapping("/customer/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công", customerService.updateMyProfile(principal.getUsername(), request)));
    }

    // Customer API: Đổi mật khẩu
    @PutMapping("/customer/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        customerService.changePassword(principal.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công. Vui lòng đăng nhập lại", null));
    }

    // Admin API: Tìm kiếm, lọc và phân trang khách hàng
    @GetMapping("/admin/customers")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> adminSearchCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng thành công", customerService.searchCustomers(keyword, pageable)));
    }

    // Admin API: Khóa hoặc xóa khách hàng (BR-04)
    @DeleteMapping("/admin/customers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrLockCustomer(@PathVariable Long id) {
        customerService.lockOrDeleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Thực hiện thao tác thành công", null));
    }

    // Admin API: Cập nhật trạng thái ACTIVE / LOCKED
    @PutMapping("/admin/customers/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateCustomerStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status
    ) {
        customerService.updateAccountStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái tài khoản thành công", null));
    }
}

package com.example.CinemaApplication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CustomerUpdateRequest(
        @NotBlank @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
        String fullName,

        @NotBlank @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại gồm 10 chữ số bắt đầu bằng 0")
        String phone,

        @NotNull(message = "Ngày sinh không được để trống")
        @Past(message = "Ngày sinh phải trong quá khứ")
        LocalDate birthday,

        @NotBlank @Pattern(regexp = "^[0-9]{12}$", message = "Số căn cước phải đúng 12 chữ số")
        String identityCard,

        @NotBlank @Email(message = "Email không hợp lệ") @Size(max = 100, message = "Email tối đa 100 ký tự")
        String email
) {
}

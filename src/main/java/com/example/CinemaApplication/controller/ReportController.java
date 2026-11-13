package com.example.CinemaApplication.controller;

import com.example.CinemaApplication.dto.response.ApiResponse;
import com.example.CinemaApplication.dto.response.RevenueReportResponse;
import com.example.CinemaApplication.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reports")
public class ReportController {

    private final ReportService reportService;

    // Admin API: Xem báo cáo doanh thu theo kỳ (BR-15)
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(ApiResponse.success("Lấy báo cáo doanh thu thành công", reportService.getRevenueReport(startDate, endDate)));
    }
}

package com.example.CinemaApplication.service;

import com.example.CinemaApplication.dto.response.RevenueReportResponse;

import java.time.LocalDate;

public interface ReportService {

    RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate);
}

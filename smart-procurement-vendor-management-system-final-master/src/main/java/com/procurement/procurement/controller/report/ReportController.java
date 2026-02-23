package com.procurement.procurement.controller.report;

import com.procurement.procurement.service.report.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/vendor")
    public ResponseEntity<byte[]> generateVendorReport(
            @RequestParam(defaultValue = "pdf") String format) {

        byte[] report = reportService.generateVendorReport(format);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=vendor." +
                                (format.equalsIgnoreCase("excel") ? "xlsx" : "pdf"))
                .body(report);
    }
}
package com.procurement.procurement.service.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ReportService {

    public byte[] generateVendorReport(String format) {

        try {

            // Dummy Data
            List<Map<String, ?>> data = new ArrayList<>();

            Map<String, Object> row1 = new HashMap<>();
            row1.put("vendorName", "ABC Traders");
            row1.put("rating", 4.5);
            row1.put("status", "ACTIVE");

            Map<String, Object> row2 = new HashMap<>();
            row2.put("vendorName", "XYZ Supplies");
            row2.put("rating", 4.2);
            row2.put("status", "ACTIVE");

            data.add(row1);
            data.add(row2);

            JRMapCollectionDataSource dataSource =
                    new JRMapCollectionDataSource(data);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", "Vendor Report");

            InputStream reportStream =
                    new ClassPathResource("jasper/vendor_report.jrxml")
                            .getInputStream();

            JasperDesign jasperDesign =
                    JRXmlLoader.load(reportStream);

            JasperReport jasperReport =
                    JasperCompileManager.compileReport(jasperDesign);

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            if ("excel".equalsIgnoreCase(format)) {

                JRXlsxExporter exporter = new JRXlsxExporter();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(
                        new SimpleOutputStreamExporterOutput(outputStream)
                );

                exporter.exportReport();

                return outputStream.toByteArray();
            }

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Report generation failed", e);
        }
    }
}
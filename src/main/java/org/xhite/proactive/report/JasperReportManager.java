package org.xhite.proactive.report;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JasperReportManager {

    public byte[] generatePdfReport(String reportPath, Map<String, Object> parameters) throws JRException, FileNotFoundException {
        try {
            File file = ResourceUtils.getFile("classpath:reports/" + reportPath);
            if (!file.exists()) {
                throw new FileNotFoundException("Report template not found at: " + file.getAbsolutePath());
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            if (jasperReport == null) {
                throw new JRException("Failed to compile report");
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            if (jasperPrint == null) {
                throw new JRException("Failed to fill report");
            }

            byte[] pdfContent = JasperExportManager.exportReportToPdf(jasperPrint);
            if (pdfContent == null || pdfContent.length == 0) {
                throw new JRException("Generated PDF content is empty");
            }

            return pdfContent;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}



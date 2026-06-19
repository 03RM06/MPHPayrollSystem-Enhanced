package Services;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportService {
     static {
        net.sf.jasperreports.engine.JRPropertiesUtil
                .getInstance(net.sf.jasperreports.engine.DefaultJasperReportsContext.getInstance())
                .setProperty("net.sf.jasperreports.compiler.xml.validation", "false");
    }

    /** For table-style reports (Payroll Summary, Timecard) backed by a list of beans. */
    public void viewReport(String reportResourceName, Map<String, Object> parameters,
                            Collection<?> dataList) throws Exception {
        JasperPrint print = fillReport(reportResourceName, parameters, dataList);
        JasperViewer.viewReport(print, false);
    }

    public void exportToPdf(String reportResourceName, Map<String, Object> parameters,
                             Collection<?> dataList, String outputPath) throws Exception {
        JasperPrint print = fillReport(reportResourceName, parameters, dataList);
        JasperExportManager.exportReportToPdfFile(print, outputPath);
    }

    /** For single-record reports (Payslip) driven purely by parameters. */
    public void viewReportWithParams(String reportResourceName, Map<String, Object> parameters)
            throws Exception {
        JasperPrint print = fillReport(reportResourceName, parameters, List.of());
        JasperViewer.viewReport(print, false);
    }

    public void exportToPdfWithParams(String reportResourceName, Map<String, Object> parameters,
                                       String outputPath) throws Exception {
        JasperPrint print = fillReport(reportResourceName, parameters, List.of());
        JasperExportManager.exportReportToPdfFile(print, outputPath);
    }

    private JasperPrint fillReport(String reportResourceName, Map<String, Object> parameters,
                                Collection<?> dataList) throws Exception {
    if (parameters == null) parameters = new HashMap<>();
    try (InputStream jrxmlStream = getClass().getResourceAsStream("/reports/" + reportResourceName)) {
        if (jrxmlStream == null) {
            throw new IllegalArgumentException("Report template not found: " + reportResourceName);
        }
        byte[] bytes = jrxmlStream.readAllBytes();

        // Diagnostic step: validate as plain XML first to surface the real parse error
        try {
    javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(new java.io.ByteArrayInputStream(bytes));
} catch (org.xml.sax.SAXParseException saxEx) {
    String content = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    String[] lines = content.split("\n", -1);
    int lineNum = saxEx.getLineNumber();
    StringBuilder diag = new StringBuilder();
    diag.append("Parse failed at line ").append(lineNum)
        .append(", column ").append(saxEx.getColumnNumber()).append("\n\n");
    if (lineNum - 1 < lines.length) {
        String badLine = lines[lineNum - 1];
        diag.append("Raw line text:\n[").append(badLine).append("]\n\n");
        diag.append("Character codes:\n");
        for (int i = 0; i < badLine.length(); i++) {
            char c = badLine.charAt(i);
            diag.append(i + 1).append(":'").append(c).append("'=U+")
                .append(String.format("%04X", (int) c)).append("  ");
        }
    }
    throw new RuntimeException(diag.toString(), saxEx);
} catch (Exception xmlEx) {
    throw new RuntimeException(
            "XML validation failed for " + reportResourceName + ": " + xmlEx.getMessage(), xmlEx);
}

        JasperReport jasperReport = JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(bytes));
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);
        return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
    }
}
}
package wgu.jbas127.frontiercompanionbackend.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import wgu.jbas127.frontiercompanionbackend.dto.MostRecentQueryDTO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for generating PDF reports related to application data, such as search analytics.
 */
@Service
public class ReportService {

    /**
     * Generates a PDF report containing search analytics from the provided list of recent queries.
     * The report includes query text, result counts, clicked article IDs, and timestamps.
     *
     * @param queries The list of {@link MostRecentQueryDTO} objects to include in the report.
     * @return A byte array representing the generated PDF document.
     * @throws IOException If an error occurs during PDF generation.
     */
    public byte[] generateSearchAnalyticsPdfReport(List<MostRecentQueryDTO> queries) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add title
        document.add(new Paragraph("Search Analytics Report")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Generated: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Create table
        Table table = new Table(new float[]{3, 2, 2, 3});
        table.setWidth(UnitValue.createPercentValue(100));

        // Add headers
        table.addHeaderCell(new Cell().add(new Paragraph("Query Text").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Result Count").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Clicked Article ID").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Search Timestamp").setBold()));

        // Add data rows
        for (MostRecentQueryDTO query : queries) {
            table.addCell(new Cell().add(new Paragraph(query.getQueryText())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(query.getResultCount()))));
            table.addCell(new Cell().add(new Paragraph(
                    query.getClickedArticleId() != null ?
                            String.valueOf(query.getClickedArticleId()) : "N/A"
            )));
            table.addCell(new Cell().add(new Paragraph(
                    query.getSearchTimestamp().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )));
        }

        document.add(table);

        // Add summary
        document.add(new Paragraph("\nTotal Queries: " + queries.size())
                .setMarginTop(20)
                .setBold());

        document.close();
        return outputStream.toByteArray();
    }
}

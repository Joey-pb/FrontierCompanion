package wgu.jbas127.frontiercompanionbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wgu.jbas127.frontiercompanionbackend.dto.MostRecentQueryDTO;
import wgu.jbas127.frontiercompanionbackend.service.ReportService;
import wgu.jbas127.frontiercompanionbackend.service.SearchService;

import java.io.IOException;
import java.util.List;

/**
 * REST controller for generating and downloading reports.
 * Currently supports downloading search analytics in PDF format.
 * Current reports include: Most Recent Queries
 */
@RestController
@RequestMapping("api/analytics")
@Slf4j
@SecurityRequirements({
        @SecurityRequirement(name = "api-key"),
        @SecurityRequirement(name = "basic-auth")
})
@RequiredArgsConstructor
@Tag(name = "Search Analytics", description = "Report and Analytics APIs")
public class AnalyticsController {

    private final SearchService searchService;
    private final ReportService reportService;

    /**
     * Generates and downloads a PDF report containing search analytics.
     * The report includes a specified number of the most recent search queries.
     *
     * @param limit The maximum number of recent queries to include in the report (default is 10).
     * @return A {@link ResponseEntity} containing the PDF file as a byte array with appropriate headers.
     * @throws IOException If an error occurs during PDF generation.
     */
    @GetMapping("/search-analytics")
    @Operation(summary = "Download search analytics PDF report")
    public ResponseEntity<byte[]> downloadSearchAnalytics(
            @RequestParam(defaultValue = "10") int limit) throws IOException{

        List<MostRecentQueryDTO> queries = searchService.getMostRecentQueries(limit);
        byte[] pdfBytes = reportService.generateSearchAnalyticsPdfReport(queries);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("search-analytics-report.pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Retrieves a list of the most popular search queries.
     *
     * @param limit The maximum number of popular queries to return (default is 10).
     * @return A {@link ResponseEntity} containing a list of strings.
     */
    @GetMapping("/popular")
    @Operation(summary = "Get popular search queries")
    public ResponseEntity<List<String>> getPopularQueries(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.getPopularQueries(limit));
    }

    /**
     * Retrieves a list of the most recent search queries with metadata.
     *
     * @param limit The maximum number of recent queries to return (default is 10).
     * @return A {@link ResponseEntity} containing a list of {@link MostRecentQueryDTO} objects.
     */
    @GetMapping("/recent")
    @Operation(summary = "Get most recent search queries")
    public ResponseEntity<List<MostRecentQueryDTO>> getMostRecentQueries(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.getMostRecentQueries(limit));
    }

}

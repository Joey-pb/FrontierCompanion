package wgu.jbas127.frontiercompanionbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wgu.jbas127.frontiercompanionbackend.dto.SearchRequest;
import wgu.jbas127.frontiercompanionbackend.dto.SearchResultDTO;
import wgu.jbas127.frontiercompanionbackend.service.SearchService;

/**
 * REST controller for semantic search operations and analytics.
 * Provides endpoints for searching articles and narratives, and retrieving popular or recent queries.
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search APIs for articles and narratives")
public class SearchController {

    private final SearchService searchService;

    /**
     * Performs a semantic similarity-search based on a query parameter.
     * Results include both articles and narratives that exceed the similarity threshold.
     *
     * @param query     The search query text.
     * @param limit     The maximum number of results to return (default is 20).
     * @param threshold The similarity threshold for results (default is 0.3).
     * @return A {@link ResponseEntity} containing a {@link SearchResultDTO} with matched items.
     */
    @GetMapping
    @Operation(summary = "Semantic similarity-search with query parameter")
    public ResponseEntity<SearchResultDTO> searchSimple(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0.3") Double threshold) {

        SearchRequest request = new SearchRequest();
        request.setQuery(query);
        request.setLimit(limit);
        request.setThreshold(threshold);

        return ResponseEntity.ok(searchService.search(request));
    }
}

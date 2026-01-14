package wgu.jbas127.frontiercompanionbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wgu.jbas127.frontiercompanionbackend.dto.ArticleDTO;
import wgu.jbas127.frontiercompanionbackend.dto.NarrativeDTO;
import wgu.jbas127.frontiercompanionbackend.dto.SearchRequest;
import wgu.jbas127.frontiercompanionbackend.dto.SearchResultDTO;
import wgu.jbas127.frontiercompanionbackend.service.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search APIs for articles and narratives")
public class SearchController {

    private final SearchService searchService;

//    /**
//     * Unified Search
//     */
//    @PostMapping
//    @Operation(summary = "Unified search", description = "Search both articles and narratives")
//    public ResponseEntity<SearchResultDTO> search(@RequestBody SearchRequest searchRequest) {
//        return ResponseEntity.ok(searchService.search(searchRequest));
//    }

    /**
     * Search only Articles
     */
    @PostMapping("/articles")
    @Operation(summary = "Search only articles")
    public ResponseEntity<List<ArticleDTO>> searchArticles(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(searchService.searchArticles(searchRequest));
    }

    /**
     * Search only Narratives
     */
    @PostMapping("/narratives")
    @Operation(summary = "Search only narratives")
    public ResponseEntity<List<NarrativeDTO>> searchNarratives(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(searchService.searchNarratives(searchRequest));
    }

    /**
     * Search Narratives for specific exhibit
     */
    @PostMapping("/narratives/exhibit/{exhibitId}")
    @Operation(summary = "Search narratives for specific exhibit")
    public ResponseEntity<List<NarrativeDTO>> searchNarrativeExhibit(
            @PathVariable Long exhibitId,
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.searchNarrativesByExhibit(query, exhibitId, limit));
    }

    @GetMapping
    @Operation(summary = "Simple search with query parameter")
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

    @GetMapping("/popular")
    @Operation(summary = "Get popular search queries")
    public ResponseEntity<List<String>> getPopularQueries(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.getPopularQueries(limit));
    }
}

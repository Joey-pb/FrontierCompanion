package wgu.jbas127.frontiercompanionbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wgu.jbas127.frontiercompanionbackend.dto.SearchRequest;
import wgu.jbas127.frontiercompanionbackend.dto.SearchResultDTO;
import wgu.jbas127.frontiercompanionbackend.service.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<SearchResultDTO> search(@RequestBody SearchRequest searchRequest) {
        return ResponseEntity.ok(searchService.search(searchRequest));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularQueries(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(searchService.getPopularQueries(limit));
    }
}

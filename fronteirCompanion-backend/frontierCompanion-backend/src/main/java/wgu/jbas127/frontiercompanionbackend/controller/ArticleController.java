package wgu.jbas127.frontiercompanionbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wgu.jbas127.frontiercompanionbackend.dto.ArticleDTO;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.service.ArticleService;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticle(@PathVariable long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/exhibit/{exhibitId}")
    public ResponseEntity<List<ArticleDTO>> getArticlesByExhibit(@PathVariable long exhibitId) {
        return ResponseEntity.ok(articleService.getArticlesByExhibitId(exhibitId));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<ArticleDTO>> getSimilarArticles(
            @PathVariable long id,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(articleService.findSimilarArticles(id, limit));
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody Article article) {
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }
}

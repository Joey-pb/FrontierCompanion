package wgu.jbas127.frontiercompanionbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wgu.jbas127.frontiercompanionbackend.dto.ArticleDTO;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.service.ArticleService;

import java.util.List;

/**
 * REST controller for managing articles.
 * Provides endpoints for retrieving, creating, and updating articles.
 */
@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * Retrieves all articles.
     *
     * @return A {@link ResponseEntity} containing a list of {@link ArticleDTO} objects.
     */
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    /**
     * Retrieves a specific article by its ID.
     *
     * @param id The ID of the article to retrieve.
     * @return A {@link ResponseEntity} containing the found {@link ArticleDTO}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticle(@PathVariable long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    /**
     * Retrieves all articles associated with a specific exhibit.
     *
     * @param exhibitId The ID of the exhibit.
     * @return A {@link ResponseEntity} containing a list of {@link ArticleDTO} objects.
     */
    @GetMapping("/exhibit/{exhibitId}")
    public ResponseEntity<List<ArticleDTO>> getArticlesByExhibit(@PathVariable long exhibitId) {
        return ResponseEntity.ok(articleService.getArticlesByExhibitId(exhibitId));
    }

    /**
     * Creates a new article.
     *
     * @param article The {@link Article} entity to create.
     * @return A {@link ResponseEntity} containing the created {@link Article}.
     */
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }

    /**
     * Updates an existing article.
     *
     * @param id      The ID of the article to update.
     * @param article The {@link Article} entity with updated information.
     * @return A {@link ResponseEntity} containing the updated {@link Article}.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id, @RequestBody Article article) {
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }
}

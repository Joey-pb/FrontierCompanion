package wgu.jbas127.frontiercompanionbackend.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wgu.jbas127.frontiercompanionbackend.dto.ArticleDTO;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.repository.ArticleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final EmbeddingService embeddingService;

    public List<ArticleDTO> getAllArticles() {
        return articleRepository.findAllOrderByPublishedDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ArticleDTO getArticleById(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        return convertToDTO(article);
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> getArticlesByExhibitId(Long exhibitId) {
        return articleRepository.findByExhibitId(exhibitId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> findSimilarArticles(Long articleId, int limit) {
        return articleRepository.findSimilarArticles(articleId, limit).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Article createArticle(Article article) {
        // Generate embedding for new article
        return generateArticleEmbedding(article);
    }

    @Transactional
    public Article updateArticle(Long id, Article updatedArticle) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        article.setTitle(updatedArticle.getTitle());
        article.setDescription(updatedArticle.getDescription());
        article.setContent(updatedArticle.getContent());
        article.setUrl(updatedArticle.getUrl());
        article.setThumbnailUrl(updatedArticle.getThumbnailUrl());
        article.setAuthor(updatedArticle.getAuthor());
        article.setSource(updatedArticle.getSource());
        article.setPublishedDate(updatedArticle.getPublishedDate());

        // Regenerate embedding
        return generateArticleEmbedding(article);
    }

    private Article generateArticleEmbedding(Article article) {
        String textToEmbed = article.getTitle() + " " +
                (article.getDescription() != null ? article.getDescription() : "") + " " +
                (article.getContent() != null ? article.getContent() : "");

        float[] embedding = embeddingService.generateEmbedding(textToEmbed);
        article.setEmbedding(new PGvector(embedding));

        return articleRepository.save(article);
    }

    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();

        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setUrl(article.getUrl());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setAuthor(article.getAuthor());
        dto.setSource(article.getSource());
        dto.setPublishedDate(article.getPublishedDate() != null ? article.getPublishedDate().toString() : null);

        return dto;
    }
}

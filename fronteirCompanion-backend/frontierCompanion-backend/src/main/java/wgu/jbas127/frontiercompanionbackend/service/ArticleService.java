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

/**
 * Service class for managing articles, including their retrieval, creation,
 * updates, and semantic embedding generation.
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final EmbeddingService embeddingService;

    /**
     * Retrieves all articles ordered by published date in descending order.
     *
     * @return A list of {@link ArticleDTO} objects.
     */
    public List<ArticleDTO> getAllArticles() {
        return articleRepository.findAllOrderByPublishedDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single article by its unique identifier.
     *
     * @param id The ID of the article to retrieve.
     * @return The {@link ArticleDTO} for the found article.
     * @throws RuntimeException if no article is found with the given ID.
     */
    @Transactional(readOnly = true)
    public ArticleDTO getArticleById(long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        return convertToDTO(article);
    }

    /**
     * Retrieves all articles associated with a specific exhibit.
     *
     * @param exhibitId The unique identifier of the exhibit.
     * @return A list of {@link ArticleDTO} objects associated with the exhibit.
     */
    @Transactional(readOnly = true)
    public List<ArticleDTO> getArticlesByExhibitId(Long exhibitId) {
        return articleRepository.findByExhibitId(exhibitId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds articles that are semantically similar to the specified article.
     *
     * @param articleId The ID of the reference article.
     * @param limit     The maximum number of similar articles to return.
     * @return A list of similar {@link ArticleDTO} objects.
     */
    @Transactional(readOnly = true)
    public List<ArticleDTO> findSimilarArticles(Long articleId, int limit) {
        return articleRepository.findSimilarArticles(articleId, limit).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new article and automatically generates its vector embedding.
     *
     * @param article The article entity to be created.
     * @return The saved {@link Article} entity including its generated embedding.
     */
    @Transactional
    public Article createArticle(Article article) {
        return generateArticleEmbedding(article);
    }

    /**
     * Updates an existing article's details and regenerates its vector embedding.
     *
     * @param id             The ID of the article to update.
     * @param updatedArticle The article entity containing updated information.
     * @return The updated and saved {@link Article} entity.
     * @throws RuntimeException if the article to be updated does not exist.
     */
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

        return generateArticleEmbedding(article);
    }

    /**
     * Generates a vector embedding for an article based on its title, description, and content,
     * then saves the article.
     *
     * @param article The article for which to generate the embedding.
     * @return The saved {@link Article} entity.
     */
    private Article generateArticleEmbedding(Article article) {
        String textToEmbed = article.getTitle() + " " +
                (article.getDescription() != null ? article.getDescription() : "") + " " +
                (article.getContent() != null ? article.getContent() : "");

        float[] embedding = embeddingService.generateEmbedding(textToEmbed);
        article.setEmbedding(new PGvector(embedding));

        return articleRepository.save(article);
    }

    /**
     * Converts an {@link Article} entity to an {@link ArticleDTO}.
     *
     * @param article The entity to convert.
     * @return The resulting DTO.
     */
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

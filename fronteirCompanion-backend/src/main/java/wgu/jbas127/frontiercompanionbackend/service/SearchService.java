package wgu.jbas127.frontiercompanionbackend.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wgu.jbas127.frontiercompanionbackend.dto.*;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.entitiy.SearchAnalytics;
import wgu.jbas127.frontiercompanionbackend.repository.ArticleRepository;
import wgu.jbas127.frontiercompanionbackend.repository.NarrativeRepository;
import wgu.jbas127.frontiercompanionbackend.repository.SearchAnalyticsRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for performing semantic searches across articles and narratives.
 * It also handles search analytics tracking and retrieval.
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private final ArticleRepository articleRepository;
    private final NarrativeRepository narrativeRepository;
    private final SearchAnalyticsRepository analyticsRepository;
    private final EmbeddingService embeddingService;

    /**
     * Executes a semantic search based on the provided request.
     * Generates an embedding for the query text and retrieves similar articles and narratives.
     * Search results are logged in the analytics repository.
     *
     * @param request The {@link SearchRequest} containing the query, threshold, and limit.
     * @return A {@link SearchResultDTO} containing the matching articles and narratives.
     */
    @Transactional
    public SearchResultDTO search(SearchRequest request) {
        // Track search analytics
        SearchAnalytics analytics = new SearchAnalytics();
        analytics.setQueryText(request.getQuery());

        // Generate embedding for search query
        float[] queryEmbedding = embeddingService.generateEmbedding(request.getQuery());
        String embeddingStr = new PGvector(queryEmbedding).toString();

        // Search articles
        List<Article> articles = articleRepository.searchBySimilarity(
                embeddingStr,
                request.getThreshold(),
                request.getLimit()
        );

        // Search narratives
        List<Narrative> narratives = narrativeRepository.searchBySimilarity(
                embeddingStr,
                request.getThreshold(),
                request.getLimit()
        );

        // Track total results
        int totalResults = articles.size() + narratives.size();
        analytics.setResultCount(totalResults);
        analyticsRepository.save(analytics);

        SearchResultDTO result = new SearchResultDTO();
        result.setArticles(articles.stream()
                .map(this::convertArticleToDTO)
                .collect(Collectors.toList()));
        result.setNarratives(narratives.stream()
                .map(this::convertNarrativeToDTO)
                .collect(Collectors.toList()));
        result.setTotalResults(articles.size());

        return result;
    }

    /**
     * Retrieves a list of the most frequent search queries.
     *
     * @param limit The maximum number of popular queries to return.
     * @return A list of strings representing the most popular queries.
     */
    public List<String> getPopularQueries(int limit) {
        return analyticsRepository.findPopularQueries().stream()
                .limit(limit)
                .map(row -> (String) row[0])
                .collect(Collectors.toList());

    }

    /**
     * Retrieves the most recent search queries with their result counts and timestamps.
     *
     * @param limit The maximum number of recent queries to return.
     * @return A list of {@link MostRecentQueryDTO} objects.
     */
    public List<MostRecentQueryDTO> getMostRecentQueries(int limit) {
        return analyticsRepository.findRecentQueries().stream()
                .limit(limit)
                .map(s -> new MostRecentQueryDTO(
                        s.getQueryText(),
                        s.getResultCount(),
                        s.getClickedArticle() != null ? s.getClickedArticle().getId() : null,
                        s.getSearchTimestamp()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Converts an {@link Article} entity to an {@link ArticleDTO}.
     *
     * @param article The article entity to convert.
     * @return The resulting DTO.
     */
    private ArticleDTO convertArticleToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();

        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setUrl(article.getUrl());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setAuthor(article.getAuthor());
        dto.setSource(article.getSource());
        dto.setPublishedDate(article.getPublishedDate() != null ?
                article.getPublishedDate().toString() : null);

        return dto;
    }

    /**
     * Converts a {@link Narrative} entity to a {@link NarrativeDTO}.
     *
     * @param narrative The narrative entity to convert.
     * @return The resulting DTO.
     */
    private NarrativeDTO convertNarrativeToDTO(Narrative narrative) {
        NarrativeDTO dto = new NarrativeDTO();

        dto.setId(narrative.getId());
        dto.setExhibitId(narrative.getExhibitId());
        dto.setContent(narrative.getContent());
        dto.setSectionName(narrative.getSectionName());

        return dto;
    }
}

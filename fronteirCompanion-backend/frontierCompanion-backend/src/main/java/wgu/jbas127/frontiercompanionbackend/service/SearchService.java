package wgu.jbas127.frontiercompanionbackend.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wgu.jbas127.frontiercompanionbackend.dto.ArticleDTO;
import wgu.jbas127.frontiercompanionbackend.dto.NarrativeDTO;
import wgu.jbas127.frontiercompanionbackend.dto.SearchRequest;
import wgu.jbas127.frontiercompanionbackend.dto.SearchResultDTO;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.entitiy.SearchAnalytics;
import wgu.jbas127.frontiercompanionbackend.repository.ArticleRepository;
import wgu.jbas127.frontiercompanionbackend.repository.NarrativeRepository;
import wgu.jbas127.frontiercompanionbackend.repository.SearchAnalyticsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ArticleRepository articleRepository;
    private final NarrativeRepository narrativeRepository;
    private final SearchAnalyticsRepository analyticsRepository;
    private final EmbeddingService embeddingService;

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
     * Search only articles
     */
    @Transactional
    public List<ArticleDTO> searchArticles(SearchRequest request) {
        float[] queryEmbedding = embeddingService.generateEmbedding(request.getQuery());
        String embeddingStr = new PGvector(queryEmbedding).toString();

        List<Article> articles = articleRepository.searchBySimilarity(
                embeddingStr,
                request.getThreshold(),
                request.getLimit()
        );

        return articles.stream()
                .map(this::convertArticleToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search only narratives
     */
    @Transactional
    public List<NarrativeDTO> searchNarratives(SearchRequest request) {
        float[] queryEmbedding = embeddingService.generateEmbedding(request.getQuery());
        String embeddingStr = new PGvector(queryEmbedding).toString();

        List<Narrative> narratives = narrativeRepository.searchBySimilarity(
                embeddingStr,
                request.getThreshold(),
                request.getLimit()
        );

        return narratives.stream()
                .map(this::convertNarrativeToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search narratives for a specific exhibit
     */
    @Transactional(readOnly = true)
    public List<NarrativeDTO> searchNarrativesByExhibit(String query, Long exhibitId, int limit) {
        float[] queryEmbedding = embeddingService.generateEmbedding(query);
        String embeddingStr = new PGvector(queryEmbedding).toString();

        List<Narrative> narratives = narrativeRepository.searchByExhibitAndSimilarity(
                exhibitId,
                embeddingStr,
                0.5,
                limit
        );

        return narratives.stream()
                .map(this::convertNarrativeToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getPopularQueries(int limit) {
        return analyticsRepository.findPopularQueries().stream()
                .limit(limit)
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }

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

    private NarrativeDTO convertNarrativeToDTO(Narrative narrative) {
        NarrativeDTO dto = new NarrativeDTO();

        dto.setId(narrative.getId());
        dto.setExhibitId(narrative.getExhibitId());
        dto.setContent(narrative.getContent());
        dto.setSectionName(narrative.getSectionName());

        return dto;
    }
}

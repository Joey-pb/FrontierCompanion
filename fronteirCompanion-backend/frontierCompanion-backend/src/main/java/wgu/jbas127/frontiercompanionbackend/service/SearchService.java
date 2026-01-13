package wgu.jbas127.frontiercompanionbackend.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wgu.jbas127.frontiercompanionbackend.dto.ArticleDTO;
import wgu.jbas127.frontiercompanionbackend.dto.SearchRequest;
import wgu.jbas127.frontiercompanionbackend.dto.SearchResultDTO;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.entitiy.SearchAnalytics;
import wgu.jbas127.frontiercompanionbackend.repository.ArticleRepository;
import wgu.jbas127.frontiercompanionbackend.repository.SearchAnalyticsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ArticleRepository articleRepository;
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

        // Search by semantic similarity
        List<Article> articles = articleRepository.searchBySimilarity(
                embeddingStr,
                request.getThreshold(),
                request.getLimit()
        );

        analytics.setResultCount(articles.size());
        analyticsRepository.save(analytics);

        SearchResultDTO result = new SearchResultDTO();
        result.setArticles(articles.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        result.setTotalResults(articles.size());

        return result;
    }

    public List<String> getPopularQueries(int limit) {
        return analyticsRepository.findPopularQueries().stream()
                .limit(limit)
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
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
        dto.setPublishedDate(article.getPublishedDate() != null ?
                article.getPublishedDate().toString() : null);
        return dto;
    }
}

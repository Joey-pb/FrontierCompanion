package wgu.jbas127.frontiercompanionbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import wgu.jbas127.frontiercompanionbackend.dto.SearchRequest;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.entitiy.SearchAnalytics;
import wgu.jbas127.frontiercompanionbackend.repository.ArticleRepository;
import wgu.jbas127.frontiercompanionbackend.repository.NarrativeRepository;
import wgu.jbas127.frontiercompanionbackend.repository.SearchAnalyticsRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTests {

    @Mock
    ArticleRepository articleRepository;
    @Mock
    NarrativeRepository narrativeRepository;
    @Mock
    SearchAnalyticsRepository analyticsRepository;
    @Mock EmbeddingService embeddingService;

    @InjectMocks
    SearchService searchService;

    @Test
    void search_generatesEmbedding_queriesRepos_savesAnalytics_andMapsDtos() {
        SearchRequest req = new SearchRequest();
        req.setQuery("hello world");
        req.setThreshold(0.75);
        req.setLimit(5);

        when(embeddingService.generateEmbedding("hello world")).thenReturn(new float[]{0.1f, 0.2f});

        Article a1 = new Article(); a1.setId(1L); a1.setTitle("A1");
        Article a2 = new Article(); a2.setId(2L); a2.setTitle("A2");
        Narrative n1 = new Narrative(); n1.setId(100L); n1.setContent("N1");

        when(articleRepository.searchBySimilarity(anyString(), eq(0.75), eq(5))).thenReturn(List.of(a1, a2));
        when(narrativeRepository.searchBySimilarity(anyString(), eq(0.75), eq(5))).thenReturn(List.of(n1));

        ArgumentCaptor<SearchAnalytics> analyticsCaptor = ArgumentCaptor.forClass(SearchAnalytics.class);
        when(analyticsRepository.save(analyticsCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        var result = searchService.search(req);

        verify(embeddingService).generateEmbedding("hello world");
        verify(articleRepository).searchBySimilarity(anyString(), eq(0.75), eq(5));
        verify(narrativeRepository).searchBySimilarity(anyString(), eq(0.75), eq(5));
        verify(analyticsRepository).save(any(SearchAnalytics.class));

        SearchAnalytics savedAnalytics = analyticsCaptor.getValue();
        assertEquals("hello world", savedAnalytics.getQueryText());
        assertEquals(3, savedAnalytics.getResultCount(), "Should log total results = articles + narratives");

        assertNotNull(result);
        assertEquals(2, result.getArticles().size());
        assertEquals(1, result.getNarratives().size());

        // This is the expected behavior: totalResults should count both kinds.
        // If this assertion fails, it's a bug in SearchService.
        assertEquals(3, result.getTotalResults(), "totalResults should equal articles + narratives");
    }
}

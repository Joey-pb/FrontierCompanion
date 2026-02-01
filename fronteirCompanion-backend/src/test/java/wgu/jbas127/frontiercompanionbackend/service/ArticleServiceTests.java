package wgu.jbas127.frontiercompanionbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;
import wgu.jbas127.frontiercompanionbackend.repository.ArticleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTests {

    @Mock ArticleRepository articleRepository;
    @Mock EmbeddingService embeddingService;

    @InjectMocks ArticleService articleService;

    @Test
    void getArticleById_whenMissing_throws() {
        when(articleRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> articleService.getArticleById(42L));
        assertEquals("Article not found", ex.getMessage());
    }

    @Test
    void getAllArticles_mapsAndReturnsDtos() {
        Article a1 = new Article();
        a1.setId(1L);
        a1.setTitle("T1");
        a1.setDescription("D1");
        a1.setUrl("https://example.test/a1");
        a1.setThumbnailUrl("https://example.test/t1.png");
        a1.setAuthor("Author1");
        a1.setSource("Source1");
        a1.setPublishedDate(LocalDate.of(2024, 1, 2));

        Article a2 = new Article();
        a2.setId(2L);
        a2.setTitle("T2");

        when(articleRepository.findAllOrderByPublishedDateDesc()).thenReturn(List.of(a1, a2));

        var dtos = articleService.getAllArticles();

        assertEquals(2, dtos.size());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("T1", dtos.get(0).getTitle());
        assertEquals("2024-01-02", dtos.get(0).getPublishedDate());
        assertEquals(2L, dtos.get(1).getId());
        assertEquals("T2", dtos.get(1).getTitle());
    }

    @Test
    void createArticle_generatesEmbeddingAndSaves() {
        Article input = new Article();
        input.setTitle("Title");
        input.setDescription("Desc");
        input.setContent("Content");

        when(embeddingService.generateEmbedding(anyString())).thenReturn(new float[]{0.1f, 0.2f});
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        Article saved = articleService.createArticle(input);

        assertNotNull(saved.getEmbedding(), "Embedding should be set before save returns");
        verify(embeddingService).generateEmbedding(argThat(s -> s.contains("Title") && s.contains("Desc") && s.contains("Content")));
        verify(articleRepository).save(input);
    }

    @Test
    void updateArticle_updatesFields_regeneratesEmbedding_andSaves() {
        Article existing = new Article();
        existing.setId(10L);
        existing.setTitle("Old");
        existing.setDescription("OldD");
        existing.setContent("OldC");

        Article update = new Article();
        update.setTitle("NewTitle");
        update.setDescription("NewDesc");
        update.setContent("NewContent");
        update.setUrl("https://example.test/new");
        update.setThumbnailUrl("https://example.test/new.png");
        update.setAuthor("NewAuthor");
        update.setSource("NewSource");
        update.setPublishedDate(LocalDate.of(2025, 5, 5));

        when(articleRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(embeddingService.generateEmbedding(anyString())).thenReturn(new float[]{0.3f});
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        Article saved = articleService.updateArticle(10L, update);

        assertEquals("NewTitle", saved.getTitle());
        assertEquals("NewDesc", saved.getDescription());
        assertEquals("NewContent", saved.getContent());
        assertEquals("https://example.test/new", saved.getUrl());
        assertEquals(LocalDate.of(2025, 5, 5), saved.getPublishedDate());
        assertNotNull(saved.getEmbedding());

        verify(embeddingService).generateEmbedding(argThat(s -> s.contains("NewTitle") && s.contains("NewDesc") && s.contains("NewContent")));
        verify(articleRepository).save(existing);
    }
}
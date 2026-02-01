package wgu.jbas127.frontiercompanionbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.repository.NarrativeRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NarrativeServiceTest {

    @Mock
    NarrativeRepository narrativeRepository;
    @Mock
    EmbeddingService embeddingService;

    @InjectMocks
    NarrativeService narrativeService;

    @Test
    void processNarrativeDocument_whenEmptyFile_throws() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "narrative.txt", "text/plain", new byte[0]
        );

        assertThrows(IllegalArgumentException.class,
                () -> narrativeService.processNarrativeDocument(file, 1L, "Section"));
        verifyNoInteractions(narrativeRepository);
    }

    @Test
    void processNarrativeDocument_structuredHeaders_createsChunksPerSection_andSavesAll() throws Exception {
        String text = """
                # Intro
                This is the intro. It has two sentences.
                This is still the intro.
                
                ## Details
                Details sentence one. Details sentence two.
                Details sentence three.
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "narrative.md",
                "text/markdown",
                text.getBytes(StandardCharsets.UTF_8)
        );

        when(embeddingService.generateEmbedding(anyString())).thenReturn(new float[]{0.11f});
        when(narrativeRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Narrative> saved = narrativeService.processNarrativeDocument(file, 99L, "IgnoredWhenStructured");

        assertNotNull(saved);
        assertEquals(2, saved.size(), "Should create 1 chunk per non-empty section for short text");

        assertEquals(99L, saved.get(0).getExhibitId());
        assertNotNull(saved.get(0).getEmbedding());
        assertNotNull(saved.get(1).getEmbedding());

        // verify titles/section names came from headers
        assertEquals("Intro", saved.get(0).getSectionName());
        assertEquals("Details", saved.get(1).getSectionName());

        verify(embeddingService, times(2)).generateEmbedding(anyString());
        verify(narrativeRepository).saveAll(anyList());
    }

    @Test
    void softDeleteByExhibitId_whenNoneFound_throws() {
        when(narrativeRepository.findByExhibitIdAndDeletedFalse(7L)).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> narrativeService.softDeleteByExhibitId(7L));
        assertEquals("No narratives found for exhibit 7", ex.getMessage());
        verify(narrativeRepository, never()).saveAll(anyList());
    }

    @Test
    void softDeleteByExhibitId_marksAllDeleted_andSaves() {
        Narrative n1 = new Narrative();
        n1.setDeleted(false);
        Narrative n2 = new Narrative();
        n2.setDeleted(false);

        when(narrativeRepository.findByExhibitIdAndDeletedFalse(7L)).thenReturn(List.of(n1, n2));

        String msg = narrativeService.softDeleteByExhibitId(7L);

        assertTrue(n1.isDeleted());
        assertTrue(n2.isDeleted());
        assertEquals("2 narrative chunks deleted for exhibit 7", msg);
        verify(narrativeRepository).saveAll(List.of(n1, n2));
    }

    @Test
    void restoreByExhibitId_whenNoneFound_throws() {
        when(narrativeRepository.findByExhibitIdAndDeletedTrue(7L)).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> narrativeService.restoreByExhibitId(7L));
        assertEquals("No deleted narratives found for exhibit 7", ex.getMessage());
        verify(narrativeRepository, never()).saveAll(anyList());
    }

    @Test
    void restoreByExhibitId_marksAllNotDeleted_andSaves() {
        Narrative n1 = new Narrative();
        n1.setDeleted(true);
        Narrative n2 = new Narrative();
        n2.setDeleted(true);

        when(narrativeRepository.findByExhibitIdAndDeletedTrue(7L)).thenReturn(List.of(n1, n2));

        String msg = narrativeService.restoreByExhibitId(7L);

        assertFalse(n1.isDeleted());
        assertFalse(n2.isDeleted());
        assertEquals("2 narrative chunks restored for exhibit 7", msg);
        verify(narrativeRepository).saveAll(List.of(n1, n2));
    }
}

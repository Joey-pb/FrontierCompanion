package wgu.jbas127.frontiercompanionbackend.service;

import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.repository.NarrativeRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing narrative documents, including processing, chunking,
 * embedding generation, and soft deletion/restoration.
 */
@Service
@RequiredArgsConstructor
public class NarrativeService {

    private final NarrativeRepository narrativeRepository;
    private final EmbeddingService embeddingService;

    /**
     * Processes a narrative document (TXT, MD, etc.) and stores it as multiple narrative chunks with embeddings.
     * The document can be structured with markdown-style headers or processed as continuous text.
     *
     * @param file        The {@link MultipartFile} containing the narrative text.
     * @param exhibitId   The ID of the exhibit associated with this narrative.
     * @param sectionName An optional name for the narrative section.
     * @return A list of the saved {@link Narrative} entities.
     * @throws Exception If an error occurs during file reading or processing.
     */
    @Transactional
    public List<Narrative> processNarrativeDocument(MultipartFile file, Long exhibitId, String sectionName)
            throws Exception {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Read file content
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        // Check if content is structured (has sections)
        String fullText = content.toString();

        System.out.println("=== Processing File ===");
        System.out.println("File size: " + fullText.length() + " chars");
        System.out.println("First 200 chars: " + fullText.substring(0, Math.min(200, fullText.length())));

        if (fullText.trim().isEmpty()) {
            throw new IllegalArgumentException("File content is empty");
        }

        List<NarrativeSection> sections = parseStructuredDocument(fullText);

        List<Narrative> narratives = new ArrayList<>();

        if (!sections.isEmpty()) {
            // Process structured document with sections
            for (NarrativeSection section : sections) {
                System.out.println("Processing section: " + section.getTitle());
                System.out.println("  Content length: " + section.getContent().length());

                // Skip empty sections
                if (section.getContent().trim().isEmpty()) {
                    System.out.println("  Skipping empty section");
                    continue;
                }

                List<String> chunks = chunkText(section.getContent(), 800);
                System.out.println("  Chunks: " + chunks.size());

                for (int i = 0; i < chunks.size(); i++) {
                    String chunk = chunks.get(i);

                    // Skip empty chunks
                    if (chunk.trim().isEmpty()) {
                        System.out.println("  Skipping empty chunk " + i);
                        continue;
                    }

                    narratives.add(createNarrative(
                            exhibitId,
                            section.getTitle(),
                            chunk,
                            section.getTitle(),
                            i
                    ));
                }
            }
        } else {
            // Process as single continuous text
            System.out.println("No sections found, processing as continuous text");
            List<String> chunks = chunkText(fullText, 800);
            System.out.println("Chunks: " + chunks.size());

            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);

                if (chunk.trim().isEmpty()) {
                    System.out.println("Skipping empty chunk " + i);
                    continue;
                }

                narratives.add(createNarrative(
                        exhibitId,
                        "Narrative - Part " + (i + 1),
                        chunk,
                        sectionName,
                        i
                ));
            }
        }

        System.out.println("Total narratives created: " + narratives.size());
        return narrativeRepository.saveAll(narratives);
    }

    /**
     * Parses a structured document with markdown-style headers (e.g., lines starting with ## or #).
     *
     * @param text The full text of the document.
     * @return A list of {@link NarrativeSection} objects.
     */
    private List<NarrativeSection> parseStructuredDocument(String text) {
        List<NarrativeSection> sections = new ArrayList<>();
        String[] lines = text.split("\n");

        String currentTitle = null;
        StringBuilder currentContent = new StringBuilder();

        for (String line : lines) {
            // Check for headers
            if (line.trim().startsWith("## ") || line.trim().startsWith("# ")) {
                // Save previous section if it has content
                if (currentTitle != null && currentContent.length() > 0) {
                    String contentStr = currentContent.toString().trim();
                    if (!contentStr.isEmpty()) {
                        sections.add(new NarrativeSection(currentTitle, contentStr));
                    }
                }

                // Start new section
                currentTitle = line.replaceFirst("^\\s*#+\\s*", "").trim();
                currentContent = new StringBuilder();
            } else {
                // Add content to current section (or accumulate before first header)
                currentContent.append(line).append("\n");
            }
        }

        // Save last section if it has content
        if (currentTitle != null && currentContent.length() > 0) {
            String contentStr = currentContent.toString().trim();
            if (!contentStr.isEmpty()) {
                sections.add(new NarrativeSection(currentTitle, contentStr));
            }
        }

        return sections;
    }

    /**
     * Splits text into chunks of approximately chunkSize characters, splitting on sentence boundaries.
     *
     * @param text      The text to chunk.
     * @param chunkSize The target size for each chunk.
     * @return A list of text chunks.
     */
    private List<String> chunkText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();

        if (text == null || text.trim().isEmpty()) {
            return chunks; // Return empty list
        }

        // Split on sentence boundaries
        String[] sentences = text.split("(?<=[.!?])\\s+");

        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            // Skip empty sentences
            if (sentence.trim().isEmpty()) {
                continue;
            }

            // If adding this sentence exceeds chunk size has content, save chunk
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                String chunk = currentChunk.toString().trim();
                if (!chunk.isEmpty()) {
                    chunks.add(chunk);
                }
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }

        // Add final chunk if it has content
        if (currentChunk.length() > 0) {
            String chunk = currentChunk.toString().trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
        }

        return chunks;
    }

    /**
     * Creates a {@link Narrative} entity, generates its embedding, and returns it.
     *
     * @param exhibitId   The ID of the exhibit.
     * @param title       The title for the narrative chunk.
     * @param content     The text content of the chunk.
     * @param sectionName The name of the section.
     * @param chunkIndex  The index of this chunk within the section/document.
     * @return The created {@link Narrative} entity.
     */
    private Narrative createNarrative(Long exhibitId, String title, String content,
                                      String sectionName, int chunkIndex) {
        // Validate content before creating narrative
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot create narrative with empty content");
        }

        System.out.println("Creating narrative: " + title + " (chunk " + chunkIndex + ")");

        Narrative narrative = new Narrative();
        narrative.setExhibitId(exhibitId);
        narrative.setTitle(title);
        narrative.setContent(content);
        narrative.setSectionName(sectionName);
        narrative.setChunkIndex(chunkIndex);

        // Generate embedding
        float[] embedding = embeddingService.generateEmbedding(content);
        narrative.setEmbedding(new PGvector(embedding));

        return narrative;
    }

    /**
     * Retrieves all narratives for a specific exhibit.
     *
     * @param exhibitId The ID of the exhibit.
     * @return A list of {@link Narrative} entities associated with the exhibit.
     */
    @Transactional(readOnly = true)
    public List<Narrative> getNarrativesByExhibit(Long exhibitId) {
        return narrativeRepository.findByExhibitId(exhibitId);
    }

    /**
     * Marks all active narratives for an exhibit as deleted.
     *
     * @param exhibitId The ID of the exhibit.
     * @return A message indicating how many chunks were deleted.
     * @throws RuntimeException If no active narratives are found for the exhibit.
     */
    public String softDeleteByExhibitId(Long exhibitId) {
        List<Narrative> narratives = narrativeRepository.findByExhibitIdAndDeletedFalse(exhibitId);

        if (narratives.isEmpty()) {
            throw new RuntimeException("No narratives found for exhibit " + exhibitId);
        }

        narratives.forEach(narrative -> narrative.setDeleted(true));
        narrativeRepository.saveAll(narratives);

        return narratives.size() + " narrative chunks deleted for exhibit " + exhibitId;
    }

    /**
     * Restores previously soft-deleted narratives for an exhibit.
     *
     * @param exhibitId The ID of the exhibit.
     * @return A message indicating how many chunks were restored.
     * @throws RuntimeException If no deleted narratives are found for the exhibit.
     */
    public String restoreByExhibitId(Long exhibitId) {
        List<Narrative> narratives = narrativeRepository.findByExhibitIdAndDeletedTrue(exhibitId);

        if (narratives.isEmpty()) {
            throw new RuntimeException("No deleted narratives found for exhibit " + exhibitId);
        }

        narratives.forEach(narrative -> narrative.setDeleted(false));
        narrativeRepository.saveAll(narratives);

        return narratives.size() + " narrative chunks restored for exhibit " + exhibitId;
    }

    /**
     * Private helper class for representing sections of a structured narrative document.
     */
    private static class NarrativeSection {
        private final String title;
        private final String content;

        public NarrativeSection(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public String getTitle() { return title; }
        public String getContent() { return content; }
    }
}

package wgu.jbas127.frontiercompanionbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link EmbeddingService} that uses an OpenAI {@link EmbeddingModel}
 * to generate vector embeddings for text.
 */
@Service
@RequiredArgsConstructor
public class OpenAIEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

    /**
     * Generates a single vector embedding for the provided text.
     *
     * @param text The input text to be embedded. Must not be null or empty.
     * @return An array of floats representing the vector embedding.
     * @throws IllegalArgumentException if the text is null or blank.
     */
    @Override
    public float[] generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        return embeddingModel.call(new EmbeddingRequest(List.of(text), null))
                .getResults()
                .get(0)
                .getOutput();
    }

    /**
     * Generates a list of vector embeddings for multiple text inputs in a single batch request.
     *
     * @param texts A list of strings to be embedded.
     * @return A list of float arrays, each representing a vector embedding.
     */
    public List<float[]> generateEmbeddings(List<String> texts) {
        return embeddingModel.call(new EmbeddingRequest(texts, null))
                .getResults()
                .stream()
                .map(Embedding::getOutput)
                .toList();
    }
}

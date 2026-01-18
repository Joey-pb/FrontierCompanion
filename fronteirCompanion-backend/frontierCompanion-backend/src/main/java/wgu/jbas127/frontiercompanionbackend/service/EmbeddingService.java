package wgu.jbas127.frontiercompanionbackend.service;

/**
 * Interface for generating vector embeddings from text.
 */
public interface EmbeddingService {
    /**
     * Generates a vector embedding for the given text.
     *
     * @param text The input text to be embedded.
     * @return An array of floats representing the vector embedding.
     */
    float[] generateEmbedding(String text);
}

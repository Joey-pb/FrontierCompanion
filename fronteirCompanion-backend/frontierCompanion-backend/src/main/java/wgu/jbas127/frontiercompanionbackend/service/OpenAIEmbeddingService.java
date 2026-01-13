package wgu.jbas127.frontiercompanionbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embeddingModel;

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

    public List<float[]> generateEmbeddings(List<String> texts) {
        return embeddingModel.call(new EmbeddingRequest(texts, null))
                .getResults()
                .stream()
                .map(Embedding::getOutput)
                .toList();
    }
}

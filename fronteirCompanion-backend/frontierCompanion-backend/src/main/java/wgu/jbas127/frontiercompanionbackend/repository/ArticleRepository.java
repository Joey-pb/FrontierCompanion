package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;

import java.util.List;

@Repository
public interface ArticleRepository {

    // Semantic search using vector similarity
    @Query(value = """
        SELECT a.*, 1 - (a.embedding <=> CAST(:queryEmbedding AS vector)) AS similarity
        FROM articles a
        WHERE a.embedding IS NOT NULL
            AND 1 - (a.embedding <=> CAST(:queryEmbedding AS vector)) > :threshold
        ORDER BY a.embedding <=> CAST(:queryEmbedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<Article> searchBySimilarity(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );

    // Find similar articles to a given article
    @Query(value = """
        SELECT a.*, 1 - (a.embedding <=> ref.embedding) AS similarity
        FROM articles a,
             (SELECT embedding FROM articles WHERE id = :articleId) ref
        WHERE a.id != :articleId
            AND a.embedding IS NOT NULL
        ORDER BY a.embedding <=> ref.embedding
        LIMIT :limit
        """, nativeQuery = true)
    List<Article> findSimilarArticles(
            @Param("id") Long articleId,
            @Param("limit") int limit
    );

    // Get articles by exhibit ID
    @Query("SELECT a FROM Article a JOIN a.exhibitMappings em " +
            "WHERE em.exhibitId = :exhibitId ORDER BY em.displayOrder ASC")
    List<Article> findByExhibitId(@Param("exhibitId") String exhibitId);

    // Get all articles ordered by date
    @Query("SELECT a FROM Article a ORDER BY a.publishedDate DESC NULLS LAST")
    List<Article> findAllOrderByPublishedDateDesc();
}

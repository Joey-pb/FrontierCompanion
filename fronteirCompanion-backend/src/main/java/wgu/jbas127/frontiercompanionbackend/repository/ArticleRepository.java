package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.Article;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Performs semantic search for articles based on a query embedding.
     * Returns articles with similarity scores above a specified threshold, ordered by similarity.
     *
     * @param queryEmbedding Embedding of the query text
     * @param threshold Minimum similarity threshold
     * @param limit Maximum number of results to return
     * @return List of articles with similarity scores
     */
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

    /**
     * Finds articles similar to a given article, ordered by similarity.
     *
     * @param articleId ID of the article to compare against
     * @param limit Maximum number of results to return
     * @return List of articles with similarity scores
     */
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

    /**
     * Retrieves articles associated with a specific exhibit, ordered by their display order.
     *
     * @param exhibitId ID of the exhibit
     * @return List of articles associated with the exhibit, sorted by display order.
     */
    @Query("SELECT a FROM Article a JOIN a.exhibitMappings em " +
            "WHERE em.exhibitId = :exhibitId ORDER BY em.displayOrder ASC")
    List<Article> findByExhibitId(@Param("exhibitId") Long exhibitId);

    /**
     * Retrieves all articles sorted by their published date in descending order.
     * Articles with null published dates are sorted last.
     *
     * @return List of articles ordered from the most recently published to the least recently published.
     */
    @Query("SELECT a FROM Article a ORDER BY a.publishedDate DESC NULLS LAST")
    List<Article> findAllOrderByPublishedDateDesc();
}

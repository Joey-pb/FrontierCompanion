package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;

import java.util.List;

@Repository
public interface NarrativeRepository extends JpaRepository<Narrative, Long> {

    /**
     * Performs semantic search for narratives based on a query embedding.
     * Returns narratives with similarity scores above a specified threshold, ordered by similarity.
     *
     * @param queryEmbedding Embedding of the query text
     * @param threshold Minimum similarity threshold
     * @param limit Maximum number of results to return
     * @return List of narratives with similarity scores
     */
    @Query(value = """
    SELECT n.*, 1 - (n.embedding <=> CAST(:queryEmbedding AS vector)) AS similarity
    FROM narratives n
    WHERE n.embedding IS NOT NULL
        AND n.deleted = false
        AND 1 - (n.embedding <=> CAST(:queryEmbedding AS vector)) > :threshold
    ORDER BY n.embedding <=> CAST(:queryEmbedding AS vector)
    LIMIT :limit
    """, nativeQuery = true)
    List<Narrative> searchBySimilarity(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );

    /**
     * Retrieves narratives associated with a specific exhibit, ordered by their chunk index.
     *
     * @param exhibitId ID of the exhibit
     * @return List of narratives associated with the exhibit, sorted by chunk index.
     */
    @Query("SELECT n FROM Narrative n WHERE n.exhibitId = :exhibitId ORDER BY n.chunkIndex ASC")
    List<Narrative> findByExhibitId(@Param("exhibitId") Long exhibitId);

    /**
     * Retrieves active narratives associated with a specific exhibit.
     *
     * @param exhibitId ID of the exhibit
     * @return List of active narratives associated with the exhibit.
     */
    List<Narrative> findByExhibitIdAndDeletedFalse(Long exhibitId);

    /**
     * Retrieves deleted narratives associated with a specific exhibit.
     *
     * @param exhibitId ID of the exhibit
     * @return List of deleted narratives associated with the exhibit.
     */
    List<Narrative> findByExhibitIdAndDeletedTrue(Long exhibitId);
}

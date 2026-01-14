package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;

import java.util.List;

@Repository
public interface NarrativeRepository extends JpaRepository<Narrative, Long> {

    // Semantic search for narratives
    @Query(value = """
        SELECT n.*, 1 - (n.embedding <=> CAST(:queryEmbedding AS vector)) AS similarity
        FROM narratives n
        WHERE n.embedding IS NOT NULL
            AND 1 - (n.embedding <=> CAST(:queryEmbedding AS vector)) > :threshold
        ORDER BY n.embedding <=> CAST(:queryEmbedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<Narrative> searchBySimilarity(
            @Param("queryEmbedding") String queryEmbedding,
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );

    // Search narratives for specific exhibit
    @Query(value = """
        SELECT n.*, 1 - (n.embedding <=> CAST(:queryEmbedding AS vector)) AS similarity
        FROM narratives n
        WHERE n.exhibit_id = :exhibitId
            AND n.embedding IS NOT NULL
            AND 1 - (n.embedding <=> CAST(:queryEmbedding AS vector)) > :threshold
        ORDER BY n.embedding <=> CAST(:queryEmbedding AS vector)
        LIMIT :limit
        """, nativeQuery = true)
    List<Narrative> searchByExhibitAndSimilarity(
            @Param("exhibitId") Long exhibitId,
            @Param("queryEmbedding") String queryEmbedding,
            @Param("threshold") double threshold,
            @Param("limit") int limit
    );

    // Get narratives for specific exhibit
    @Query("SELECT n FROM Narrative n WHERE n.exhibitId = :exhibitId ORDER BY n.chunkIndex ASC")
    List<Narrative> findByExhibitId(@Param("exhibitId") Long exhibitId);

    // Get narratives by section
    @Query("SELECT n FROM Narrative n WHERE n.sectionName = :sectionName")
    List<Narrative> findBySectionName(@Param("sectionName") String sectionName);
}

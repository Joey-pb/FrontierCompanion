package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.SearchAnalytics;

import java.util.List;

@Repository
public interface SearchAnalyticsRepository extends JpaRepository<SearchAnalytics, Long> {

    /**
     * Retrieves a list of the most popular search queries along with their occurrence counts.
     * The results are aggregated and ordered by occurrence count in descending order.
     *
     * @return A list of objects where each object is an array containing:
     *         - The query text as a String (index 0)
     *         - The count of occurrences as a Long (index 1)
     */
    @Query("SELECT s.queryText, COUNT(s) as count FROM SearchAnalytics s " +
            "GROUP BY s.queryText ORDER BY count DESC")
    List<Object[]> findPopularQueries();

    /**
     * Retrieves a list of recent search queries, ordered by their ID in descending order.
     *
     * @return A list of SearchAnalytics objects representing the most recent search queries,
     *         sorted in descending order by their ID.
     */
    @Query("SELECT s FROM SearchAnalytics s ORDER BY s.id DESC")
    List<SearchAnalytics> findRecentQueries();
}

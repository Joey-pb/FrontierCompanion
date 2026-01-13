package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.SearchAnalytics;

import java.util.List;

@Repository
public interface SearchAnalyticsRepository extends JpaRepository<SearchAnalytics, Long> {
    @Query("SELECT s.queryText, COUNT(s) as count FROM SearchAnalytics s " +
            "GROUP BY s.queryText ORDER BY count DESC")
    List<Object[]> findPopularQueries();
}

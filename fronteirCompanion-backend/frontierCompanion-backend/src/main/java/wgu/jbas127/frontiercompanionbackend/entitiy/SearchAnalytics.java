package wgu.jbas127.frontiercompanionbackend.entitiy;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "search_analytics")
public class SearchAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "query_text", nullable = false, columnDefinition = "TEXT")
    private String queryText;

    @Column(name = "result_count")
    private int resultCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clicked_article_id")
    private Article clickedArticle;

    @Column(name = "search_timestamp")
    private LocalDateTime searchTimestamp;

    @PrePersist
    protected void onCreate() {
        searchTimestamp = LocalDateTime.now();
    }
}

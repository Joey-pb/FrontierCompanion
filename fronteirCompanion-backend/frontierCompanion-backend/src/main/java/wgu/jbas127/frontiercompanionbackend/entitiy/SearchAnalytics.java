package wgu.jbas127.frontiercompanionbackend.entitiy;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_analytics")
public class SearchAnalytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "query_text", nullable = false, columnDefinition = "TEXT")
    private String queryText;

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

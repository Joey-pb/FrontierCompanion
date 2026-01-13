package wgu.jbas127.frontiercompanionbackend.entitiy;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exhibit_article_mappings")
public class ExhibitArticleMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exhibit_id")
    private Long exhibitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

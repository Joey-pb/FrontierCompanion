package wgu.jbas127.frontiercompanionbackend.entitiy;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import wgu.jbas127.frontiercompanionbackend.config.VectorType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    private String author;

    private String source;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "vector(1536)")
    @Type(VectorType.class)
    private PGvector embedding;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private Set<ExhibitArticleMapping> exhibitMappings = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

package wgu.jbas127.frontiercompanionbackend.entitiy;

import com.pgvector.PGvector;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import wgu.jbas127.frontiercompanionbackend.config.VectorType;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "narratives")
public class Narrative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "exhibit_id")
    private long exhibitId;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "section_name")
    private String sectionName;

    @Column(name = "chunk_index")
    private Integer chunkIndex;

    @Column(columnDefinition = "vector(1536)")
    @Type(VectorType.class)
    private PGvector embedding;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

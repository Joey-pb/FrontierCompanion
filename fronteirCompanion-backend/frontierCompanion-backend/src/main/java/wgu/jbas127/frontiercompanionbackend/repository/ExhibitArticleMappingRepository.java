package wgu.jbas127.frontiercompanionbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wgu.jbas127.frontiercompanionbackend.entitiy.ExhibitArticleMapping;

import java.util.List;

@Repository
public interface ExhibitArticleMappingRepository extends JpaRepository<ExhibitArticleMapping, Long> {

    /**
     * Find all mappings for a given exhibit
     * @param exhibitId ID of the exhibit
     * @return List of mappings ordered by display order
     */
    List<ExhibitArticleMapping> findByExhibitIdOrderByDisplayOrderAsc(Long exhibitId);
}

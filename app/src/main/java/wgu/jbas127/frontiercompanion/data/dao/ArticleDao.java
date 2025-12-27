package wgu.jbas127.frontiercompanion.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.entities.Article;

@Dao
public interface ArticleDao {
    // Filter by isActive - only show active articles
    @Query("SELECT * FROM article WHERE exhibit_id = :exhibitId AND is_active = 1 ORDER BY display_order")
    LiveData<List<Article>> getActiveArticlesForExhibit(long exhibitId);

    @Query("SELECT * FROM article WHERE exhibit_id = :exhibitId AND is_active = 1 ORDER BY display_order")
    List<Article> getActiveArticlesForExhibitSync(long exhibitId);

    // Get ALL articles (including inactive)
    @Query("SELECT * FROM article WHERE exhibit_id = :exhibitId ORDER BY display_order")
    List<Article> getAllArticlesForExhibit(long exhibitId);

    @Query("SELECT * FROM article WHERE id = :id")
    LiveData<List<Article>> getArticleById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Article> articles);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Article article);

    @Update
    void update(Article article);

    @Delete
    void delete(Article article);

    @Query("DELETE FROM article")
    void deleteAll();

    // Search active articles
    @Query("SELECT * FROM article WHERE is_active = 1 AND (" +
            "title LIKE '%' || :query || '%' OR " +
            "description LIKE '%' || :query || '%')")
    List<Article> searchArticles(String query);

    // Soft delete - mark as inactive
    @Query("UPDATE article SET is_active = 0 WHERE id = :id")
    void deactivateArticle(long id);

    // Reactivate article
    @Query("UPDATE article SET is_active = 1 WHERE id = :id")
    void activateArticle(long id);
}
package wgu.jbas127.frontiercompanion.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import wgu.jbas127.frontiercompanion.data.dao.ArticleDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitPanelDao;
import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.models.SearchResult;

public class ExhibitRepository {
    private final ExhibitDao exhibitDao;
    private final ExhibitPanelDao panelDao;
    private final ArticleDao articleDao;

    public ExhibitRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);

        this.exhibitDao = db.exhibitDao();
        this.panelDao = db.ExhibitPanelDao();
        this.articleDao = db.ArticleDao();
    }

    // Exhibits
    public LiveData<List<Exhibit>> getAllExhibits() {
        return exhibitDao.getAllExhibits();
    }

    public LiveData<Exhibit> getExhibit(long id) {
        return exhibitDao.getExhibitById(id);
    }

    // Exhibit panels
    public LiveData<List<ExhibitPanel>> getPanelsForExhibit(long exhibitId) {
        return panelDao.getPanelsForExhibit(exhibitId);
    }

    // Articles (filtered by isActive)
    public LiveData<List<Article>> getActiveArticlesForExhibit(long exhibitId) {
        return articleDao.getActiveArticlesForExhibit(exhibitId);
    }

    // Articles (all articles including inactive)
    public List<Article> getAllArticlesForExhibit(long exhibitId) {
        return articleDao.getAllArticlesForExhibit(exhibitId);
    }

    // Article management
    public void activateArticle(long articleId) {
        new Thread(() -> {
            articleDao.activateArticle(articleId);
        }).start();
    }

    public void deactivateArticle(long articleId) {
        new Thread(() -> {
            articleDao.deactivateArticle(articleId);
        }).start();
    }

    public void updateArticle(Article article) {
        new Thread(() -> {
            articleDao.update(article);
        }).start();
    }

    // Search
    public List<SearchResult> search(String query) {
        // Search exhibits
        List<SearchResult> results = new ArrayList<>();

        List<Exhibit> exhibits = exhibitDao.searchExhibits(query);
        for (Exhibit exhibit: exhibits) {
            results.add(new SearchResult(
                    SearchResult.TYPE_EXHIBIT,
                    exhibit.getId(),
                    exhibit.getName(),
                    exhibit.getDescription(),
                    "Exhibit"
            ));
        }

        // Search exhibit panels
        List<ExhibitPanel> panels = panelDao.searchExhibitPanels(query);

        for (ExhibitPanel panel : panels) {
            exhibitDao.getAllExhibitsSync().stream()
                    .filter(e -> e.getId() == panel.getExhibitId())
                    .findFirst().ifPresent(exhibit -> results.add(new SearchResult(
                            SearchResult.TYPE_PANEL,
                            exhibit.getId(),
                            panel.getTitle(),
                            "From" + exhibit.getName(),
                            "Content"
                    )));

        }

        // Search articles
        List<Article> articles = articleDao.searchArticles(query);

        for (Article article : articles) {
            exhibitDao.getAllExhibitsSync().stream()
                    .filter(e -> e.getId() == article.getExhibitId())
                    .findFirst().ifPresent(exhibit -> results.add(new SearchResult(
                            SearchResult.TYPE_ARTICLE,
                            exhibit.getId(),
                            article.getTitle(),
                            "Article From " + exhibit.getName(),
                            "Article"
                    )));

        }

        return results;
    }
}

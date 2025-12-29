package wgu.jbas127.frontiercompanion;


import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitWithContent;
import wgu.jbas127.frontiercompanion.data.models.SearchResult;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;
import wgu.jbas127.frontiercompanion.util.LiveDataTestUtil;

@RunWith(AndroidJUnit4.class)
public class ExhibitRepositoryTests {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private AppDatabase db;
    private ExhibitRepository repository;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .fallbackToDestructiveMigration(true)
                .allowMainThreadQueries()
                .build();

        repository = new ExhibitRepository(
                db.exhibitDao(),
                db.ExhibitPanelDao(),
                db.ArticleDao()
        );

        populateDbForTesting();
    }

    private void populateDbForTesting() {
        repository.insertExhibit(new Exhibit("Exhibit 1", 38.0, -79.0, "Desc 1", "1700s", "Location 1", "exhibit_1_bg"));
        repository.insertExhibit(new Exhibit("Exhibit 2", 38.1, -79.1, "Desc 2", "1800s", "Location 2", "exhibit_2_bg"));
        repository.insertExhibit(new Exhibit("Exhibit 3", 38.2, -79.2, "Desc 3", "1800s", "Location 3", "exhibit_3_bg"));
        repository.insertExhibit(new Exhibit("Exhibit 4", 38.3, -79.3, "Desc 4", "1700s", "Location 4", "exhibit_4_bg"));
        repository.insertExhibit(new Exhibit("Exhibit 5", 38.4, -79.4, "Desc 5", "1800s", "Location 5", "exhibit_5_bg"));

        repository.insertExhibitPanel(new ExhibitPanel(1, 1, "Panel 1 Content", "TOP", "panel_1_image"));
        repository.insertExhibitPanel(new ExhibitPanel( 1, 2,  "Panel 2 Content", "BOTTOM", "panel_2_image"));
        repository.insertArticle(new Article( 1, "Article 1 Title", "thumb1.jpg", "content.html", 1, true));

    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void getExhibitByIdSync_withValidId_returnsCorrectExhibit() {
        long exhibitId = 1L;

        Exhibit result = repository.getExhibitSync(exhibitId);

        assertNotNull("Repository should return an exhibit for a valid ID", result);
        assertEquals("Returned exhibit ID should match", exhibitId, result.getId());
        assertEquals("Returned exhibit name should be 'Exhibit 1'", "Exhibit 1", result.getName());
    }

    @Test
    public void getExhibitById_withInvalidId_returnsNull() {
        long invalidId = -1L;

        Exhibit result = repository.getExhibitSync(invalidId);

        assertNull("Repository should return null for an invalid ID", result);
    }

    @Test
    public void getAllExhibitsSync_onPrePopulatedDb_returnsCorrectList() {
        List<Exhibit> allExhibits = repository.getAllExhibitsSync();

        assertEquals("Should return all 5 exhibits from the pre-populated database", 5, allExhibits.size());
        assertEquals("First exhibit should be 'Exhibit 1'", "Exhibit 1", allExhibits.getFirst().getName());
    }

    @Test
    public void getExhibitWithContent_withValidId_returnsCorrectContent() throws InterruptedException{
        long exhibitId = 1L;

        LiveData<ExhibitWithContent> liveData = repository.getExhibitWithContent(exhibitId);
        ExhibitWithContent result = LiveDataTestUtil.getValue(liveData);

        assertNotNull("ExhibitWithContent should not be null", result);
        assertNotNull("Exhibit within result should not be null", result.exhibit);
        assertEquals("Exhibit name should be 'Exhibit 1'", "Exhibit 1", result.exhibit.getName());

        assertNotNull("Panels list should not be null", result.panels);
        assertEquals("Should retrieve 2 panels", 2, result.panels.size());

        assertNotNull("Articles list should not be empty", result.articles);
        assertEquals("Should retrieve 1 article", 1, result.articles.size());
    }

    @Test
    public void insert_withValidExhibit_returnsPositiveId() {
        Exhibit exhibit1 = new Exhibit(
                "Exhibit 6",
                38.12345,
                -79.05678,
                "Desc 6",
                "2000s",
                "Location 6",
                "exhibit_6_bg"

        );

        long exhibitId = repository.insertExhibit(exhibit1);

        assertTrue("Inserting a new exhibit should return a row ID greater than 0",exhibitId > 0);
    }

    @Test
    public void search_withValidQuery_returnsMatchingResults() {
        List<SearchResult> results = repository.search("Exhibit 1");

        assertFalse("Results list should not be empty with a valid query", results.isEmpty());
        assertTrue("Should return results containing 'Exhibit 1'", results.stream().anyMatch(r -> r.getName().contains("Exhibit 1")));
    }

    @Test
    public void search_withNonExistentQuery_returnsEmptyList() {
        List<SearchResult> results = repository.search("DoesNotExist");

        assertTrue("Results list should be empty for a query that has no matches", results.isEmpty());
    }

}

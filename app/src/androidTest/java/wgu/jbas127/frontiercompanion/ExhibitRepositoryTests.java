package wgu.jbas127.frontiercompanion;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.models.SearchResult;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

@RunWith(AndroidJUnit4.class)
public class ExhibitRepositoryTests {
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
        repository.insert(new Exhibit("Exhibit 1", 38.0, -79.0, "Desc 1", "1700s", "Location 1"));
        repository.insert(new Exhibit("Exhibit 2", 38.1, -79.1, "Desc 2", "1800s", "Location 2"));
        repository.insert(new Exhibit("Exhibit 3", 38.2, -79.2, "Desc 3", "1800s", "Location 3"));
        repository.insert(new Exhibit("Exhibit 4", 38.3, -79.3, "Desc 4", "1700s", "Location 4"));
        repository.insert(new Exhibit("Exhibit 5", 38.4, -79.4, "Desc 5", "1800s", "Location 5"));
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
    public void insert_withValidExhibit_returnsPositiveId() {
        Exhibit exhibit1 = new Exhibit(
                "Exhibit 6",
                38.12345,
                -79.05678,
                "Desc 6",
                "2000s",
                "Location 6"

        );

        long exhibitId = repository.insert(exhibit1);

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

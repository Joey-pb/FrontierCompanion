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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import wgu.jbas127.frontiercompanion.data.dao.ArticleDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitPanelDao;
import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;

@RunWith(AndroidJUnit4.class)
public class ExhibitDaoTests {
    private AppDatabase db;
    private ExhibitDao exhibitDao;

    @Before
    public void createDb() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .fallbackToDestructiveMigration(true)
                .allowMainThreadQueries()
                .build();

        exhibitDao = db.exhibitDao();

        populateDbForTesting();
    }

    private void populateDbForTesting() {
        exhibitDao.insert(new Exhibit("Exhibit 1", 38.0, -79.0, "Desc 1", "1700s", "Location 1"));
        exhibitDao.insert(new Exhibit("Exhibit 2", 38.1, -79.1, "Desc 2", "1800s", "Location 2"));
        exhibitDao.insert(new Exhibit("Exhibit 3", 38.2, -79.2, "Desc 3", "1800s", "Location 3"));
        exhibitDao.insert(new Exhibit("Exhibit 4", 38.3, -79.3, "Desc 4", "1700s", "Location 4"));
        exhibitDao.insert(new Exhibit("Exhibit 5", 38.4, -79.4, "Desc 5", "1800s", "Location 5"));
    }


    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void getAllExhibitsSync_onPopulatedDb_returnsNotNull() {
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();
        assertNotNull("List should not be empty", exhibits);
    }

    @Test
    public void getAllExhibitsSync_onPopulatedDb_returnsCorrectSize() {
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();
        assertEquals("Exhibit list size should be 5",5, exhibits.size());
    }

    @Test
    public void getAllExhibitsSync_onPopulatedDb_returnsCorrectFirstItem() {
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();
        assertFalse(exhibits.isEmpty());
        assertEquals("First exhibit should be Exhibit 1", "Exhibit 1", exhibits.get(0).getName());
    }

    @Test
    public void getExhibitByIdSync_withValidId_returnsCorrectExhibit() {
        List<Exhibit> allExhibits = exhibitDao.getAllExhibitsSync();
        assertFalse("Pre-populated database should not be empty", allExhibits.isEmpty());
        Exhibit firstExhibit = allExhibits.get(0);
        long exhibitId = firstExhibit.getId();

        Exhibit result = exhibitDao.getExhibitByIdSync(exhibitId);

        assertNotNull("Exhibit should not be null for a valid ID", result);
        assertEquals("Returned exhibit ID should match the requested ID", exhibitId, result.getId());
        assertEquals("Returned exhibit name should be correct", "Exhibit 1", result.getName());
    }

    @Test
    public void getExhibitById_withInvalidId_returnsNull() {
        long invalidId = -1L;

        Exhibit result = exhibitDao.getExhibitById(invalidId).getValue();

        assertNull("Exhibit should be null for an invalid ID", result);
    }

    @Test
    public void insert_withNewExhibit_increaseRowCount() {
        Exhibit testExhibit = new Exhibit(
                "Exhibit 6",
                38.12345,
                -79.05678,
                "Desc 6",
                "2000s",
                "Location 6"
        );

        long testExhibitId = exhibitDao.insert(testExhibit);
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();

        assertTrue("Insert should return a positive ID", testExhibitId > 0);
        assertEquals("Exhibit list size should be a 6 after insertion", 6, exhibits.size());
    }

    @Test
    public void update_withExistingExhibit_modifiesDataCorrectly() {
        List<Exhibit> allExhibits = exhibitDao.getAllExhibitsSync();
        Exhibit exhibitToUpdate = allExhibits.get(0);
        String originalName = exhibitToUpdate.getName();
        String newName = "Updated Exhibit 1";
        exhibitToUpdate.setName(newName);

        int rowsAffected = exhibitDao.update(exhibitToUpdate);
        Exhibit updatedExhibit = exhibitDao.getExhibitByIdSync(exhibitToUpdate.getId());

        assertEquals("Update should affect exactly one row", 1, rowsAffected);
        assertNotNull(updatedExhibit);
        assertEquals("Exhibit name should be updated in the database", newName, updatedExhibit.getName());
        assertNotEquals("New name should not equal the original name", originalName, updatedExhibit.getName());
    }

    @Test
    public void delete_withExistingExhibit_removesItemFromDb() {
        List<Exhibit> initialExhibits = exhibitDao.getAllExhibitsSync();
        int initialSize = initialExhibits.size();
        Exhibit exhibitToDelete = initialExhibits.getLast();

        int rowsAffected = exhibitDao.delete(exhibitToDelete);
        List<Exhibit> finalExhibits = exhibitDao.getAllExhibitsSync();
        Exhibit result = exhibitDao.getExhibitByIdSync(exhibitToDelete.getId());

        assertEquals("Delete should affect exactly 1 row", 1, rowsAffected);
        assertEquals("Database size should decrease by 1", initialSize - 1, finalExhibits.size());
        assertNull("Deleted exhibit should not be findable by its ID", result);
    }
}

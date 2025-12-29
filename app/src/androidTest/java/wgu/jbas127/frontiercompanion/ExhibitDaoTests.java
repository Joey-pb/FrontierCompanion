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

import java.io.IOException;
import java.util.List;

import wgu.jbas127.frontiercompanion.data.dao.ArticleDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitPanelDao;
import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitWithContent;
import wgu.jbas127.frontiercompanion.util.LiveDataTestUtil;

@RunWith(AndroidJUnit4.class)
public class ExhibitDaoTests {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private AppDatabase db;
    private ExhibitDao exhibitDao;
    private ExhibitPanelDao panelDao;
    private ArticleDao articleDao;

    @Before
    public void createDb(){
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .fallbackToDestructiveMigration(true)
                .allowMainThreadQueries()
                .build();

        exhibitDao = db.exhibitDao();
        panelDao = db.ExhibitPanelDao();
        articleDao = db.ArticleDao();

        populateDbForTesting();
    }

    private void populateDbForTesting() {
        // Exhibit 1
        exhibitDao.insert(new Exhibit("Exhibit 1", 38.0, -79.0, "Desc 1", "1700s", "Location 1", "exhibit_1_bg"));
        panelDao.insert(new ExhibitPanel(1, 1, "Panel 1 Content", "TOP", "panel_1_image"));
        panelDao.insert(new ExhibitPanel(1, 2, "Panel 2 Content", "BOTTOM", "panel_2_image"));
        articleDao.insert(new Article(1, "Article Title", "thumb1.jpg", "content.html", 1, true));


        // Exhibit 2
        exhibitDao.insert(new Exhibit("Exhibit 2", 38.1, -79.1, "Desc 2", "1800s", "Location 2", "exhibit_2_bg"));
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
        assertEquals("Exhibit list size should be 2",2, exhibits.size());
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
    public void getExhibitByIdSync_withInvalidId_returnsNull(){
        long invalidId = -1L;

        Exhibit result = exhibitDao.getExhibitByIdSync(invalidId);

        assertNull("Exhibit should be null for an invalid ID", result);
    }

    @Test
    public void getExhibitWithContent_whenDataExists_returnsObjectWithChildren() throws InterruptedException {
        long exhibitId = 1L;

        LiveData<ExhibitWithContent> liveData = exhibitDao.getExhibitWithContent(exhibitId);
        ExhibitWithContent result = LiveDataTestUtil.getValue(liveData);

        assertNotNull("ExhibitWithContent object should not be null", result);
        assertNotNull("Exhibit within the result should not be null", result.exhibit);
        assertEquals("Exhibit name should match", "Exhibit 1", result.exhibit.getName());

        assertNotNull("Panels should not be null", result.panels);
        assertEquals("Should have 2 panels", 2, result.panels.size());

        assertNotNull("Articles list should not be null", result.articles);
        assertEquals("Should have 1 article", 1, result.articles.size());
    }

    @Test
    public void insert_withNewExhibit_increaseRowCount() {
        Exhibit testExhibit = new Exhibit(
                "Exhibit 3",
                38.12345,
                -79.05678,
                "Desc 3",
                "2000s",
                "Location 3",
                "exhibit_3_bg"
        );

        long testExhibitId = exhibitDao.insert(testExhibit);
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();

        assertTrue("Insert should return a positive ID", testExhibitId > 0);
        assertEquals("Exhibit list size should be a 3 after insertion", 3, exhibits.size());
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

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
public class RoomDbTests {
    private AppDatabase db;
    private ExhibitDao exhibitDao;
    private ExhibitPanelDao panelDao;
    private ArticleDao articleDao;

    @Before
    public void createDb() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.databaseBuilder(context, AppDatabase.class, "Test_Database")
                .createFromAsset("database/prepopulated.db")
                .fallbackToDestructiveMigration(true)
                .allowMainThreadQueries()
                .build();

        exhibitDao = db.exhibitDao();
        panelDao = db.ExhibitPanelDao();
        articleDao = db.ArticleDao();
    }


    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testAssetFileExists() throws IOException {
        Context context = ApplicationProvider.getApplicationContext();

        try {
            InputStream inputStream = context.getAssets().open("database/prepopulated.db");
            assertNotNull("Asset file should exist", inputStream);
            Log.d("DatabaseTest", "Asset file found. Size: " + inputStream.available() + " bytes");
            inputStream.close();
        } catch (IOException e) {
            fail("Asset file not found: " + e.getMessage());
        }
    }

    @Test
    public void testExhibitDataIsNotNull() {
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();
        assertNotNull(exhibits);
    }

    @Test
    public void testExhibitSizeEqualsFive() {
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();
        assertEquals(5, exhibits.size());
    }

    @Test
    public void testFirstExhibitNameIsIrishForge() {
        List<Exhibit> exhibits = exhibitDao.getAllExhibitsSync();
        assertFalse(exhibits.isEmpty());
        assertEquals("Irish Forge", exhibits.get(0).getName());

    }
}

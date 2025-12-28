package wgu.jbas127.frontiercompanion;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import wgu.jbas127.frontiercompanion.data.database.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class AppDatabaseTests {

    @Test
    public void assetDatabase_onBuild_existsAndIsReadable() {
        Context context = ApplicationProvider.getApplicationContext();
        try (InputStream inputStream = context.getAssets().open("database/prepopulated.db")) {
            Assert.assertNotNull("Asset file 'prepopulated.db' should exist", inputStream);
            Assert.assertTrue("Asset file should not be empty", inputStream.available() > 0);
        } catch (IOException e) {
            fail("Asset file 'database/prepopulated.db' not found or could not be read: " + e.getMessage());
        }
    }

    @Test
    public void createFromAsset_onBuild_buildsDatabaseSuccessfully() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase db = null;
        try {
            db = Room.databaseBuilder(context, AppDatabase.class, "Test_Asset_Db")
                    .createFromAsset("database/prepopulated.db")
                    .allowMainThreadQueries()
                    .build();
            Assert.assertNotNull("Database should be successfully created from asset", db);

            Assert.assertFalse("Exhibit list should not be empty after loading from asset",
                    db.exhibitDao().getAllExhibitsSync().isEmpty());

        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}

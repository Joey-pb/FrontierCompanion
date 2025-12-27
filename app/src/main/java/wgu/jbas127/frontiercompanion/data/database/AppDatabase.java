package wgu.jbas127.frontiercompanion.data.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wgu.jbas127.frontiercompanion.data.dao.ArticleDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitDao;
import wgu.jbas127.frontiercompanion.data.dao.ExhibitPanelDao;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;

@Database(
        entities = {Exhibit.class, ExhibitPanel.class, Article.class},
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ExhibitDao exhibitDao();
    public abstract ExhibitPanelDao ExhibitPanelDao();
    public abstract ArticleDao ArticleDao();

    public static synchronized AppDatabase getInstance(Context context){
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "museum_database"
            ).addCallback(new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    Executors.newSingleThreadExecutor().execute(() -> {
                            populateDatabase(context.getApplicationContext());
                        });
                }
            }).build();
        }
        return instance;
    }

    private static void populateDatabase(Context context) {
        AppDatabase db = getInstance(context);

        try {
            // Load exhibits
            String exhibitsJson = loadJSONFromAsset(context, "data/exhibits.json");
            Gson gson = new Gson();
            Type exhibitListType = new TypeToken<List<Exhibit>>(){}.getType();
            List<Exhibit> exhibits = gson.fromJson(exhibitsJson, exhibitListType);
            db.exhibitDao().insertAll(exhibits);
            Log.d("Database", "Loaded " + exhibits.size() + " exhibits");

        } catch (Exception e) {
            Log.e("Database", "Error populating database", e);
        }
    }

    private static String loadJSONFromAsset(Context context, String filename) {
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e("Database", "Error loading JSON from assets", e);
        }

        return null;
    }
}

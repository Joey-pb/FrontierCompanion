package wgu.jbas127.frontiercompanion.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
            ).createFromAsset("database/prepopulated.db")
            .build();
        }
        return instance;
    }
}

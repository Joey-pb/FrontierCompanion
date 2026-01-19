package wgu.jbas127.frontiercompanion;

import android.app.Application;

import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

public class FrontierCompanionApplication extends Application {
    public AppDatabase db;
    public ExhibitRepository exhibitRepository;

    private static FrontierCompanionApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        db = AppDatabase.getInstance(this);
        exhibitRepository = new ExhibitRepository(this);
        instance = this;
    }

    public static FrontierCompanionApplication getInstance() {
        return instance;
    }
}

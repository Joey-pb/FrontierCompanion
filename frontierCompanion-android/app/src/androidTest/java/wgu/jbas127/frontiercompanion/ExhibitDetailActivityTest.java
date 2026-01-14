package wgu.jbas127.frontiercompanion;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;

import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule; // <-- Import the Rule
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule; // <-- Import the Rule
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import wgu.jbas127.frontiercompanion.data.database.AppDatabase;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

@RunWith(AndroidJUnit4.class)
public class ExhibitDetailActivityTest {

    private AppDatabase testDb;
    private FrontierCompanionApplication app;

    // --- FIX 1: ADD THE RULE ---
    // This rule will launch the activity defined in the intent before each test runs.
    @Rule
    public ActivityScenarioRule<ExhibitDetailActivity> activityRule;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();

        // 1. Create a fresh in-memory database
        testDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        // 2. Populate the test database
        populateDb(testDb);

        // 3. Create a repository that uses our test database
        ExhibitRepository testRepository = new ExhibitRepository(
                testDb.exhibitDao(),
                testDb.exhibitPanelDao(),
                testDb.articleDao()
        );

        // 4. Inject our test repository into the application
        app = (FrontierCompanionApplication) InstrumentationRegistry.getInstrumentation()
                .getTargetContext().getApplicationContext();
        app.exhibitRepository = testRepository;

        // 5. Create the Intent and initialize the rule with it
        Intent intent = new Intent(app, ExhibitDetailActivity.class)
                .putExtra(ExhibitDetailActivity.EXTRA_EXHIBIT_ID, 1L); // Test with Exhibit ID 1
        activityRule = new ActivityScenarioRule<>(intent);
    }

    private void populateDb(AppDatabase db) {
        // Exhibit 1 with two panels and one article
        // --- FIX 2: PROVIDE IDs to link entities correctly ---
        db.exhibitDao().insert(new Exhibit( "Irish Forge", 38.0, -79.0, "Desc", "1700s", "Location", "irish_forge_bg"));
        db.exhibitPanelDao().insert(new ExhibitPanel(1L, 1, "This is Panel 1", "CENTER", "panel1_bg"));
        db.exhibitPanelDao().insert(new ExhibitPanel(1L, 2, "This is Panel 2", "LOWER_THIRD", "panel2_bg"));
        db.articleDao().insert(new Article(1L, "Article About The Forge", "thumb.jpg", "content.html", 1, true));
    }

    @After
    public void tearDown() throws IOException {
        testDb.close();
    }

    // You no longer need the launchActivityWithExhibit() method as the Rule handles it.

    @Test
    public void onLaunch_displaysTitlePanelCorrectly() {
        // --- ARRANGE & ACT ---
        // The activity is already launched by the @Rule

        // --- ASSERT ---
        // Check that the title from the Exhibit entity is displayed.
        onView(withText("Irish Forge")).check(matches(isDisplayed()));

        // Check that buttons on the title slide are displayed.
        onView(ViewMatchers.withId(R.id.button_locate_map)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.button_create_route)).check(matches(isDisplayed()));
    }

    @Test
    public void onSwipeUp_displaysContentPanelCorrectly() {
        // --- ACT ---
        // Find the ViewPager2 and perform a swipe up gesture.
        onView(ViewMatchers.withId(R.id.view_pager_panels))
                .perform(ViewActions.swipeUp());

        // --- ASSERT ---
        // After swiping, check for the unique content of the second slide.
        onView(withText("This is Panel 1")).check(matches(isDisplayed()));
    }

    @Test
    public void onSwipeToEnd_displaysActionPanelCorrectly() {
        // --- ACT ---
        // Find the ViewPager2 and perform a chain of swipes to get to the end.
        onView(ViewMatchers.withId(R.id.view_pager_panels))
                .perform(ViewActions.swipeUp()) // Swipe to Panel 1
                .perform(ViewActions.swipeUp()) // Swipe to Panel 2
                .perform(ViewActions.swipeUp()); // Swipe to final Action Panel

        // --- ASSERT ---
        // Check if the "Related Articles" title is now displayed.
        onView(withText("Related Articles")).check(matches(isDisplayed()));

        // Check if a button unique to the action panel is displayed.
        onView(ViewMatchers.withId(R.id.button_back_to_top)).check(matches(isDisplayed()));

        // Check if the article from our test data is visible in the inner RecyclerView.
        onView(withText("Article About The Forge")).check(matches(isDisplayed()));
    }
}

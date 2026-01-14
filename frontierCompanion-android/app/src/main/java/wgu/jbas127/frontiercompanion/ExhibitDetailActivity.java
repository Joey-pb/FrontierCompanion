package wgu.jbas127.frontiercompanion;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;
import wgu.jbas127.frontiercompanion.ui.models.DisplayableItem;
import wgu.jbas127.frontiercompanion.ui.viewmodel.ExhibitDetailViewModel;
import wgu.jbas127.frontiercompanion.ui.adapters.PanelAdapter;
import wgu.jbas127.frontiercompanion.ui.viewmodel.ViewModelFactory;

public class ExhibitDetailActivity extends AppCompatActivity implements PanelAdapter.OnActionPanelClickListener{
    public static final String EXTRA_EXHIBIT_ID = "exhibit_id";

    private ExhibitDetailViewModel viewModel;
    private ViewPager2 viewPager;
    private PanelAdapter panelAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set view
        setContentView(R.layout.activity_exhibit_detail);

        // Initialize views
        viewPager = findViewById(R.id.view_pager_panels);
        progressBar = findViewById(R.id.progress_bar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get dependencies and ViewModel
        ExhibitRepository repository = ((FrontierCompanionApplication) getApplication()).exhibitRepository;
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(ExhibitDetailViewModel.class);

        // Setup ViewPager and adapter
        panelAdapter = new PanelAdapter(this);
        viewPager.setAdapter(panelAdapter);

        // Apply page transformer
        // viewPager.setPageTransformer(new OverlappingPageTransformer());

        // Observe LiveData
        observeViewModel();

        // Trigger data load
        long exhibitId = getIntent().getLongExtra(EXTRA_EXHIBIT_ID, -1);
        if (exhibitId != -1) {
            viewModel.loadExhibitData(exhibitId);
        } else {
            Toast.makeText(this, "Error: Exhibit ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? VISIBLE : GONE);
        });

        viewModel.selectedExhibit.observe(this, exhibitWithContent -> {
            if (exhibitWithContent != null) {
                List<DisplayableItem> displayList = new ArrayList<>();

                displayList.add(new DisplayableItem.TitleItem(exhibitWithContent.exhibit));

                if (exhibitWithContent.panels != null) {
                    for (ExhibitPanel panel : exhibitWithContent.panels) {
                        displayList.add(new DisplayableItem.PanelItem(panel));
                    }
                }

                if (exhibitWithContent.articles != null && !exhibitWithContent.articles.isEmpty()) {
                    displayList.add(new DisplayableItem.ActionItem(
                            exhibitWithContent.articles,
                            exhibitWithContent.exhibit) {
                    });
                }

                panelAdapter.submitList(displayList);
            }
        });
    }

    // CLick listeners
    @Override
    public void onShowOnMapClicked(long exhibitId) {
        // TODO: Implement logic to open a map activity with the exhibit's coordinates
        Toast.makeText(
                this,
                "Show on map clicked for exhibit: " + exhibitId,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onCreateRouteClicked(long exhibitId) {
        // TODO: Implement logic to start a Google Maps navigation intent
        Toast.makeText(
                this,
                "Create route clicked for exhibit: " + exhibitId,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onBackToTopClicked() {
        viewPager.setCurrentItem(0, true);
    }

    @Override
    public void onArticleClicked(Article article) {
        // TODO: Implement logic to open a new activity to show the full article
        Toast.makeText(
                this,
                "Article Clicked: " + article.getTitle(),
                Toast.LENGTH_SHORT)
                .show();
    }
}
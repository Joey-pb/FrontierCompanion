package wgu.jbas127.frontiercompanion.ui.fragments;

import static androidx.lifecycle.AndroidViewModel_androidKt.getApplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wgu.jbas127.frontiercompanion.FrontierCompanionApplication;
import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;
import wgu.jbas127.frontiercompanion.ui.adapters.PanelAdapter;
import wgu.jbas127.frontiercompanion.ui.models.DisplayableItem;
import wgu.jbas127.frontiercompanion.ui.viewmodel.ExhibitDetailViewModel;
import wgu.jbas127.frontiercompanion.ui.viewmodel.SharedViewModel;
import wgu.jbas127.frontiercompanion.ui.viewmodel.ViewModelFactory;

public class ExhibitDetailsFragment extends Fragment implements PanelAdapter.OnActionPanelClickListener{

    private ViewPager2 viewPager;
    private PanelAdapter panelAdapter;
    private ExhibitDetailViewModel viewModel;
    private SharedViewModel sharedViewModel;
    private ProgressBar progressBar;
    private long exhibitId = -1;

    public ExhibitDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exhibitId = ExhibitDetailsFragmentArgs.fromBundle(getArguments()).getExhibitId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exhibit_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        viewPager = view.findViewById(R.id.view_pager_panels);
        panelAdapter = new PanelAdapter(this);
        viewPager.setAdapter(panelAdapter);

        ExhibitRepository repository = ((FrontierCompanionApplication) requireActivity().getApplication()).exhibitRepository;
        ViewModelFactory factory = new ViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(ExhibitDetailViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        setupObservers();

        if (exhibitId != -1) {
            viewModel.loadExhibitData(exhibitId);
        }
    }

    private void setupObservers() {
        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            }
        });

        viewModel.selectedExhibit.observe(getViewLifecycleOwner(), exhibitWithContent -> {
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

    @Override
    public void onShowOnMapClicked(long exhibitId) {
        // TODO: Implement logic to open a map activity with the exhibit's coordinates
        Exhibit exhibit = viewModel.selectedExhibit.getValue().exhibit;
        if (exhibit != null) {
            // Use the SharedViewModel to make the request
            sharedViewModel.requestShowOnMap(exhibit);
            // Navigate back to the previous screen (which should be the map)
            NavHostFragment.findNavController(this).popBackStack();
        } else {
            Toast.makeText(getContext(), "Could not find exhibit.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateRouteClicked(long exhibitId) {
        // TODO: Implement logic to start a Google Maps navigation intent
    }

    @Override
    public void onBackToTopClicked() {
        viewPager.setCurrentItem(0, true);
    }

    @Override
    public void onArticleClicked(Article article) {
        // TODO: Implement logic to open a new activity to show the full article
    }
}
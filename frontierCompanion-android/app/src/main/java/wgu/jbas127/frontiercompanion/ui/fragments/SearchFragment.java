package wgu.jbas127.frontiercompanion.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.List;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.data.models.NarrativeDTO;
import wgu.jbas127.frontiercompanion.ui.adapters.SearchResultsAdapter;
import wgu.jbas127.frontiercompanion.ui.viewmodel.SearchViewModel;

public class SearchFragment extends Fragment {
    private SearchViewModel searchViewModel;
    private SearchResultsAdapter searchAdapter;

    // UI Components
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar loadingIndicator;
    private TextView emptyStateText;

    private FirebaseAnalytics firebaseAnalytics;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());

        // Initialize ViewModel and UI components
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        initViews(view);

        // Setup the RecyclerView and its adapter
        setupRecyclerView();

        // Setup listeners for UI components
        setupSearchListener();

        // Observe LiveData from the ViewModel
        setupObservers();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.search_results_recycler_view);
        searchView = view.findViewById(R.id.search_view);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        emptyStateText = view.findViewById(R.id.empty_state_text);
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchResultsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(searchAdapter);
    }

    private void setupSearchListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    // Log search query
                    Bundle bundle = new Bundle();
                    firebaseAnalytics.logEvent("search_requested", bundle);

                    searchViewModel.search(query.trim());
                    searchView.clearFocus(); // Hide keyboard after search
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // We can choose to search as the user types, but for now, we'll only search on submit.
                // If the user clears the text, clear the results.
                if (newText.isEmpty()) {
                    searchAdapter.submitList(null);
                    emptyStateText.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void setupObservers() {
        // Observer for search results
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), (List<NarrativeDTO> narratives) -> {
            boolean hasResults = narratives != null && !narratives.isEmpty();
            searchAdapter.submitList(narratives);

            // Only show empty state if not loading and list is empty/null
            if (searchViewModel.getIsLoading().getValue() != null && !searchViewModel.getIsLoading().getValue()) {
                emptyStateText.setVisibility(hasResults ? View.GONE : View.VISIBLE);
            }
            recyclerView.setVisibility(hasResults ? View.VISIBLE : View.GONE);
        });

        // Observer for loading state
        searchViewModel.getIsLoading().observe(getViewLifecycleOwner(), (Boolean isLoading) -> {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            // Hide other views while loading
            if (isLoading) {
                recyclerView.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.GONE);
            }
        });

        // Observer for network errors
        searchViewModel.getNetworkError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                // Log Network Errors
                Bundle bundle = new Bundle();
                bundle.putString("err_msg", errorMsg);
                firebaseAnalytics.logEvent("network_error", bundle);

                Toast.makeText(getContext(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                // Optionally show the error in the empty state TextView
                emptyStateText.setText("An error occurred: " + errorMsg);
                emptyStateText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}
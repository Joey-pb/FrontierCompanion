package wgu.jbas127.frontiercompanion.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.ui.viewmodel.SearchViewModel;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragmentTest"; // Tag for logging

    private SearchViewModel searchViewModel;

    // UI components for testing
    private SearchView searchView;
    private Button testButton;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        testButton = view.findViewById(R.id.test_search_button);
        searchView = view.findViewById(R.id.search_view);

        testButton.setOnClickListener(v -> {
            String query = "blacksmith"; // Hardcoded query for the test
            Log.d(TAG, "Initiating test search for query: '" + query + "'");
            Toast.makeText(getContext(), "Running test for: " + query, Toast.LENGTH_SHORT).show();

            searchViewModel.search(query);
        });

        setupObservers();
    }

    private void setupObservers() {
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), narratives -> {
            if (narratives != null) {
                Log.i(TAG, "SUCCESS: Search results received. Count: " + narratives.size());
                if (!narratives.isEmpty()) {
                    Log.d(TAG, "First result - Section: " + narratives.get(0).getSectionName());
                    Log.d(TAG, "First result - Content: " + narratives.get(0).getContent());
                }
                Toast.makeText(getContext(), "Success! " + narratives.size() + " results found.", Toast.LENGTH_LONG).show();
            }
        });

        searchViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                Log.d(TAG, "LOADING: API call in progress...");
                Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "LOADING: API call finished.");
            }
        });

        searchViewModel.getNetworkError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Log.e(TAG, "FAILURE: " + errorMsg);
                Toast.makeText(getContext(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
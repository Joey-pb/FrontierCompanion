package wgu.jbas127.frontiercompanion.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.models.NarrativeDTO;
import wgu.jbas127.frontiercompanion.data.repository.SearchRepository;

public class SearchViewModel extends AndroidViewModel {

    public final SearchRepository searchRepository;

    public final LiveData<List<NarrativeDTO>> searchResults;
    public final LiveData<String> networkError;
    public final LiveData<Boolean> isLoading;
    public SearchViewModel(@NonNull Application application) {
        super(application);

        this.searchRepository = SearchRepository.getInstance();
        this.searchResults = searchRepository.getSearchResults();
        this.networkError = searchRepository.getNetworkError();
        this.isLoading = searchRepository.getIsLoading();
    }

    public LiveData<List<NarrativeDTO>> getSearchResults() {
        return searchResults;
    }

    public LiveData<String> getNetworkError() {
        return networkError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void search(String query) {
        searchRepository.search(query);
    }
}

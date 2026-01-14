package wgu.jbas127.frontiercompanion.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wgu.jbas127.frontiercompanion.data.models.ApiResponseDTO;
import wgu.jbas127.frontiercompanion.data.models.NarrativeDTO;
import wgu.jbas127.frontiercompanion.data.models.SearchResult;
import wgu.jbas127.frontiercompanion.network.ApiService;
import wgu.jbas127.frontiercompanion.network.RetrofitClient;

public class SearchRepository {

    private static volatile SearchRepository instance;
    private final ApiService apiService;

    private final MutableLiveData<List<NarrativeDTO>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<String> networkError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private SearchRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public static synchronized SearchRepository getInstance() {
        if (instance == null) {
            instance = new SearchRepository();
        }

        return instance;
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
        isLoading.setValue(true);

        apiService.searchItems(query).enqueue(new Callback<ApiResponseDTO>() {
            @Override
            public void onResponse(Call<ApiResponseDTO> call, Response<ApiResponseDTO> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    searchResults.postValue(response.body().getNarratives());
                    networkError.postValue(null);
                } else {
                    networkError.postValue("API Error " + response.code());
                    searchResults.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDTO> call, Throwable t) {
                isLoading.setValue(false);

                networkError.postValue("Network Failure: " + t.getMessage());
                searchResults.postValue(null);
            }
        });
    }
}

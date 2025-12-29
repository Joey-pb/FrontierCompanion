package wgu.jbas127.frontiercompanion.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

public class ExhibitDetailViewModel extends ViewModel {
    private final ExhibitRepository exhibitRepository;

    // -- INPUT --
    private final MutableLiveData<Long> exhibitId = new MutableLiveData<>();

    // -- STATE --
    private final MediatorLiveData<Boolean> _isLoading = new MediatorLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    // -- OUTPUT --
    public final LiveData<Exhibit> selectedExhibit;
    public final LiveData<List<ExhibitPanel>> panelList;
    public final LiveData<List<Article>> articleList;

    public ExhibitDetailViewModel(ExhibitRepository exhibitRepository) {
        this.exhibitRepository = exhibitRepository;

        selectedExhibit = Transformations.switchMap(exhibitId, exhibitRepository::getExhibit);
        panelList = Transformations.switchMap(exhibitId, exhibitRepository::getPanelsForExhibit);
        articleList = Transformations.switchMap(exhibitId, exhibitRepository::getActiveArticlesForExhibit);
    }

    public void loadExhibitData(long id) {
        if (exhibitId.getValue() != null && exhibitId.getValue() == id) {
            return;
        }

        _isLoading.setValue(true);
        _isLoading.addSource(selectedExhibit, exhibit -> checkLoadingStatus());
        _isLoading.addSource(panelList, exhibitPanels -> checkLoadingStatus());
        _isLoading.addSource(articleList, articles -> checkLoadingStatus());

        exhibitId.setValue(id);
    }

    private void checkLoadingStatus() {
        if (selectedExhibit.getValue() != null && panelList.getValue() != null && articleList.getValue() != null) {
            _isLoading.setValue(false);

            _isLoading.removeSource(selectedExhibit);
            _isLoading.removeSource(panelList);
            _isLoading.removeSource(articleList);
        }
    }

}

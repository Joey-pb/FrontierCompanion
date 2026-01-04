package wgu.jbas127.frontiercompanion.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import wgu.jbas127.frontiercompanion.data.entities.ExhibitWithContent;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

public class ExhibitDetailViewModel extends ViewModel {
    private final ExhibitRepository exhibitRepository;

    // -- INPUT --
    private final MutableLiveData<Long> exhibitId = new MutableLiveData<>();

    // -- STATE --
    private final MediatorLiveData<Boolean> _isLoading = new MediatorLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    // -- OUTPUT --
    public final LiveData<ExhibitWithContent> selectedExhibit;

    public ExhibitDetailViewModel(ExhibitRepository exhibitRepository) {
        this.exhibitRepository = exhibitRepository;

        selectedExhibit = Transformations.switchMap(exhibitId, exhibitRepository::getExhibitWithContent);
    }

    public void loadExhibitData(long id) {
        if (exhibitId.getValue() != null && exhibitId.getValue() == id) {
            return;
        }

        _isLoading.setValue(true);
        _isLoading.addSource(selectedExhibit, exhibit -> checkLoadingStatus());

        exhibitId.setValue(id);
    }

    private void checkLoadingStatus() {
        if (selectedExhibit != null) {
            _isLoading.setValue(false);
            _isLoading.removeSource(selectedExhibit);
        }
    }

}

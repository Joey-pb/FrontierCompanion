package wgu.jbas127.frontiercompanion.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final ExhibitRepository exhibitRepository;

    public ViewModelFactory(ExhibitRepository exhibitRepository) {
        this.exhibitRepository = exhibitRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ExhibitDetailViewModel.class)) {
            return (T) new ExhibitDetailViewModel(exhibitRepository);
        }
        // Add more 'if' blocks here for additional ViewModels.

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

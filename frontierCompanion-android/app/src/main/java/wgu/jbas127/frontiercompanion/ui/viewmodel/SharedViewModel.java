package wgu.jbas127.frontiercompanion.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.models.Event;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Event<Exhibit>> routeToExhibit = new MutableLiveData<>();
    private final MutableLiveData<Event<Exhibit>> showOnMap = new MutableLiveData<>();
    private boolean isHandlingRequest = false;

    public void requestRoute(Exhibit exhibit) {
        isHandlingRequest = true;
        routeToExhibit.setValue(new Event<>(exhibit));
    }

    public LiveData<Event<Exhibit>> getRouteToExhibit() {
        return routeToExhibit;
    }

    public void requestShowOnMap(Exhibit exhibit) {
        isHandlingRequest = true;
        showOnMap.setValue(new Event<>(exhibit));
    }

    public LiveData<Event<Exhibit>> getShowOnMapRequest() {
        return showOnMap;
    }

    public boolean isHandlingRequest() {
        return isHandlingRequest;
    }

    public void consumeRequest() {
        isHandlingRequest = false;
    }
}

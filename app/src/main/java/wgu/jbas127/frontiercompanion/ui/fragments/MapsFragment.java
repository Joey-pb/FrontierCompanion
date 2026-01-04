package wgu.jbas127.frontiercompanion.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;

public class MapsFragment extends Fragment {

    private ExhibitRepository repository;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                List<Exhibit> exhibitList = repository.getAllExhibitsSync();

                handler.post(() -> {
                    if (exhibitList == null || exhibitList.isEmpty()) {
                        LatLng visitorCenter = new LatLng(38.124745, -79.050276);
                        googleMap.addMarker(new MarkerOptions().position(visitorCenter).title("No Exhibits Found"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(visitorCenter));
                        return;
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Exhibit exhibit : exhibitList) {
                        LatLng exhibitLocation = new LatLng(exhibit.getLatitude(), exhibit.getLongitude());

                        googleMap.addMarker(new MarkerOptions()
                                .position(exhibitLocation)
                                .title(exhibit.getName()));

                        builder.include(exhibitLocation);
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 150;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                    googleMap.setLatLngBoundsForCameraTarget(bounds);
                });
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new ExhibitRepository(requireActivity().getApplication());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}
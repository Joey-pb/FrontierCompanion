package wgu.jbas127.frontiercompanion.ui.fragments;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.behavior.HideViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.repository.ExhibitRepository;
import wgu.jbas127.frontiercompanion.databinding.BottomSheetDetailsBinding;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private ExhibitRepository repository;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private BottomSheetDetailsBinding sheetBinding;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    enableMyLocation();
            } else {
                    Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_LONG).show();
                }
            });

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

        LinearLayout bottomSheetLayout = requireActivity().findViewById(R.id.details_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        sheetBinding = BottomSheetDetailsBinding.bind(bottomSheetLayout);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;

            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
            HideViewOnScrollBehavior<BottomNavigationView> bottomNavBehavior =
                    HideViewOnScrollBehavior.from(bottomNavigationView);
            
            mMap.setOnMarkerClickListener(marker -> {
                Exhibit exhibit = (Exhibit) marker.getTag();
                if (exhibit != null) {
                    populateBottomSheet(exhibit);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    if (!bottomNavBehavior.isScrolledOut()) {
                        bottomNavBehavior.slideOut(bottomNavigationView);
                    }
                }
                return true;
            });
            
            mMap.setOnMapClickListener(latLng -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                if (bottomNavBehavior.isScrolledOut()) {
                    bottomNavBehavior.slideIn(bottomNavigationView);
                }
            });

            checkLocationPermissionAndEnableLocation();
            loadSitesOnMap();
        }
    };

    private void checkLocationPermissionAndEnableLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void enableMyLocation() {
        if (mMap != null) {
            try {
                mMap.setMyLocationEnabled(true);
                mMap.setPadding(0, 50, 0, 0);
            } catch (SecurityException e) {
                e.printStackTrace();
                Log.e("Location Error", "Unable to set MyLocation" + e.getMessage());
            }
        }
    }

    private void loadSitesOnMap() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                List<Exhibit> exhibitList = repository.getAllExhibitsSync();

                handler.post(() -> {
                    if (exhibitList == null || exhibitList.isEmpty()) {
                        LatLng visitorCenter = new LatLng(38.124745, -79.050276);
                        mMap.addMarker(new MarkerOptions().position(visitorCenter).title("No Exhibits Found"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(visitorCenter));
                        return;
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Exhibit exhibit : exhibitList) {
                        LatLng exhibitLocation = new LatLng(exhibit.getLatitude(), exhibit.getLongitude());

                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(exhibitLocation)
                                .title(exhibit.getName()));

                        if (marker != null) {
                            marker.setTag(exhibit);
                        }

                        builder.include(exhibitLocation);
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 150;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding), new GoogleMap.CancelableCallback() {

                        @Override
                        public void onCancel() {
                            mMap.setLatLngBoundsForCameraTarget(bounds);
                            mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);
                        }

                        @Override
                        public void onFinish() {
                            mMap.setLatLngBoundsForCameraTarget(bounds);
                            mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);
                        }
                    });
                });
            });
    }

    private void populateBottomSheet(Exhibit exhibit) {
        sheetBinding.exhibitTitle.setText(exhibit.getName());
        sheetBinding.exhibitDescription.setText(exhibit.getDescription());
        
        // TODO: Load image using Glide
        Context context = getContext();

        if (context != null && exhibit.getImageResName() != null) {
            int resId = getContext().getResources().getIdentifier(
                    exhibit.getImageResName(),
                    "drawable",
                    getContext().getPackageName()
            );

            sheetBinding.exhibitImage.setImageResource(resId);
        }

        sheetBinding.detailsButton.setOnClickListener(v -> {
            MapsFragmentDirections.ActionMapsFragmentToExhibitDetailsFragment action =
                    MapsFragmentDirections.actionMapsFragmentToExhibitDetailsFragment(exhibit.getId());

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            NavHostFragment.findNavController(MapsFragment.this).navigate(action);


        });

        sheetBinding.routeButton.setOnClickListener(v -> {
            // TODO: Implement route creation logic
            Toast.makeText(super.getContext(), "Create route button clicked", Toast.LENGTH_SHORT).show();
        });
    }

}
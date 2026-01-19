package wgu.jbas127.frontiercompanion.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import java.util.HashSet;
import java.util.Set;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.navigation_home);
        topLevelDestinations.add(R.id.navigation_search);
        topLevelDestinations.add(R.id.navigation_map);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            binding.bottomNavView.post(() -> {
                if (topLevelDestinations.contains(destination.getId())) {
                    binding.bottomNavView.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomNavView.setVisibility(View.GONE);
                }
            });
        });
    }
}
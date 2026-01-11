package wgu.jbas127.frontiercompanion.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if(destination.getId() == R.id.navigation_home ||
                    destination.getId() == R.id.navigation_search ||
                    destination.getId() == R.id.navigation_map) {
                binding.bottomNavView.setVisibility(View.VISIBLE);
            } else if(destination.getId() == R.id.exhibitDetailsFragment) {
                binding.bottomNavView.setVisibility(View.GONE);
            } else {
                binding.bottomNavView.setVisibility(View.GONE);
            }
        });

        binding.bottomNavView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_map) {
                navController.popBackStack(R.id.navigation_map, false);
            }
        });
    }
}
package wgu.jbas127.frontiercompanion.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply insets to the FragmentContainer to prevent content from going under the bars
//        ViewCompat.setOnApplyWindowInsetsListener(binding.navHostFragment, (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
//            return insets;
//        });

        // Apply insets to the BottomNavigationView to prevent it from being covered
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Get original padding
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            // Set the new bottom margin to be the original margin + the system bar height
            v.setPadding(0,0,0,systemBars.bottom); // Directly set margin instead of padding
            v.setLayoutParams(mlp);
            return insets;
        });

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
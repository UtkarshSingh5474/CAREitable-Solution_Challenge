package com.example.careitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.location.LocationRequest;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.careitable.databinding.ActivityMainBinding;
import com.example.careitable.fragments.AccountFragment;
import com.example.careitable.fragments.CharityFragment;
import com.example.careitable.fragments.DonationsFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        CheckNetwork.isInternetAvailable(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        binding.bottomNavigation.setSelectedItemId(R.id.nav_donations);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DonationsFragment())
                .commit();

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_charities:
                        selectedFragment = new CharityFragment();
                        break;

                    case R.id.nav_donations:
                        selectedFragment = new DonationsFragment();
                        break;

                    case R.id.nav_account:
                        selectedFragment = new AccountFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();

                return true;
            }
        });


    }

    public void changeFragment(Fragment fragment, int id) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                fragment).commit();
        binding.bottomNavigation.setSelectedItemId(id);

    }

}



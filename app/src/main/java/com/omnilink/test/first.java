package com.omnilink.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.namespace.ClientsFragment;
import com.example.namespace.ProfileFragment;
import com.example.namespace.MemosFragment;
import com.example.namespace.R;
import com.example.namespace.SessionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class first extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    MemosFragment memosFragment = new MemosFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    SessionsFragment sessionsFragment = new SessionsFragment();
    ClientsFragment clientsFragment = new ClientsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.Sessions) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, sessionsFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.Profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.Memos) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, memosFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.Clients) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, clientsFragment).commit();
                return true;
            }

                return false;
            }
        });

    }
}

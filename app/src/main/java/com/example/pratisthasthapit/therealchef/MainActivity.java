package com.example.pratisthasthapit.therealchef;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pratisthasthapit.therealchef.Fragment.HomeFragment;
import com.example.pratisthasthapit.therealchef.Fragment.NotificationFragment;
import com.example.pratisthasthapit.therealchef.Fragment.ProfileFragment;
import com.example.pratisthasthapit.therealchef.Fragment.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:{
                            selectedFragment = new HomeFragment();
                            break;
                        }
                        case R.id.nav_search:{
                            selectedFragment = new SearchFragment();
                            break;
                        }
                        case R.id.nav_add:{
                            selectedFragment = null;
                            Intent intent = new Intent(MainActivity.this, PostActivity.class);
                            startActivity(intent);
                            break;
                        }
                        case R.id.nav_notification:{
                            selectedFragment = new NotificationFragment();
                            break;
                        }
                        case R.id.nav_profile:{
                            SharedPreferences.Editor editor = getSharedPreferences("PREF", MODE_PRIVATE).edit();
                            editor.putString("profileid", FirebaseAuth.getInstance().getInstance().getCurrentUser().getUid());
                            editor.apply();
                            selectedFragment = new ProfileFragment();
                            break;
                        }
                    }
                    if (selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    }
                    return true;
                }
            };
}

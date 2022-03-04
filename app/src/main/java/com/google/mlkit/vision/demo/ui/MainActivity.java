package com.google.mlkit.vision.demo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.demo.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public int count=0;
    public int your_pos=0;
    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);



        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                count++;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        your_pos=0;
                        fragment=new Fragment_Home();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_notify:
                        your_pos=1;
                        fragment=new Fragment_Notify();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_setting:
                        your_pos=2;
                        fragment=new SettingsFragment2();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_profile:
                        your_pos=3;
                        fragment=new Fragment_Profile();
                        Fragment_load(fragment);
                        return true;
                }
                return false;
            }

        });

        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if( getSupportFragmentManager().getBackStackEntryCount() <= count) {
                    Log.d("Frag", "onBackStackChanged: "+your_pos);
                    //check your position based on selected fragment and set it accordingly.
                    navigation.getMenu().getItem(your_pos);
                }
            }
        });
        ///////////////////////////////////////////////////////////
        Fragment_load(new Fragment_Home());
        ///////////////////////////////////////////////////////////
    }
    private void Fragment_load(Fragment fragment){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        if (!getSupportFragmentManager().getFragments().isEmpty()){
            transaction.addToBackStack(null);
            transaction.setReorderingAllowed(true);
        }


        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            Fragment selectedFragment = null;
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {
                    selectedFragment = fragment;
                    break;
                }
            }
            if (!(selectedFragment instanceof Fragment_Home)) {
                navigation.setSelectedItemId(R.id.navigation_home);
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }
    }
}
package com.google.mlkit.vision.demo.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.demo.R;

public class MainActivity extends AppCompatActivity {

    ActionBar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        ///////////////////////////////////////////////////////////
        toolbar = getSupportActionBar();

        toolbar.setTitle("Shop");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_shop:
                        toolbar.setTitle("Trang chủ");
                        fragment=new Fragment_Anasayfa();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_gifts:
                        toolbar.setTitle("Thông báo");
                        fragment=new Fragment_Gift();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_cart:
                        toolbar.setTitle("Cài đặt");
                        fragment=new Fragment_Cards();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_profile:
                        toolbar.setTitle("Profile");
                        fragment=new Fragment_Profile();
                        Fragment_load(fragment);
                        return true;
                }
                return false;
            }
        });
        ///////////////////////////////////////////////////////////
        Fragment_load(new Fragment_Anasayfa());
        ///////////////////////////////////////////////////////////
    }
    private void Fragment_load(Fragment fragment){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
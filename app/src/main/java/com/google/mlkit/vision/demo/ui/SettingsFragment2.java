package com.google.mlkit.vision.demo.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.google.mlkit.vision.demo.R;

public class SettingsFragment2 extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
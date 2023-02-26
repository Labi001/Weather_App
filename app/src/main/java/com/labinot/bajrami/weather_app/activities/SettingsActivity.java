package com.labinot.bajrami.weather_app.activities;

import android.os.Bundle;

import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.fragments.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTheme();
        setTheme(Theme);
        setContentView(R.layout.activity_settings);

        setUpToolbar("Settings");
        setToolbarBackIcon();

        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setActivity(SettingsActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,settingsFragment).commit();

    }
}
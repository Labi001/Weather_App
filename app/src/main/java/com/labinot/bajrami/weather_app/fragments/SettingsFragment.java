package com.labinot.bajrami.weather_app.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.labinot.bajrami.weather_app.about.About;
import com.labinot.bajrami.weather_app.helper.Constants;
import com.labinot.bajrami.weather_app.R;
import com.labinot.bajrami.weather_app.activities.SplashScreenActivity;
import com.labinot.bajrami.weather_app.helper.MySharedPreferences;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private AppCompatActivity appCompatActivity;
    private boolean isThemeClicked = false;

    public void setActivity(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);


          for(int i=0; i<getPreferenceScreen().getPreferenceCount();i++){

              initSummary(getPreferenceScreen().getPreference(i));

          }

    }



    private void initSummary(Preference preference) {

        if(preference instanceof PreferenceCategory){

            PreferenceCategory cat = (PreferenceCategory) preference;

            for(int i=0; i<cat.getPreferenceCount(); i++){

                 initSummary(cat.getPreference(i));
            }


        }else if(preference instanceof ListPreference){

           ListPreference listPreference = (ListPreference) preference;
           listPreference.setSummary(listPreference.getEntry());

        }else{

            updatePreferences(preference);

        }

    }

    private void updatePreferences(Preference preference) {

        if(preference instanceof EditTextPreference){

            EditTextPreference editTextPreference = (EditTextPreference) preference;

            String city = editTextPreference.getText();
            city = city.substring(0,1).toUpperCase() + city.substring(1);
            editTextPreference.setText(city);
            editTextPreference.setSummary(city);
            MySharedPreferences.setNormalPref(appCompatActivity, preference.getKey(),city);

        }else if(preference instanceof ListPreference){

            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
            MySharedPreferences.setNormalPref(appCompatActivity, preference.getKey(),listPreference.getValue());

            if(preference.getKey().equals(Constants.list_theme_key) && isThemeClicked){

                isThemeClicked = true;
                reloadApp();


            }

        }else if(preference instanceof SwitchPreferenceCompat){

            SwitchPreferenceCompat switchPreferenceCompat = (SwitchPreferenceCompat) preference;
            switchPreferenceCompat.setChecked(switchPreferenceCompat.isChecked());

        }

    }

    private void reloadApp() {

        Intent intent = new Intent(appCompatActivity, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        appCompatActivity.finishAffinity();
        appCompatActivity.overridePendingTransition(0, 0);
        appCompatActivity.startActivity(intent);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        updatePreferences(findPreference(key));
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {

        switch (preference.getKey()){

            case Constants.list_theme_key:
                isThemeClicked = true;
                break;
            case Constants.pref_about_key:
                Intent i = new Intent(appCompatActivity, About.class);
                startActivity(i);
                break;
            case Constants.pref_help_key:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse(getString(R.string.help_uri));
                intent.setData(data);
                startActivity(intent);
                break;
            default:
                break;

        }

        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onResume() {
        super.onResume();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
}
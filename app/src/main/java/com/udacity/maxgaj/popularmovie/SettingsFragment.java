package com.udacity.maxgaj.popularmovie;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preference);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i=0; i<preferenceCount; i++){
            Preference p = preferenceScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String key = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, key);
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String key){
        if (preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int preferenceIndex = listPreference.findIndexOfValue(key);
            if (preferenceIndex >=0){
                listPreference.setSummary(listPreference.getEntries()[preferenceIndex]);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference p = findPreference(s);
        if (p != null){
            if (!(p instanceof CheckBoxPreference)) {
                String key = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, key);
            }
        }
    }
}

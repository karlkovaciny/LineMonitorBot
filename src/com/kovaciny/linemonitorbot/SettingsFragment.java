package com.kovaciny.linemonitorbot;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        //set the summary of the ListPreference to its currently selected value.
        ListPreference listPreference = (ListPreference) findPreference("vibrator_duration");
        if(listPreference.getValue()==null) {
            // to ensure we don't get a null value
            listPreference.setValueIndex(0);
        } 
        listPreference.setSummary(listPreference.getValue().toString());
        listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(newValue.toString());
                return true;
            }
        });
        
        Preference version = findPreference("version_name");
        String versionName;
        try {
        	versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;	
        } catch (NameNotFoundException e){
        	versionName = "3.14159";
        }
        version.setTitle(versionName);
    }
}

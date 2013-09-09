package com.kovaciny.linemonitorbot;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

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
        final String versionNameFinal = versionName;
        version.setTitle(versionNameFinal);
        
        Preference emailLink = findPreference("support_email");
        emailLink.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.support_email)});
                i.putExtra(Intent.EXTRA_SUBJECT, versionNameFinal);
                StringBuilder sb = new StringBuilder(android.os.Build.MANUFACTURER);  
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                String deviceDetails = "Device: " + sb.toString() + " " + android.os.Build.MODEL + "\nAndroid version: " + android.os.Build.VERSION.RELEASE + "\n\n";
                i.putExtra(Intent.EXTRA_TEXT   , deviceDetails);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
       
    }
}

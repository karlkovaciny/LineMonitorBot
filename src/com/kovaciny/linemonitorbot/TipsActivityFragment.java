package com.kovaciny.linemonitorbot;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class TipsActivityFragment extends ListFragment {

    public static final String MOTION_ACTIONS_PACKAGE_NAME = "novum.inceptum.motion";
    
    public static final String TINY_FLASHLIGHT_PACKAGE_NAME = "com.devuni.flashlight";

    public static final String PROXIMITY_ACTIONS_PACKAGE_NAME = "novum.inceptum.proximity";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayAdapter<String> twoLineAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, getResources().getStringArray(R.array.tips_and_apps_headers) ){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                
                text1.setText(getResources().getStringArray(R.array.tips_and_apps_headers)[position]);
                text2.setText(getResources().getStringArray(R.array.tips_and_apps_details)[position]);
                return view;
            }  
        };
        setListAdapter(twoLineAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        List<String> listHeaders = Arrays.asList(getResources().getStringArray(R.array.tips_and_apps_headers));
        String packageName = "";
        if (listHeaders.get(position).equals("Motion Actions")) {
            packageName = MOTION_ACTIONS_PACKAGE_NAME;
        } else if (listHeaders.get(position).equals("Tiny Flashlight")) {
            packageName = TINY_FLASHLIGHT_PACKAGE_NAME;
        } else if (listHeaders.get(position).equals("Proximity Actions")) {
            packageName = PROXIMITY_ACTIONS_PACKAGE_NAME;
        }
        if (packageName.length() > 0) {
            try {
                Uri uri = Uri.parse("market://details?id=" + packageName);
                startActivity( new Intent( Intent.ACTION_VIEW, uri));
            } catch (android.content.ActivityNotFoundException anfe) {
                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        }
        super.onListItemClick(l, v, position, id);
    }
    
}

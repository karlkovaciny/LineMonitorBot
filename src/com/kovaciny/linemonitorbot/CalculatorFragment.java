package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class CalculatorFragment extends Fragment implements View.OnClickListener {

	private ImageButton mImgBtn_calculator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_calculator_button, container, false);

		mImgBtn_calculator = (ImageButton) rootView.findViewById(R.id.btn_calculator);
		mImgBtn_calculator.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.btn_calculator):
			ArrayList<HashMap<String,Object>> items =new ArrayList<HashMap<String,Object>>();
		final PackageManager pm = getActivity().getPackageManager();
		List<PackageInfo> packs = pm.getInstalledPackages(0);  
		for (PackageInfo pi : packs) {
			if( pi.packageName.toString().toLowerCase().contains("calcul")){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("appName", pi.applicationInfo.loadLabel(pm));
				map.put("packageName", pi.packageName);
				items.add(map);
			}
		}
		if(items.size()>=1){
			//Search the list for the first calculator whose name starts with just plain "Calculator".
			//If you don't find one, take the first app in the list.
			boolean found = false;
			String packageName = "";
			for (HashMap<String, Object> hm : items) {
				if (!found) {
					String appName = (String) hm.get("appName");
					if (appName.toLowerCase().startsWith("calc")) {
						packageName = (String) hm.get("packageName");
						found = true;
					}
				}
			}
			if (!found) {
				packageName = (String) items.get(0).get("packageName");
			}
			Intent i = pm.getLaunchIntentForPackage(packageName);
			if (i != null)
				startActivity(i);
		} 
		else{
			// Application not found
			Toast.makeText(getActivity(), "No calculator found", Toast.LENGTH_SHORT).show();
		}

		break;
		default:
			break;
		}

	}


}

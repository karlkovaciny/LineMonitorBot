package com.kovaciny.linemonitorbot;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SkidsListActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		SkidsListFragment skidsListFrag = new SkidsListFragment();
		Bundle extras = getIntent().getExtras();
		skidsListFrag.setArguments(extras);
		getFragmentManager().beginTransaction().replace(android.R.id.content, skidsListFrag).commit();
	}

}

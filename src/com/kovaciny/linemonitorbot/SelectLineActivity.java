package com.kovaciny.linemonitorbot;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.kovaciny.primexmodel.PrimexModel;

public class SelectLineActivity extends Activity {

    MenuItem mJobPicker;
    MenuItem mLinePicker;
    PrimexModel mModel;
    
    private static final int LINE_LIST_MENU_GROUP = 1111;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new PrimexModel(getApplication());
        Bundle intentExtras = getIntent().getExtras();
        SelectLineFragment slfrag = new SelectLineFragment();
        slfrag.setArguments(intentExtras);
        getFragmentManager().beginTransaction().replace(android.R.id.content, slfrag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mJobPicker = (MenuItem) menu.findItem(R.id.action_pick_job);
        mLinePicker = (MenuItem) menu.findItem(R.id.action_pick_line);
        mJobPicker.setEnabled(false);
        
        // populate the line picker with line numbers from the database
        List<Integer> lineNumberList = new ArrayList<Integer>();
        lineNumberList = mModel.getLineNumbers();

        Menu pickLineSubMenu = mLinePicker.getSubMenu();
        pickLineSubMenu.clear();

        for (int i = 0; i < lineNumberList.size(); i++) {
            // don't have access to View.generateViewId(), so fake a random ID
            pickLineSubMenu.add(LINE_LIST_MENU_GROUP,
                    (Menu.FIRST + lineNumberList.get(i)),
                    Menu.FLAG_APPEND_TO_GROUP,
                    "Line  " + String.valueOf(lineNumberList.get(i)));
        }
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() == LINE_LIST_MENU_GROUP) {
            Intent intentMessage = new Intent();
            int lineNumber = item.getItemId() - Menu.FIRST;
            intentMessage.putExtra("LineNumber", lineNumber);
            setResult(RESULT_FIRST_USER, intentMessage);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
}
 
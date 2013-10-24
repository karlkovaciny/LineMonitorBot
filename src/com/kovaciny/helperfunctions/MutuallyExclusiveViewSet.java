package com.kovaciny.helperfunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kovaciny.linemonitorbot.R;

public class MutuallyExclusiveViewSet implements View.OnTouchListener {

    public static final int NO_BACKGROUND = 0;
    
    Context mContext;
    HashMap<ViewGroup, EditText> mGroupsToRequiredFieldsMap = new HashMap<ViewGroup, EditText>();
    HashMap<ViewGroup, List<EditText>> mGroupsToListOfChildrenMap = new HashMap<ViewGroup, List<EditText>>();
    
    public MutuallyExclusiveViewSet(Context context, HashMap<ViewGroup, EditText> groupsToRequiredFieldsMap, int backgroundSelectorId) {
        mGroupsToRequiredFieldsMap = groupsToRequiredFieldsMap;
        mContext = context;
        if (backgroundSelectorId != NO_BACKGROUND) {
            setBackgroundSelector(backgroundSelectorId);
        }

        //Find all EditTexts in the viewgroups so we can check them for validity and focus.
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            ViewGroup container = entry.getKey();
            mGroupsToListOfChildrenMap.put(container, HelperFunction.getChildEditTexts(container));
        }
        
        for (Map.Entry<ViewGroup, List<EditText>> entry : mGroupsToListOfChildrenMap.entrySet() ) {
            List<EditText> editTexts = entry.getValue();
            for (Iterator<EditText> itr = editTexts.iterator(); itr.hasNext(); ) {
                EditText et = itr.next();
                et.setOnTouchListener(this);
            }
        }
    }
    
    public void setBackgroundSelector(int id) {
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            View vg = (View) entry.getKey();
            vg.setBackgroundResource(id);
        }
    }
    
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(MotionEvent.ACTION_UP == event.getAction()) {
                for (ViewGroup vg : mGroupsToListOfChildrenMap.keySet()) {
                    View view = (View) vg;
                    if (mGroupsToListOfChildrenMap.get(vg).contains(v)) {
                        view.setSelected(true);
                    } else {
                        view.setSelected(false);
                    }
                }
            }
            return false;
        }
    
    /*
     * If exactly one group is valid, selects it (necessary in case a different view has the focus), and returns its id.
     * Otherwise sets errors in fields and returns 0. 
     */
    public int getValidGroupId() {
        HashMap<ViewGroup, EditText> groupsWithValuesMap = new HashMap<ViewGroup, EditText>();
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            entry.getValue().setError(null);
            if (entry.getValue().getText().length() > 0) {
                groupsWithValuesMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (groupsWithValuesMap.size() == 0) {
            for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
                entry.getValue().setError(mContext.getString(R.string.error_mutually_exclusive_need_at_least_one));
            }
            return 0; 
        } else if (groupsWithValuesMap.size() > 1) {
            for (Map.Entry<ViewGroup, EditText> entry : groupsWithValuesMap.entrySet()) {
                entry.getValue().setError(mContext.getString(R.string.error_mutually_exclusive_need_only_one));
            }   
            return 0;
        } else {
            View selected = (View) groupsWithValuesMap.keySet().toArray()[0];
            clearSelection();
            selected.setSelected(true);
            return selected.getId();
        }
    }
    
    public void clearSelection() {
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            View v = (View) entry.getKey();
            v.setSelected(false);
        }
    }
}

package com.kovaciny.helperfunctions;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.kovaciny.linemonitorbot.R;

public class MutuallyExclusiveViewSet<ViewGroup> implements View.OnTouchListener {


    public static final int NO_BACKGROUND = 0;
    
    Context mContext;
    HashMap<ViewGroup, EditText> mGroupsToRequiredFieldsMap = new HashMap<ViewGroup, EditText>();
    
    public MutuallyExclusiveViewSet(Context context, HashMap<ViewGroup, EditText> groupsToRequiredFieldsMap, int backgroundSelectorId) {
        mGroupsToRequiredFieldsMap = groupsToRequiredFieldsMap;
        mContext = context;
        if (backgroundSelectorId != NO_BACKGROUND) {
            setBackgroundSelector(backgroundSelectorId);
        }
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            EditText et = entry.getValue();
            et.setOnTouchListener(this);
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
                for (ViewGroup vg : mGroupsToRequiredFieldsMap.keySet()) {
                    View view = (View) vg;
                    if (mGroupsToRequiredFieldsMap.get(vg) == v) {
                        view.setSelected(true);
                    } else {
                        view.setSelected(false);
                    }
                }
            }
            return false;
        }
    
    /*
     * If exactly one group is valid, returns it, otherwise sets errors in fields and returns null. 
     */
    public ViewGroup validateGroups() {
        HashMap<ViewGroup, EditText> groupsWithValuesMap = new HashMap<ViewGroup, EditText>();
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            if (entry.getValue().getText().length() > 0) {
                groupsWithValuesMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (groupsWithValuesMap.size() == 0) {
            for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
                entry.getValue().setError(mContext.getString(R.string.error_mutually_exclusive_need_at_least_one));
            }
            return null; 
        } else if (groupsWithValuesMap.size() > 1) {
            for (Map.Entry<ViewGroup, EditText> entry : groupsWithValuesMap.entrySet()) {
                entry.getValue().setError(mContext.getString(R.string.error_mutually_exclusive_need_only_one));
            }   
            return null;
        } else {
            for (Map.Entry<ViewGroup, EditText> entry : groupsWithValuesMap.entrySet()) {
                entry.getValue().setError(null);
            }
            return (ViewGroup) groupsWithValuesMap.keySet().toArray()[0];
        }
    }
}

package com.kovaciny.helperfunctions;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.EditText;

import com.kovaciny.linemonitorbot.R;

public class MutuallyExclusiveViewSet<ViewGroup> {

    Context mContext;
    HashMap<ViewGroup, EditText> mGroupsToRequiredFieldsMap = new HashMap<ViewGroup, EditText>();
    
    public MutuallyExclusiveViewSet(Context context, HashMap<ViewGroup, EditText> groupsToRequiredFieldsMap) {
        mGroupsToRequiredFieldsMap = groupsToRequiredFieldsMap;
        mContext = context;
        setBackgroundSelector(R.drawable.selector_viewgroup_exclusive);
    }
    
    public void setBackgroundSelector(int id) {
        for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
            View vg = (View) entry.getKey();
            vg.setBackgroundResource(id);
        }
    }
    
    /*
     * If exactly one group is valid, returns it, otherwise sets errors in fields and returns null. 
     */
    public ViewGroup validateGroups() {
        HashMap<ViewGroup, EditText> groupsWithValuesMap = new HashMap<ViewGroup, EditText>();
        for (Map.Entry<ViewGroup, EditText> entry : groupsWithValuesMap.entrySet()) {
            if (entry.getValue().getText().length() == 0) {
                groupsWithValuesMap.remove(entry.getKey());
            }
        }
        if (groupsWithValuesMap.size() == 0) {
            for (Map.Entry<ViewGroup, EditText> entry : mGroupsToRequiredFieldsMap.entrySet()) {
                entry.getValue().setError(mContext.getString(R.string.error_need_at_least_one));
            }
            return null; 
        } else if (groupsWithValuesMap.size() > 1) {
            for (Map.Entry<ViewGroup, EditText> entry : groupsWithValuesMap.entrySet()) {
                entry.getValue().setError(mContext.getString(R.string.error_need_only_one));
            }   
            return null;
        } else return (ViewGroup) groupsWithValuesMap.keySet().toArray()[0];
    }
}

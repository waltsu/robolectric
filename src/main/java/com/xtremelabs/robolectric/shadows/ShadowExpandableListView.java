package com.xtremelabs.robolectric.shadows;

import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.internal.RealObject;

@Implements(ExpandableListView.class)
public class ShadowExpandableListView extends ShadowListView {
    @RealObject private ExpandableListView mExpandable;
    private OnChildClickListener mChildClickListener;
    
    private ExpandableListAdapter adapter;
    
    @Implementation
    @Override
    public boolean performItemClick(View view, int position, long id) {
        if (mChildClickListener != null) {
            mChildClickListener.onChildClick(mExpandable, null, 0, position, id);
            return true;
        }
        return false;
    }

    @Implementation
    public void setOnChildClickListener(OnChildClickListener clildListener) {
        mChildClickListener = clildListener;
    }
    
    @Implementation
    public void setAdapter(ExpandableListAdapter adapter) {
        this.adapter = adapter;
    }
    
    @Implementation
    public ExpandableListAdapter getExpandableListAdapter (){
    	return adapter;
    }
}
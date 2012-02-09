package com.xtremelabs.robolectric.shadows;

import android.widget.AbsListView;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;

@Implements(AbsListView.class)
public class ShadowAbsListView extends ShadowAdapterView {
    private AbsListView.OnScrollListener onScrollListener;
    private int choiceMode;

    @Implementation
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        onScrollListener = l;
    }
    
    @Implementation
    public void setChoiceMode(int choiceMode) {
    	this.choiceMode = choiceMode;
    }
    
    @Implementation
    public int getChoiceMode() {
    	return choiceMode;
    }

    /**
     * Robolectric accessor for the onScrollListener
     *
     * @return AbsListView.OnScrollListener
     */
    public AbsListView.OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }
}

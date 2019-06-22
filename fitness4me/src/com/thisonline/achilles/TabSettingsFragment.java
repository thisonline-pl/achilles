package com.thisonline.achilles;
import com.thisonline.achilles.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabSettingsFragment extends Fragment {
    private static View view;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		//setRetainInstance(true);
    	if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
	    	view = inflater.inflate(R.layout.tab_frag5_layout, container, false);
	    } catch (InflateException e) {
	        
	    }
        return view;
    }
	@Override
    public void onDestroyView() // necessary for restoring the dialog
    {
        super.onDestroyView();
    }
}
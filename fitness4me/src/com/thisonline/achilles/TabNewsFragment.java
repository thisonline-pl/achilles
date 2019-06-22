package com.thisonline.achilles;

import com.thisonline.achilles.R;
import com.thisonline.achilles.middleware.AchillesEngine;
import com.thisonline.achilles.uihelper.UICustomAnimationDrawable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TabNewsFragment extends Fragment {
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
	    	view = inflater.inflate(R.layout.tab_news_layout, container, false);
            //load advertisements:
            new UICustomAnimationDrawable((ImageView)view.findViewById(R.id.imageViewAdvertTabNews),
            							  AchillesEngine.getInstance().getAllAds(), 3000, getResources(), getActivity());
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
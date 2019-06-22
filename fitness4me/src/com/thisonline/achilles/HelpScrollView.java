package com.thisonline.achilles;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class HelpScrollView extends ScrollView {
	private ScrollViewListener scrollViewListener = null;
	
    public HelpScrollView(Context context) {
        super(context);
    }

    public HelpScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HelpScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {

        View view = (View) getChildAt(getChildCount() - 1);
        int diff = (view.getBottom() - (getHeight() + getScrollY()));
        if (diff == 0) { // if diff is zero, then the bottom has been reached
            if (scrollViewListener != null) {
                scrollViewListener.onScrollEnded(this, x, y, oldx, oldy);
            }
        }
        super.onScrollChanged(x, y, oldx, oldy);
    }

}

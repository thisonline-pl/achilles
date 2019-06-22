package com.thisonline.achilles;


import com.thisonline.achilles.R;
import com.thisonline.achilles.middleware.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SplashAchillesActivity extends FragmentActivity {

	private static String TAG = SplashAchillesActivity.class.getName();
	private static long SLEEP_TIME = 2;	// Sleep for some time

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
      	this.requestWindowFeature(Window.FEATURE_NO_TITLE);	// Removes title bar
      	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	// Removes notification bar
        
      	setContentView(R.layout.splash_achilles);
        
	    // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
 	}
	
	private class IntentLauncher extends Thread {
    
		@Override
    	/**
    	 * Sleep for some time and than start new activity.
    	 */
		public void run() {
    		try {
            	// Sleeping
    			Thread.sleep(SLEEP_TIME*1000);
            } catch (Exception e) {
            	Log.e(TAG, e.getMessage());
            }
            
            // Start main activity
          	Intent intent = new Intent(SplashAchillesActivity.this, TabsFragmentActivity.class);
          	SplashAchillesActivity.this.startActivity(intent);
          	
          	SplashAchillesActivity.this.finish();
          	SplashAchillesActivity.this.overridePendingTransition( R.anim.fadein, R.anim.fadeout );
    	}
    }
	
}

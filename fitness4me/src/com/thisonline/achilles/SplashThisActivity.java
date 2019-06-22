package com.thisonline.achilles;


import com.thisonline.achilles.R;
import com.thisonline.achilles.middleware.AchillesEngine;
import com.thisonline.achilles.middleware.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * First activity in the application (splash screen #1).
 * comments & fixes by Marek Zawadzki (c)
 *
 */
public class SplashThisActivity extends FragmentActivity {

	private static String TAG = SplashThisActivity.class.getName();
	private static long SLEEP_TIME = 1;	// Sleep for some time
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
      	this.requestWindowFeature(Window.FEATURE_NO_TITLE);	// Removes title bar
      	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	// Removes notification bar
        
      	setContentView(R.layout.splash_this);
        new DataLoaderBackgroundTask().execute("");  //see this task preExecute() for AchillesEngine parameters

      	Animation fade = AnimationUtils.loadAnimation(this, R.anim.fadein);
    	ImageView logo = (ImageView) findViewById(R.id.splashThis);
    	
      	fade.setAnimationListener(new AnimationListener() {

      	   @Override
      	   public void onAnimationRepeat(Animation animation) {
      	    // TODO Auto-generated method stub

      	   }

      	   @Override
      	   public void onAnimationStart(Animation animation) {
      	    // TODO Auto-generated method stub

      	   }

      	   @Override
      	   public void onAnimationEnd(Animation animation) {
      		 
      		 try {
             	// Sleeping
     			Thread.sleep(SLEEP_TIME*100);
             } catch (Exception e) {
             	Log.e(TAG, e.getMessage());
             }
             
             // Start main activity
           	Intent intent = new Intent(SplashThisActivity.this, SplashAchillesActivity.class);
           	SplashThisActivity.this.startActivity(intent);
           	
           	SplashThisActivity.this.finish();
           	SplashThisActivity.this.overridePendingTransition( R.anim.fadein, R.anim.fadeout );
      	   }

      	  });
      	  logo.startAnimation(fade);
	}
	
	
	private void debugShowServerChooserPopup() {
		String[] optionsArray = {"1.", "2. ", "3. "};
        new AlertDialog.Builder(this)
        .setTitle("[DEBUG] Choose server:") 
        .setSingleChoiceItems(optionsArray, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
         })
         .show();
	}

	/**
	 * Background task to load all data from local database and, if necessary,
	 * from the server.
	 * (c) Marek Zawadzki
	 */
	private class DataLoaderBackgroundTask extends AsyncTask<String, Integer, String> {
		private AchillesEngine achillesEngine;

		@Override
		protected void onPreExecute() {
			// * SETUP: *************************************************************************
			Utils.DEBUG_CONSOLE_MESSAGES_OFF = true;
	      	boolean DEBUG_OFFLINE_MODE = false;
	        //String achillesBackendURL = "http://10.0.2.2/achillesbackend/AchillesBackend.php";
	      	//String achillesBackendURL = "http://ec2-54-217-221-251.eu-west-1.compute.amazonaws.com/achillesbackend-sandbox/AchillesBackend.php";
	      	String achillesBackendURL = "http://ec2-54-217-221-251.eu-west-1.compute.amazonaws.com/achillesbackend/AchillesBackend.php";
	        AchillesEngine.setAdImageDesiredWidth(getApplicationContext(), 160, 320, 360, 360);
	        AchillesEngine.setClubIconDesiredWidth(getApplicationContext(), 25, 36, 60, 90);
	        AchillesEngine.setNewsImageDesiredWidth(getApplicationContext(), 215/2, 295/2, 500/2, 600/2);
	        // **********************************************************************************
	        achillesEngine = AchillesEngine.getInstance();
	        achillesEngine.initializeEngine(getApplicationContext(), DEBUG_OFFLINE_MODE, achillesBackendURL);
		}

		@Override
		protected String doInBackground(String... params) {
			Utils.debug("****** Starting background task.", true, true);
			Utils.debug("****** PHASE 1/3: loading local data.", true, true);
			achillesEngine.loadAllDataFromLocalDatabase();
			achillesEngine.printAll();
			Utils.debug("****** PHASE 2/3: updating data from server.", true, true);
			achillesEngine.updateAllData();
			achillesEngine.closeDatabase();
			achillesEngine.printAll();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onProgressUpdate(Integer... stage) {
		}
	}
	
}

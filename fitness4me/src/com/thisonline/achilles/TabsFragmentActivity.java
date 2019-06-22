package com.thisonline.achilles;

import com.thisonline.achilles.R;
import com.thisonline.achilles.middleware.AchillesEngine;
import com.thisonline.achilles.middleware.Utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Activity holding all main tabs (Start, Trainings, Clubs, etc.).
 * (comment&fixes by Marek Zawadzki)
 *
 */
public class TabsFragmentActivity extends FragmentActivity {

	static String TAB_HOME 		= "Home Tab";
	static String TAB_TRAININGS = "Trainings Tab";
	static String TAB_CLUBS 	= "Clubs Tab";
	static String TAB_NEWS 		= "News Tab";
	static String TAB_HELP 		= "Help Tab";

	TabHost mTabHost;

	TabHomeFragment fragment1;
	TabScheduleFragment fragment2;
	TabClubsFragment fragment3;
	TabNewsFragment fragment4;
//	TabSettingsFragment fragment5;
	TabHelpFragment fragment6;
	
	private AchillesEngine achillesEngine;
	private boolean activityVisible = false;
	private Context context;
	ProgressDialog progressDialog;
	public ProgressDialog pd;

	private static TabsFragmentActivity instance;
	static TabsFragmentActivity getInstance() {
		return instance;
	}
	
   	/**
	 * Allows to detect when the activity has been drawn and is visible to the user and we can measure its width, etc.
	 * Supports situation with dialog boxes (unlike "onWindowFocusChanged" method). 
	 * 
	 */
  	private OnGlobalLayoutListener createOnGlobalLayoutListener(final View view) {
		return new OnGlobalLayoutListener() {
			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (activityVisible == false) {
					View viewToMeasureWidth = (View) findViewById(R.id.tabsLayoutRelativeLayout).getParent();
					Utils.debug("Listener found WIDTH: " + viewToMeasureWidth.getWidth(), true, true);
					activityVisible = true;
				}

				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
				else {
					view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		};
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// super.onSaveInstanceState(savedInstanceState);
		context = this;
		instance = this;

			
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.tab_schedule_layout);

		if (customTitleSupported) {
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
		}

		final TextView myTitleText = (TextView) findViewById(R.id.text_bar);
		if (myTitleText != null) {
			myTitleText.setText("Achilles");
		}
		
        final View view = findViewById(R.id.tabsLayoutRelativeLayout);
        if (view == null) {
        	Utils.debug("ERROR: finding view failed.", true, true);
        } else {
        	view.getViewTreeObserver().addOnGlobalLayoutListener(createOnGlobalLayoutListener(view));
        }
        
		//setContentView(R.layout.splash_achilles);
		achillesEngine = AchillesEngine.getInstance();
	    new UIInitializationBackgroundTask().execute(""); 

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		super.onRestoreInstanceState(outState);
	}

	@Override
	public void onDestroy() // necessary for restoring the dialog
	{
		super.onDestroy();
	}

	public void initializeTab() {

		TabHost.TabSpec spec = mTabHost.newTabSpec(TAB_HOME);
		mTabHost.setCurrentTab(-3);

		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView("", R.drawable.icon_start_tab));
		mTabHost.addTab(spec);

		spec = mTabHost.newTabSpec(TAB_TRAININGS);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView("", R.drawable.icon_trainings_tab));
		mTabHost.addTab(spec);

		spec = mTabHost.newTabSpec(TAB_CLUBS);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		// spec.setContent(new Intent(this, ClubsTab.class));
		spec.setIndicator(createTabView("", R.drawable.icon_clubs_tab));
		mTabHost.addTab(spec);

		spec = mTabHost.newTabSpec(TAB_NEWS);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView("", R.drawable.icon_news_tab));
		mTabHost.addTab(spec);
		spec = mTabHost.newTabSpec(TAB_HELP);
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				return findViewById(android.R.id.tabcontent);
			}
		});
		spec.setIndicator(createTabView("", R.drawable.icon_help_tab));
		mTabHost.addTab(spec);
	}

	TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
		public void onTabChanged(String tabId) {
			Utils.debug("Tab changed to: " + tabId, true, true);
			if (tabId.equals(TAB_HOME)) {
				pushFragments(tabId, fragment1);
			} else if (tabId.equals(TAB_TRAININGS)) {
				pushFragments(tabId, fragment2);
			} else if (tabId.equals(TAB_CLUBS)) {
				pushFragments(tabId, fragment3);
			} else if (tabId.equals(TAB_NEWS)) {
				pushFragments(tabId, fragment4);
			} else if (tabId.equals(TAB_HELP)) {
				pushFragments(tabId, fragment6);
			}
		}

	};

	/**
	 * Hide&show fragments when user clicks tabs.
	 * (fixes by Marek Zawadzki) 
	 */
	public void pushFragments(String tag, Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (fragmentManager.findFragmentByTag(tag) == null) {
			fragmentTransaction.add(android.R.id.tabcontent, fragment, tag);
		}
		
		String[] tabTags = new String[]{TAB_HOME, TAB_TRAININGS, TAB_CLUBS, TAB_NEWS, TAB_HELP};
		Fragment f;
		for (String tabTag : tabTags) {
			f = fragmentManager.findFragmentByTag(tabTag);
			if (f != null) {
				fragmentTransaction.hide(f);
			}
		}
		fragmentTransaction.show(fragment);
		fragmentTransaction.commit();
	}

	
	private View createTabView(final String text, final int id) {
		View view = LayoutInflater.from(this).inflate(R.layout.tabs_icon, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
		imageView.setImageDrawable(getResources().getDrawable(id));

		return view;
	}

	private void showApplicationUpgradePopup() {
        new AlertDialog.Builder(context)
        .setTitle(getResources().getString(R.string.application_update_question_title) 
        								   + "  (v.: " + Utils.getPackageInfo(context).versionCode 
        								   + "→" + achillesEngine.getAppConfiguration().getApkNewestVersionCode() + ")")
        .setMessage(getResources().getString(R.string.application_update_question))
        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // continue 
    	      	Utils.debug("Launching market.", true, true);
    	      	try {
    	      		Intent goToMarket = new Intent(Intent.ACTION_VIEW)
    	      		.setData(Uri.parse("market://details?id=com.thisonline.achilles"));
    	      		startActivity(goToMarket);
    	      	} catch (Exception e) {
    	      		Utils.debug("ERROR: exception when launching market activity: " + e, true, true);
    	      	}
            }
         })
        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
         .show();
	}

	private void showOfflineModePopup() {
        new AlertDialog.Builder(context)
        .setTitle(getResources().getString(R.string.offline_mode_warning_title))
        .setMessage(getResources().getString(R.string.offline_mode_warning))
        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	dialog.dismiss();
            }
         })
         .show();
	}
	
	/**
	 * Background task to wait for the engine to load data and then initialize
	 * UI.
	 */
	private class UIInitializationBackgroundTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			publishProgress(0);
			Utils.sleep(200);
			Utils.debug("****** Starting background task.", true, true);

			int engineState = AchillesEngine.getEngineState();
			while (engineState != AchillesEngine.ENGINE_STATE_ALL_DATA_LOADED) {
				if (engineState == AchillesEngine.ENGINE_STATE_LOADING_LOCAL_DATA) {
					publishProgress(1);
				} else if (engineState == AchillesEngine.ENGINE_STATE_LOADING_REMOTE_DATA) {
					publishProgress(2);
				}
				Utils.sleep(200);
				engineState = AchillesEngine.getEngineState();
			}
			publishProgress(3);
			Utils.sleep(200);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Utils.debug("****** PHASE 3/3: rendering interface.", true, true);

			fragment1 = new TabHomeFragment();
			Utils.debug("Using width/height: " + ((View) findViewById(R.id.tabsLayoutRelativeLayout).getParent()).getWidth()
						+ "/" + ((View) findViewById(R.id.tabsLayoutRelativeLayout).getParent()).getHeight(), true, true);
			// code to restore/set default club goes here:
			int initiallySelectedClubId = achillesEngine.getAllClubs().get(0).getId();
			fragment2 = TabScheduleFragment.newInstance(((View) findViewById(R.id.tabsLayoutRelativeLayout).getParent()).getWidth(), 
							(int) Utils.convertDpToPixel(30, context), Utils.getCurrentDayOfWeek() - 1, // tabs are numbered from 0, days from 1);
							initiallySelectedClubId);
			fragment3 = new TabClubsFragment();
			fragment4 = new TabNewsFragment();
			// fragment5 = new TabSettingsFragment();
			fragment6 = new TabHelpFragment();

			mTabHost = (TabHost) findViewById(android.R.id.tabhost);
			mTabHost.setOnTabChangedListener(listener);
			mTabHost.setup();

			initializeTab();
			progressDialog.dismiss();
			
	        int applicationVersionCodeFromManifest = Utils.getPackageInfo(context).versionCode;
	        int applicationVersionCodeLatest = achillesEngine.getAppConfiguration().getApkNewestVersionCode();
	        Utils.debug("Application versionCode from manifest: " + applicationVersionCodeFromManifest, true, true);
	        Utils.debug("Newest application version code: " + applicationVersionCodeLatest, true, true);

	        if (AchillesEngine.getDebugOfflineModeStatus() == true) {
	        	showOfflineModePopup();
	        } else if (applicationVersionCodeLatest > applicationVersionCodeFromManifest) {
	        	showApplicationUpgradePopup();
			}

			Utils.debug("****** Finished background task.", true, true);
			Utils.debugPrintGlobalNumberOfErrors();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Integer... stage) {
			if (stage[0] == 0) {
				progressDialog = new ProgressDialog(context);
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(true);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("Trwa ładowanie danych, proszę czekać...");
				progressDialog.getWindow().setGravity(Gravity.BOTTOM);
				progressDialog.show();
			} else if (stage[0] == 1) {
				progressDialog.setMessage("Ładuję dane z lokalnej bazy, proszę czekać.");
			} else if (stage[0] == 2) {
				progressDialog.setMessage("Ładuję dane z serwera, proszę czekać");
			} else if (stage[0] == 3) {
				progressDialog.setMessage("Ładowanie danych zakończone. Inicjalizuję interfejs, proszę czekać...");
				
			}
		}
	}
   
}
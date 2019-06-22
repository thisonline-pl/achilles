package com.thisonline.achilles;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.thisonline.achilles.middleware.AchillesEngine;
import com.thisonline.achilles.middleware.Club;
import com.thisonline.achilles.middleware.Utils;
import com.thisonline.achilles.uihelper.UICustomAnimationDrawable;
import com.thisonline.achilles.uihelper.UICustomTabWidget;
import com.thisonline.achilles.uihelper.UIScheduleFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Fragment implementing "Schedule" tab: holding holding ad, spinners to choose club & training type and all 7 week tabs (Mon-Sun).
 * (c) Marek Zawadzki
 *
 */
public class TabScheduleFragment extends Fragment {
	
	private int dayTabsBarWidth;
	private int dayTabsBarHeight;
	private int dayTabToSet;
	private int selectedClubId;
	private List<Fragment> dayTabFragments;
	private LinearLayout tabsWidgetLinearLayout;
	private ViewPager viewPager;
	private AchillesEngine achillesEngine;
	private ArrayList<Club> clubs;
	private static int UISCHEDULE_MARGIN = 20;
	
	//workaround to the bug in Android (not setting child FragmentManager to null):
	//https://code.google.com/p/android/issues/detail?id=42601
	//http://stackoverflow.com/questions/14929907/causing-a-java-illegalstateexception-error-no-activity-only-when-navigating-to
	private static final Field sChildFragmentManagerField;
    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Utils.debug("ERROR: exception when getting mChildFragmentManager field: " + e, true, true);
        }
        sChildFragmentManagerField = f;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (sChildFragmentManagerField != null) {
            try {
                sChildFragmentManagerField.set(this, null);
            } catch (Exception e) {
            	Utils.debug("ERROR: exception when setting mChildFragmentManager field: " + e, true, true);
            }
        }
    }

	
	/**
	 * Method that should be used to create this fragment.
	 * Pattern required by Google: necessary to pass custom arguments (via Bundle) when creating a fragment.
	 * 
	 * @param tabsBarWidth If set > 0 it will be used as tab bar width instead of getWidth() of tabs bar layout.
	 * @param tabsBarHeight If set > 0 it will be used as tab bar height instead of MATCH_PARENT.
	 * @param dayTabToSet Defines which tab (day) should be set.
	 * @param selectedClubId Id of a club that should get selected when the schedule is created.
	 * @return Fragment.
	 */
	public static final TabScheduleFragment newInstance(int dayTabsBarWidth, int dayTabsBarHeight, int dayTabToSet, int selectedClubId) {
		TabScheduleFragment fragment = new TabScheduleFragment();
		Bundle bundle = new Bundle(4);
		bundle.putInt("dayTabsBarWidth", dayTabsBarWidth);
		bundle.putInt("dayTabsBarHeight", dayTabsBarHeight);
		bundle.putInt("dayTabToSet", dayTabToSet);
		bundle.putInt("selectedClubId", selectedClubId);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		Utils.debug("FRAGMENT: onCreate", true, true);
		super.onCreate(savedInstanceState);

		dayTabsBarWidth = getArguments().getInt("dayTabsBarWidth") - UISCHEDULE_MARGIN;
		dayTabsBarHeight = getArguments().getInt("dayTabsBarHeight");
		dayTabToSet = getArguments().getInt("dayTabToSet");
		selectedClubId = getArguments().getInt("selectedClubId");
		achillesEngine = AchillesEngine.getInstance();
		clubs = achillesEngine.getAllClubs();
	}

	private int[][] createListOfTabIcons() {
	  		int[][] tabIcons = new int[7][2];
	  		
	  		tabIcons[0][0] = R.drawable.monday_off;
	  		tabIcons[0][1] = R.drawable.monday_on;
	  		tabIcons[1][0] = R.drawable.tuesday_off;
	  		tabIcons[1][1] = R.drawable.tuesday_on;
	  		tabIcons[2][0] = R.drawable.wednesday_off;
	  		tabIcons[2][1] = R.drawable.wednesday_on;
	  		tabIcons[3][0] = R.drawable.thursday_off;
	  		tabIcons[3][1] = R.drawable.thursday_on;
	  		tabIcons[4][0] = R.drawable.friday_off;
	  		tabIcons[4][1] = R.drawable.friday_on;
	  		tabIcons[5][0] = R.drawable.saturday_off;
	  		tabIcons[5][1] = R.drawable.saturday_on;
	  		tabIcons[6][0] = R.drawable.sunday_off;
	  		tabIcons[6][1] = R.drawable.sunday_on;
	  		
	  		return  tabIcons;
	    }

	
    private List<Fragment> createDayTabsFragments(int clubId) {
    	  List<Fragment> listOfFragments = new ArrayList<Fragment>();
    	  for (int i=1; i<=7; i++) {
    		  listOfFragments.add(UIScheduleFragment.newInstance(i, dayTabsBarWidth, clubId));
    	  }
    	  
    	  return listOfFragments;
    }

	private void setupSpinners(View parentLayout) {
		//use buttons that look like spinners but they are easier to style and add "Select..." on-button prompt.
		
		Button buttonChooseClub = (Button)parentLayout.findViewById(R.id.buttonChooseClub);
		ArrayAdapter<Club> adapterChooseClub = new ArrayAdapter<Club>(this.getActivity(), R.layout.option_chooser_1line, clubs);
		buttonChooseClub.setOnClickListener(new ChooseAClubListener(buttonChooseClub, adapterChooseClub, parentLayout));

		Button buttonChooseTrainingType = (Button)parentLayout.findViewById(R.id.buttonChooseTrainingType);
		ArrayList<String> trainingTypes = new ArrayList<String>();
		trainingTypes.add(getResources().getString(R.string.all_trainings));
		ArrayAdapter<String> adapterChooseTrainingType = new ArrayAdapter<String>(this.getActivity(), R.layout.option_chooser_1line, trainingTypes);
		buttonChooseTrainingType.setOnClickListener(new ChooseTrainingTypeListener(buttonChooseTrainingType, adapterChooseTrainingType));

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		Utils.debug("Creating Schedule tab view.", true, true);
		View view = inflater.inflate(R.layout.tab_schedule_fragment, container, false);
		
		tabsWidgetLinearLayout = (LinearLayout)view.findViewById(R.id.tabsBarLinearLayout);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        setupSpinners(view);
        if ((clubs == null) || (clubs.get(0) == null)) {
        	Utils.debug("ERROR: cannot access club necessary to initialize schedule.", true, true);
        } else {
        	new BackgroundTaskCreateUISchedule(selectedClubId, view).execute("");
        }
        
		return view;
	}
	
	/**
	 * AsyncTask to create complex schedule UI in background, blocking the application with ProgressDialog.
	 */
	private class BackgroundTaskCreateUISchedule extends AsyncTask<String, Integer, String> {
		private ProgressDialog progressDialog;
		private int clubId;
		private View view;
		
		public BackgroundTaskCreateUISchedule(int clubId, View view) {
			this.clubId = clubId;
			this.view = view;
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setCancelable(true);
			progressDialog.setIndeterminate(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Inicjalizacja interfejsu harmonogramu, proszę czekać...");
		}
		
		@Override
		protected String doInBackground(String... params) {
			publishProgress(0);
			Utils.sleep(200); //hack to give ProgressDialog a chance to display faster
			return null;
		}
		
       @Override
       protected void onProgressUpdate(Integer... stage) {
    	   progressDialog.show();
       }
       
       @Override
       protected void onPostExecute(String result) {
	        if (viewPager == null) {
	        	Utils.debug("ERROR: couldn't get view pager.", true, true);
	        } else {
	   	    	//LinearLayout tabsWidgetLinearLayout = (LinearLayout)view.findViewById(R.id.tabsBarLinearLayout);
	   	    	dayTabFragments = createDayTabsFragments(clubId);
	   	    	UICustomTabWidget uiCustomTabWidget = new UICustomTabWidget(getActivity(), viewPager, getChildFragmentManager(), dayTabFragments, tabsWidgetLinearLayout, createListOfTabIcons(), dayTabsBarWidth, dayTabsBarHeight);
	        	uiCustomTabWidget.setCurrentPage(dayTabToSet);
	        }
	        progressDialog.dismiss();
            //load advertisements:
	        Utils.debug("View: " + view, true, true);
	        Utils.debug("(ImageView)view.findViewById: " + (ImageView)view.findViewById(R.id.imageViewAdvertTabSchedule), true, true);
	        Utils.debug("AchillesEngine.getInstance(): " + AchillesEngine.getInstance(), true, true);
	        Utils.debug("AchillesEngine.getInstance().getAllAds(): " + AchillesEngine.getInstance().getAllAds(), true, true);
            new UICustomAnimationDrawable((ImageView)view.findViewById(R.id.imageViewAdvertTabSchedule),
            							  AchillesEngine.getInstance().getAllAds(), 3000, getResources(), getActivity());
       }
	}
	
	/**
	 * Listener for club chooser.
	 */
	private class ChooseAClubListener implements OnClickListener {
		private Button buttonChooseClub;
		private ArrayAdapter<Club> adapterChooseClub;
		private View parentView;
		
		public ChooseAClubListener(Button buttonChooseClub, ArrayAdapter<Club> adapterChooseClub, View parentView) {
			this.buttonChooseClub = buttonChooseClub;
			this.adapterChooseClub = adapterChooseClub;
			this.parentView = parentView;
		}
		
		@Override
		public void onClick(View v) {
			  int selectedClubPosition = clubs.indexOf(achillesEngine.getClub(selectedClubId));
			  new AlertDialog.Builder(getActivity())
			  .setTitle(getResources().getString(R.string.choose_a_club))
			  .setSingleChoiceItems(adapterChooseClub, selectedClubPosition, new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int selectedPosition) {
				    	Utils.debug("Selected item: " + selectedPosition, true, true);
				    	buttonChooseClub.setText(clubs.get(selectedPosition).toString());
						selectedClubId =  clubs.get(selectedPosition).getId();
						
						if ((dayTabFragments == null) || (dayTabFragments.size() == 0)) {
							Utils.debug("Called too early, returning.", true, true);
							return;
						}
						dayTabToSet = viewPager.getCurrentItem();
						
				        if ((clubs == null) || (selectedPosition >= clubs.size()) || (clubs.get(selectedPosition) == null)) {
				        	Utils.debug("ERROR: cannot access club necessary to initialize schedule.", true, true);
				        } else {
							int clubId = clubs.get(selectedPosition).getId();
							Utils.debug("CLUB CHECK.", true, true);
							clubs.get(selectedPosition).printAll(true);
							
							tabsWidgetLinearLayout.removeAllViews();
							
							FragmentManager fragmentManager = getChildFragmentManager();
							FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
							for (Fragment f : dayTabFragments) {
								fragmentTransaction.remove(f);
							}
							fragmentTransaction.commit ();
							
							new BackgroundTaskCreateUISchedule(clubId, parentView).execute("");
				        }
						dialog.dismiss();
				    }
			 }).create().show();
		}
	}
	
	/**
	 * Listener for training type chooser.
	 */
	private class ChooseTrainingTypeListener implements OnClickListener {
		private Button buttonChooseTrainingType;
		private ArrayAdapter<String> adapterChooseTrainingType;
		
		public ChooseTrainingTypeListener(Button buttonChooseTrainingType, ArrayAdapter<String> adapterChooseTrainingType) {
			this.buttonChooseTrainingType = buttonChooseTrainingType;
			this.adapterChooseTrainingType = adapterChooseTrainingType;
		}
		
		@Override
		public void onClick(View v) {
			  final int selectedTrainingPosition = 0;
			  new AlertDialog.Builder(getActivity())
			  .setTitle(getResources().getString(R.string.choose_a_training_type))
			  .setSingleChoiceItems(adapterChooseTrainingType, selectedTrainingPosition, new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int selectedPosition) {
				    	Utils.debug("Selected item: " + selectedPosition, true, true);
				    	//code to display only selected type of trainigs goes here:
				    	buttonChooseTrainingType.setText(adapterChooseTrainingType.getItem(selectedTrainingPosition));
						dialog.dismiss();
				    }
			 }).create().show();
		}
	}

}
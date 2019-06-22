package com.thisonline.achilles;

import java.util.List;
import com.thisonline.achilles.middleware.AchillesEngine;
import com.thisonline.achilles.middleware.Club;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ClubsListFragment extends ListFragment {

	String listClubs = "";
	PopupWindow pw;
	List<Club> clubs;
	AchillesEngine achillesEngine;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		achillesEngine = AchillesEngine.getInstance();
		clubs = achillesEngine.getAllClubs();

		setListAdapter(new ClubsListAdapter(getActivity(),
				R.layout.clubsview_layout, clubs));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setCacheColorHint(Color.TRANSPARENT);
		getListView().setBackgroundColor(Color.TRANSPARENT);
		// getListView().setOverscrollFooter(null); //API 9 only
	}

	class ClubsListAdapter extends ArrayAdapter<Club> {

		private int layoutResourceId;

		public ClubsListAdapter(Context context, int layoutResourceId,
				List<Club> clubs) {
			super(context, layoutResourceId, clubs);
			this.layoutResourceId = layoutResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (getActivity().getLayoutInflater());
			View row = inflater.inflate(layoutResourceId, parent, false);
			ImageView icon = (ImageView) row.findViewById(R.id.clubicon);
			TextView clubname = (TextView) row.findViewById(R.id.clubname);
			Club c = clubs.get(position);
			clubname.setText(c.getName());
			icon.setImageBitmap(c.getIcon());
			return (row);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		/*
		 * String s = String.valueOf(id); Toast t =
		 * Toast.makeText(getActivity(), "Opcja niedostepna w wersji DEMO",
		 * Toast.LENGTH_SHORT); t.show();
		 */
		LayoutInflater inflater = (getActivity().getLayoutInflater());
		View popupView = inflater.inflate(R.layout.clubs_popup_window, null);

		TextView clubName = (TextView) popupView
				.findViewById(R.id.textClubName);
		ImageView clubImage = (ImageView) popupView.findViewById(R.id.clubIcon);
		TextView textClubDescription = (TextView) popupView
				.findViewById(R.id.textClubDescription);
		TextView textClubAddress = (TextView) popupView
				.findViewById(R.id.textClubAddress);
		TextView textClubOpenHours = (TextView) popupView
				.findViewById(R.id.textClubOpenHours);
		TextView textClubEmail = (TextView) popupView
				.findViewById(R.id.textClubEmail);
		TextView textClubPhoneNumber = (TextView) popupView
				.findViewById(R.id.textClubPhoneNumber);
		TextView textClubUrl = (TextView) popupView
				.findViewById(R.id.textClubUrl);
		textClubUrl.setLinksClickable(true);
		textClubUrl.setMovementMethod(LinkMovementMethod.getInstance());

		Club c = clubs.get(position);
		clubName.setText(c.getName());
		clubImage.setImageBitmap(c.getIcon());
		textClubDescription.setText(c.getDescription());
		textClubAddress.setText(c.getAddress());
		textClubOpenHours.setText(c.getOpenHoursInHumanFriendlyFormat());
		textClubEmail.setText(c.getEmail());
		textClubPhoneNumber.setText(c.getPhoneNumber());

		String url = "<a href='" + c.getUrl() + "'> " + c.getUrl() + " </a>";
		textClubUrl.setText(Html.fromHtml(url));

		pw = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);

		pw.showAtLocation(getActivity().findViewById(R.id.imageView1),
				Gravity.CENTER, 0, 0);
		Button backButton = (Button) popupView
				.findViewById(R.id.clubsPopupBackButton);
		backButton.setOnClickListener(backButtonListener);
	}

	public OnClickListener backButtonListener = new OnClickListener() {
		public void onClick(View v) {
			pw.dismiss();
		}
	};

	public void onBackPressed() {
		/*
		 * if( this.getFragmentManager().getBackStackEntryCount() != 0 ){
		 * this.getFragmentManager().popBackStack(); return true;
		 */
		Toast t = Toast.makeText(getActivity(), "back", Toast.LENGTH_SHORT);
		t.show();

	}

}

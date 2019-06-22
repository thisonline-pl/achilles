package com.thisonline.achilles;

import java.util.ArrayList;
import java.util.List;
import com.thisonline.achilles.middleware.AchillesEngine;
import com.thisonline.achilles.middleware.Club;
import com.thisonline.achilles.middleware.NewsElement;
import com.thisonline.achilles.middleware.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class NewsListFragment extends ListFragment {

	String listClubs = "";
	PopupWindow pw;
	AchillesEngine achillesEngine;
	ArrayList<NewsElement> allNews;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		achillesEngine = AchillesEngine.getInstance();
		allNews = achillesEngine.getAllNews();

		setListAdapter(new NewsListAdapter(getActivity(),
				R.layout.newsview_layout, allNews));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setCacheColorHint(Color.TRANSPARENT);
		getListView().setBackgroundColor(Color.TRANSPARENT);
		// getListView().setOverscrollFooter(null); API 9 only
	}

	class NewsListAdapter extends ArrayAdapter<NewsElement> {

		private int layoutResourceId;

		public NewsListAdapter(Context context, int layoutResourceId,
				ArrayList<NewsElement> allNews) {
			super(context, layoutResourceId, allNews);
			this.layoutResourceId = layoutResourceId;
			// this.allNews = allNews;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (getActivity().getLayoutInflater());
			View row = inflater.inflate(layoutResourceId, parent, false);
			ImageView icon = (ImageView) row.findViewById(R.id.newsicon);
			TextView clubname = (TextView) row.findViewById(R.id.newsname);
			clubname.setGravity(Gravity.LEFT);
			NewsElement n = allNews.get(position);
			int clubId = n.getClubId();
			Club c = achillesEngine.getClub(clubId);
			/*
			 * Utils.debug("CLUB id: " + clubId + ", name: " + c.getName() +
			 * ", NEWS id: " + n.getId() + ", title: " + n.getTitle(), true,
			 * true);
			 */
			clubname.setText(n.getTitle());
			icon.setImageBitmap(c.getIcon());

			return (row);
		}
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		LayoutInflater inflater = (getActivity().getLayoutInflater());
		View popupView = inflater.inflate(R.layout.news_popup_window, null);

		ImageView newsImage = (ImageView) popupView
				.findViewById(R.id.newsImage);
		TextView clubName = (TextView) popupView.findViewById(R.id.clubName);
		TextView newsTitle = (TextView) popupView.findViewById(R.id.newsTitle);
		TextView newsDescription = (TextView) popupView
				.findViewById(R.id.newsDescription);
		TextView newsUrl = (TextView) popupView.findViewById(R.id.newsUrl);
		newsUrl.setLinksClickable(true);
		newsUrl.setMovementMethod(LinkMovementMethod.getInstance());

		NewsElement n = allNews.get(position);
		int clubId = n.getClubId();
		Club c = achillesEngine.getClub(clubId);
		Utils.debug("CLUB id: " + clubId + ", name: " + c.getName()
				+ ", NEWS id: " + n.getId() + ", title: " + n.getTitle(), true,
				true);

		Bitmap newsImageBitmap = n.getImage();
		if (newsImageBitmap == null) {
			Utils.debug("news image is null", true, true);
			newsImageBitmap = c.getIcon();
		} else {
			Utils.debug("news image is NOT null", true, true);
		}
		newsImage.setImageBitmap(newsImageBitmap);
		clubName.setText(c.getName());
		newsTitle.setText(n.getTitle());
		newsDescription.setText(n.getDescription());

		String url = "<a href='" + c.getUrl() + "'> " + c.getUrl() + " </a>";
		newsUrl.setText(Html.fromHtml(url));

		pw = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		pw.setTouchable(true);
		pw.setFocusable(false);
		pw.setOutsideTouchable(true);

		// pw.showAsDropDown(popupView);
		pw.showAtLocation(getActivity().findViewById(R.id.imageView1),
				Gravity.CENTER, 0, 0);
		Button backButton = (Button) popupView
				.findViewById(R.id.newsPopupBackButton);
		backButton.setOnClickListener(backButtonListener);

	}

	public OnClickListener backButtonListener = new OnClickListener() {
		public void onClick(View v) {
			pw.dismiss();
		}
	};

	/*
	 * public void onKeyDown(int keyCode, KeyEvent event) { if (keyCode ==
	 * KeyEvent.KEYCODE_BACK) { pw.dismiss(); } }
	 */

}

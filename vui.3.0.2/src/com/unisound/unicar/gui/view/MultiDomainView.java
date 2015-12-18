package com.unisound.unicar.gui.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.SessionPreference;

public class MultiDomainView extends FrameLayout implements ISessionView {
	public static final String TAG = "MultiDomainView";

	private HashMap<String, String> mDomainNameMap = null;
	private static final HashMap<String, Integer> mDomainIconRes = new HashMap<String, Integer>();

	private void initDomainNameMap() {
		if (mDomainNameMap == null) {
			mDomainNameMap = new HashMap<String, String>();
		}
		Resources res = getResources();
		mDomainNameMap.clear();
		mDomainNameMap.put(SessionPreference.DOMAIN_ALARM,
				res.getString(R.string.domain_alarm));
		mDomainNameMap.put(SessionPreference.DOMAIN_APP,
				res.getString(R.string.domain_app));
		mDomainNameMap.put(SessionPreference.DOMAIN_CALL,
				res.getString(R.string.domain_call));
		mDomainNameMap.put(SessionPreference.DOMAIN_CONTACT,
				res.getString(R.string.domain_contact));
		mDomainNameMap.put(SessionPreference.DOMAIN_CONTACT_SEND,
				res.getString(R.string.domain_contact));
		mDomainNameMap.put(SessionPreference.DOMAIN_FLIGHT,
				res.getString(R.string.domain_flight));
		mDomainNameMap.put(SessionPreference.DOMAIN_INCITY_SEARCH,
				res.getString(R.string.domain_local_search));
		mDomainNameMap.put(SessionPreference.DOMAIN_MUSIC,
				res.getString(R.string.domain_music));
		mDomainNameMap.put(SessionPreference.DOMAIN_NEARBY_SEARCH,
				res.getString(R.string.domain_nearby_search));
		mDomainNameMap.put(SessionPreference.DOMAIN_POSITION,
				res.getString(R.string.domain_position));
		mDomainNameMap.put(SessionPreference.DOMAIN_ROUTE,
				res.getString(R.string.domain_position));
		mDomainNameMap.put(SessionPreference.DOMAIN_REMINDER,
				res.getString(R.string.domain_alarm));
		mDomainNameMap.put(SessionPreference.DOMAIN_SEARCH,
				res.getString(R.string.domain_nearby_search));
		mDomainNameMap.put(SessionPreference.DOMAIN_SITEMAP,
				res.getString(R.string.domain_sitemap));
		mDomainNameMap.put(SessionPreference.DOMAIN_SMS,
				res.getString(R.string.domain_sms));
		mDomainNameMap.put(SessionPreference.DOMAIN_STOCK,
				res.getString(R.string.domain_stock));
		mDomainNameMap.put(SessionPreference.DOMAIN_TRAIN,
				res.getString(R.string.domain_train));
		mDomainNameMap.put(SessionPreference.DOMAIN_TV,
				res.getString(R.string.domain_tv));
		mDomainNameMap.put(SessionPreference.DOMAIN_WEATHER,
				res.getString(R.string.domain_weather));
		mDomainNameMap.put(SessionPreference.DOMAIN_YELLOWPAGE,
				res.getString(R.string.domain_yellow_page));
		mDomainNameMap.put(SessionPreference.DOMAIN_WEBSEARCH,
				res.getString(R.string.domain_nearby_search));
		mDomainNameMap.put(SessionPreference.DOMAIN_NEWS,
				res.getString(R.string.domain_news));
		mDomainNameMap.put(SessionPreference.DOMAIN_COOKBOOK,
				res.getString(R.string.domain_cookbook));
		mDomainNameMap.put(SessionPreference.DOMAIN_TRANSLATION,
				res.getString(R.string.domain_translation));
		mDomainNameMap.put(SessionPreference.DOMAIN_DIANPING,
				res.getString(R.string.domain_local_search));
		mDomainNameMap.put(SessionPreference.DOMAIN_SETTING,
				res.getString(R.string.domain_setting));
		mDomainNameMap.put(SessionPreference.DOMAIN_MOVIE,
				res.getString(R.string.domain_movie));
		mDomainNameMap.put(SessionPreference.DOMAIN_NOVEL,
				res.getString(R.string.domain_novel));
		mDomainNameMap.put(SessionPreference.DOMAIN_VIDEO,
				res.getString(R.string.domain_video));
	}

	private MultiDomainViewListener mMultiDomainViewListener = null;

	private ArrayList<View> mItemViews = new ArrayList<View>();
	private ArrayList<View> mDividerItemViews = new ArrayList<View>();

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mMultiDomainViewListener != null) {
				mMultiDomainViewListener.onChoose((String) v.getTag());
			}
		}
	};

	public MultiDomainView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.multidomain_content_view, this, true);
		// findViews();
	}

	private void findViews() {
		mItemViews.add(findViewById(R.id.lyMultiDomainItem1));
		mItemViews.add(findViewById(R.id.lyMultiDomainItem2));
		mItemViews.add(findViewById(R.id.lyMultiDomainItem3));
		mItemViews.add(findViewById(R.id.lyMultiDomainItem4));

		mDividerItemViews.add(findViewById(R.id.lyMultiDomainDivider1));
		mDividerItemViews.add(findViewById(R.id.lyMultiDomainDivider2));
		mDividerItemViews.add(findViewById(R.id.lyMultiDomainDivider3));
	}

	public int setMultiDomainData(String[] domains, MultiDomainViewListener l) {
		mMultiDomainViewListener = l;
		if (domains == null || domains.length <= 0) {
			return -1;
		}

		if (mDomainNameMap == null || mDomainNameMap.size() == 0) {
			initDomainNameMap();
		}
		ArrayList<String> domainArray = new ArrayList<String>();

		for (int i = 0; i < domains.length; i++) {
			String d = domains[i];

			if (mDomainNameMap.containsKey(d)) {
				domainArray.add(d);
			}
		}

		if (domainArray.size() <= 0) {
			return -1;
		}

		if (domainArray.size() == 1 && mMultiDomainViewListener != null) {
			mMultiDomainViewListener.onChoose(domainArray.get(0));
			return 0;
		}

		String domain = "";

		for (int i = 3; i >= 0; i--) {
			View v = mItemViews.get(i);
			ImageView imgView = (ImageView) v
					.findViewById(R.id.imgMultiDomainIcon);
			TextView txtView = (TextView) v
					.findViewById(R.id.txtMultiDomainText);

			if (domainArray.size() > i) {
				domain = domainArray.get(i);
				imgView.setImageResource(mDomainIconRes.get(domain));
				txtView.setText(mDomainNameMap.get(domain));
				v.setTag(domain);
				v.setOnClickListener(mOnClickListener);
			} else {
				v.setVisibility(View.GONE);
				if (i - 1 >= 0) {
					mDividerItemViews.get(2).setVisibility(View.GONE);
				}
			}
		}

		return 0;
	}

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {

	}

	public interface MultiDomainViewListener {
		void onChoose(String domain);
	}
}

package com.unisound.unicar.gui.view;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.AssistantPreference;
import com.unisound.unicar.gui.utils.ImageDownloader;

public class GeneralAnswerView extends FrameLayout implements ISessionView {
	private View mImageGroup, mTextGroup, mURLGroup;

	private ImageView mImageViewGeneralImage;
	private TextView mTextViewGeneralText;
	private TextView mTextViewGeneralURLAlt;

	private String mText;
	private String mImageURL, mImageAlt;
	private String mURL, mURLAlt;

	private Context mContext;
	private ImageDownloader mImageDownloader;

	public GeneralAnswerView(Context context) {
		super(context);
		mContext = context;
		mImageDownloader = new ImageDownloader(
				AssistantPreference.FOLDER_PACKAGE_CACHE
						+ AssistantPreference.FOLDER_IMG, 0);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.general_answer_view, this, true);
		findViews();
		setListener();
	}

	private void findViews() {
		mImageGroup = (View) findViewById(R.id.imgViewGeneral);
		mTextGroup = (View) findViewById(R.id.lyGeneralText);
		mURLGroup = (View) findViewById(R.id.lyGeneralURL);

		mImageViewGeneralImage = (ImageView) findViewById(R.id.imgViewGeneral);
		mTextViewGeneralText = (TextView) findViewById(R.id.textViewSessionAnswer);
		mTextViewGeneralURLAlt = (TextView) findViewById(R.id.textViewGeneralURLAlt);
	}

	private void setListener() {
		mURLGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gotoURL();
			}
		});
	}

	public void setGeneralData(String text, String imgURL, String imgAlt,
			String url, String urlAlt) {
		mText = text;
		mImageURL = imgURL;
		mImageAlt = imgAlt;
		mURL = url;
		mURLAlt = urlAlt;

		refreshView();
	}

	private void refreshView() {
		if (mImageURL == null || mImageURL.equals("")) {
			mImageGroup.setVisibility(View.GONE);
		} else {
			mImageViewGeneralImage.setContentDescription(mImageAlt);
			mImageDownloader.download(mImageURL, mImageViewGeneralImage, true,
					ImageDownloader.SCALE_BY_IMAGEVIEW_WIDTH);
		}
		if (mText == null || mText.equals("")) {
			mTextGroup.setVisibility(View.GONE);
		} else {
			mTextViewGeneralText.setText(mText);
		}
		if (mURL == null || mURL.equals("")) {
			mURLGroup.setVisibility(View.GONE);
		} else {
			mTextViewGeneralURLAlt.setText(mURLAlt);
		}
	}

	@Override
	public boolean isTemporary() {
		return false;
	}

	@Override
	public void release() {

	}

	private void gotoURL() {
		if (mContext != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(mURL));

			if (intent != null) {
				PackageManager packageManager = mContext.getPackageManager();
				List<ResolveInfo> apps = packageManager.queryIntentActivities(
						intent, 0);
				if (apps != null && apps.size() > 0) {
					mContext.startActivity(intent);
				} else {
					Toast.makeText(mContext, R.string.view_sorry,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}

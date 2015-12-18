package com.unisound.unicar.gui.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

public class MingPianView extends FrameLayout implements ISessionView{
	
	private Handler mHandler = new Handler();
	int textIndex;
	String subText;
	public MingPianView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.mingpian, this, true);
		findViews();
	}
	public MingPianView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MingPianView(Context context) {
		this(context, null);
	}
	private ImageView mImageView;
	private TextView mTextView;
	

	private void findViews() {
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mTextView = (TextView) findViewById(R.id.textView1);
	}
	public void setResouse(final String text,int id){
		mImageView.setBackgroundResource(id);
		try {
			((AnimationDrawable)mImageView.getBackground()).start();
		} catch (Exception e) {
		}
		textIndex = 0;
		subText = null;
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if(textIndex != text.length()){
					textIndex++;
					subText = text.substring(0, textIndex);
					mTextView.setText(subText);
					postDelayed(this, 200);
				}
			}
		});
	}
	

	@Override
	public boolean isTemporary() {
		return true;
	}

	@Override
	public void release() {
		if(mImageView!=null){
			try {
				((AnimationDrawable)mImageView.getBackground()).stop();
			} catch (Exception e) {
			}
		}
		
	}

}

package com.unisound.unicar.gui.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.coogo.inet.vui.assistant.car.R;

/**
 * 
 * @author xiaodong
 * @date 20150715
 */
public class AboutActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		
		ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
        returnBtn.setOnClickListener(mReturnListerner);
        
//		TextView textViewVersion = (TextView) findViewById(R.id.tv_version);
//		textViewVersion.setText(String.format(textViewVersion.getText().toString(), RomDevice.getAppVersionName(this)));
	}
	
    private OnClickListener mReturnListerner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};
	
}

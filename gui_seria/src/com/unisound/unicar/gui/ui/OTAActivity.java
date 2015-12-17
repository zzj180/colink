package com.unisound.unicar.gui.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.DeviceTool;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150720
 */
public class OTAActivity extends Activity {

    private static final String TAG = OTAActivity.class.getSimpleName();

    private TextView tvVersion;
    private TextView tvNewestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota);

        ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
        returnBtn.setOnClickListener(mReturnListerner);

        tvVersion = (TextView) findViewById(R.id.tv_ota_app_version);
        tvVersion.setText(getString(R.string.setting_ota_text_version,
                DeviceTool.getAppVersionName(this)));

        tvNewestVersion = (TextView) findViewById(R.id.tv_ota_newest_version);

    }

    private OnClickListener mReturnListerner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.backBtn) {
                onBackPressed();
            }
        }
    };

    private boolean isNeedOTA() {
        boolean isNeedOTA = false;
        // TODO:
        Logger.d(TAG, "!--->---checkOTA()----------isNeedOTA = " + isNeedOTA);
        return isNeedOTA;
    }

    private void doOTA() {
        Logger.d(TAG, "!--->---doOTA()----------");
        // TODO:


    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (isNeedOTA()) {
            doOTA();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

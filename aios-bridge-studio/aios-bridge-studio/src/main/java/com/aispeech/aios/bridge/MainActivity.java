package com.aispeech.aios.bridge;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.aispeech.aios.bridge.R;
import com.aispeech.aios.sdk.manager.AIOSSystemManager;

public class MainActivity extends Activity {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
//        String versionText = String.format(getString(R.string.sdk_version), SDKBuild.VERSION.NAME);
        String versionText = "欢迎使用AIOS for car SDK";
        textView = (TextView) findViewById(R.id.text_version_info);
        textView.setText(versionText);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = Settings.System.getInt(getContentResolver(),"TTS_SHOW",0)==1;
                if(show){
                    AIOSSystemManager.getInstance().stopInteraction();
                }else{
                    AIOSSystemManager.getInstance().startInteraction();
                }
            }
        });
        // 添加ListItem，设置事件响应
    }




    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

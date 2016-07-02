package com.aispeech.aios.adapter.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.aispeech.ailog.AILog;
import com.aispeech.aimusic.AIMusic;
import com.aispeech.aimusic.config.MusicConfig;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.adapter.HelpInfoAdapter;
import com.aispeech.aios.adapter.adapter.SpinnerAdapter;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.util.PreferenceHelper;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @desc 设置和帮助页面Layout
 * @auth zzj
 * @date 2016-02-16
 */
public class SettingsHelpLayout extends LinearLayout {

    private final String TAG = "SettingsHelpLayout";
    private static final int EVENT_REFRESH_TIME = 1;
    private Context mContext;
    private HelpInfoAdapter mHelpInfoAdapter;

    private TextView mSettingsTitleText;
    private ListView mHelpListView;
    private LinearLayout mSettingMiddleLayout;
    private Spinner mPhoneSpinner;
    private Spinner mMusicSpinner;
    private Spinner mMapSpinner;
    private Spinner mWakeupSpinner;

    private TextView mCopyRightTextCN;
    private TextView mCopyRightTextEN;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case EVENT_REFRESH_TIME:
                    int i = (Integer) msg.obj;
                    int times = 2016;
                    if (i >= 2016) {//如果网络获得的时间大于等于2016
                        PreferenceHelper.getInstance().setTime(i);
                        times = i;

                    } else if (i == 0) {//如果没有从网络获取的时间就从本地获得时间
                        i = PreferenceHelper.getInstance().getTime();

                    } else {
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.ENGLISH);
                        times = Integer.valueOf(format.format(date));
                    }

                    mCopyRightTextCN.setText(getResources().getString(R.string.settings_copyright_cn, times));
                    mCopyRightTextEN.setText(getResources().getString(R.string.settings_copyright_en, times));
                    break;
            }
        }
    };

    public SettingsHelpLayout(Context context) {
        this(context, null);
    }

    public SettingsHelpLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingsHelpLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initViews();
        initDatas();
        initSettingView();
    }

    private void initViews() {
        LayoutInflater.from(mContext).inflate(R.layout.setting_layout, this);
        mHelpInfoAdapter = new HelpInfoAdapter(mContext);

        mSettingsTitleText = (TextView) findViewById(R.id.txt_setting_title);
        mHelpListView = (ListView) findViewById(R.id.setting_help_list);
        mSettingMiddleLayout = (LinearLayout) findViewById(R.id.setting_middle);
        mHelpListView.setAdapter(mHelpInfoAdapter);

        mCopyRightTextCN = (TextView) findViewById(R.id.tv_setting_time_cn);
        mCopyRightTextEN = (TextView) findViewById(R.id.tv_setting_time_en);

        mPhoneSpinner = (Spinner) findViewById(R.id.spinner_setting_phone);
        mMusicSpinner = (Spinner) findViewById(R.id.spinner_setting_music);
        mMapSpinner = (Spinner) findViewById(R.id.spinner_setting_map);
        mWakeupSpinner = (Spinner) findViewById(R.id.spinner_setting_wakeup);
    }

    private void initDatas() {
        SpinnerAdapter mPhoneAdapter = new SpinnerAdapter(mContext, mContext.getResources().getStringArray(R.array.phones));
        SpinnerAdapter mMusicAdapter = new SpinnerAdapter(mContext, mContext.getResources().getStringArray(R.array.musics));
        SpinnerAdapter mMapAdapter = new SpinnerAdapter(mContext, mContext.getResources().getStringArray(R.array.maps));
        SpinnerAdapter mWakeupAdapter = new SpinnerAdapter(mContext, mContext.getResources().getStringArray(R.array.switch_wakeup));

        mPhoneSpinner.setAdapter(mPhoneAdapter);
        mMusicSpinner.setAdapter(mMusicAdapter);
        mMapSpinner.setAdapter(mMapAdapter);
        mWakeupSpinner.setAdapter(mWakeupAdapter);
    }

    /**
     * 左下角帮助/设置按钮点击后的方法
     *
     * @param tipsType  当前处于哪个领域
     * @param isClicked 是否初次被点击 true：已点击帮助按钮，将显示设置按钮；false：点击了设置按钮，回归帮助状态
     */
    public void onHelpSettingsClicked(int tipsType, boolean isClicked) {
        if (!isClicked) {
            mHelpInfoAdapter.setTipsType(tipsType);
            mHelpInfoAdapter.notifyDataSetChanged();
            mSettingsTitleText.setText(mContext.getString(R.string.help_title));
            mSettingMiddleLayout.setVisibility(GONE);
            mHelpListView.setVisibility(VISIBLE);
        } else {
            mSettingsTitleText.setText(mContext.getString(R.string.settings_title));
            mSettingMiddleLayout.setVisibility(VISIBLE);
            mHelpListView.setVisibility(GONE);
        }
    }


    /**
     * 默认应用设置界面数据填充
     */
    private void initSettingView() {
        //电话切换监听器
        mPhoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AILog.d(TAG, "mPhoneSpinner onClick  :" + i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int defaultMusicType = PreferenceHelper.getInstance().getDefaultMusicType();
        AILog.d(TAG, " defaultMusicType  :" + defaultMusicType);

        if (defaultMusicType <= 2 && defaultMusicType >= 1) {//音乐类型读取成功
            mMusicSpinner.setSelection(defaultMusicType - 1);
            AIMusic.setMusicType(defaultMusicType);

        } else {

            mMusicSpinner.setSelection(0);
            AIMusic.setMusicType(MusicConfig.AIOS_MUSIC);
            PreferenceHelper.getInstance().setDefaultMusicType(MusicConfig.AIOS_MUSIC);
        }

        //音乐切换监听器
        mMusicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AILog.d(TAG, "mMusicSpinner onClick  :" + i);
                AIMusic.setMusicType(i + 1);
                PreferenceHelper.getInstance().setDefaultMusicType(i + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        int defaultMapType = Settings.System.getInt(AdapterApplication.getContext().getContentResolver(),"MAP_INDEX", MapConfig.GDMAP);//int
        String mapType = Configs.getMapName(defaultMapType);
        AILog.d(TAG, "读取默认地图设置：" + mapType);

        final String[] maps = mContext.getResources().getStringArray(R.array.maps);
        for (int i = 0; i < maps.length; i++) {
            if (maps[i].equals(mapType)) {
                mMapSpinner.setSelection(i, true);//设置默认项
            }
        }
        //地图切换监听器
        mMapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int mp = Settings.System.getInt(AdapterApplication.getContext().getContentResolver(),"MAP_INDEX", MapConfig.GDMAP);

                String mapType = maps[i];//选中的地图类型
                AILog.d(TAG, "mMapSpinner onClick  :" + mapType);
                if (mapType.equals("高德地图")) {
                    mp = Configs.MapConfig.GDMAP;
                } else if (mapType.contains("百度地图")) {
                    mp = Configs.MapConfig.BDMAP;
                } else if (mapType.contains("凯立德")) {
                    mp = Configs.MapConfig.KLDMAP;
                } else if (mapType.contains("图吧")) {
                    mp = Configs.MapConfig.TBMAP;
                } else if (mapType.contains("百度导航")) {
                    mp = Configs.MapConfig.BDDH;
                } else if (mapType.equals("高德地图车机版")) {
                    mp = Configs.MapConfig.GDMAPFORCAT;
                }
                Settings.System.putInt(AdapterApplication.getContext().getContentResolver(),"MAP_INDEX", mp);
                 //存储当前设置的地图类型
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            	
            }
        });
        
        mWakeupSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				PreferenceHelper.getInstance().setWakeUpEnable(position==0);
				HomeNode.getInstance().setWakeUp(position==0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        	
		});

        boolean wakeUpEnable = PreferenceHelper.getInstance().getWakeUpEnable();
        mWakeupSpinner.setSelection(wakeUpEnable?0:1,true);
        
        initTime();
    }

    private void initTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                try {
                    URL url = new URL("http://www.baidu.com");
                    URLConnection uc = url.openConnection();// 生成连接对象
                    uc.connect(); // 发出连接
                    long ld = uc.getDate(); // 取得网站日期时间
                    Date date = new Date(ld); // 转换为标准时间对象
                    // 分别取得时间中的小时，分钟和秒，并输出
                    time = date.getYear() + 1900;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }// 取得资源对象
                Message msg = new Message();
                msg.obj = time;//携带数据
                msg.what = EVENT_REFRESH_TIME;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}

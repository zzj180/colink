package com.unisound.unicar.gui.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;

/**
 * 
 * @author xiaodong
 * @date 20150715
 */
public class SettingMapViewPagerActivity extends Activity {

    private static final String TAG = SettingMapViewPagerActivity.class.getSimpleName();

    private Context mContext;

    private ArrayList<View> mViewList = new ArrayList<View>();
    private LayoutInflater mLayoutInflater;
    private ViewGroup indicatorViewGroup;

    private ImageView mImageView;
    private ImageView[] mImageViews;
    private ViewPager mViewPager;

    private FrameLayout fl_setting_map_gaode;
    private FrameLayout fl_setting_map_baidu;
    private FrameLayout fl_setting_map_tuba;
    private FrameLayout fl_setting_map_daodaotong;

    private ImageView mIvMapSelectedGaode;
    private ImageView mIvMapSelectedBaidu;
    private ImageView mIvMapSelectedTuba;
    private ImageView mIvMapSelecteDaodaotong;
    public final static String MAP_INDEX = "MAP_INDEX";
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Logger.d(TAG, "!--->onCreate()----");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_settings_view_pager);
        mLayoutInflater = getLayoutInflater();
        mContext = getApplicationContext();
        mIntent = getIntent();

        TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTopTitle.setText(R.string.setting_title_map_choose);
        ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
        returnBtn.setOnClickListener(mReturnListerner);

        // 添加layout
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_setting_map_layout_1, null));
        // mViewList.add(mLayoutInflater.inflate(R.layout.pager_setting_map_layout_2, null));

        initViewPager();
    }

    private OnClickListener mReturnListerner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.backBtn) {
                onBackPressed();
            }
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    private void initViewPager() {
        mImageViews = new ImageView[mViewList.size()];

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        indicatorViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

        if (null != mViewList && mViewList.size() < 2) {
            indicatorViewGroup.setVisibility(View.INVISIBLE);
            Logger.d(TAG, "!--->less than two page, dismiss indicator");
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_dot_normal);
        for (int i = 0; i < mViewList.size(); i++) {
            mImageView = new ImageView(SettingMapViewPagerActivity.this);
            mImageView.setLayoutParams(new LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
            mImageView.setPadding(0, 20, 0, 20);

            if (i == 0) {
                mImageView.setBackgroundResource(R.drawable.icon_dot_selected);
            } else {
                mImageView.setBackgroundResource(R.drawable.icon_dot_normal);
            }
            mImageViews[i] = mImageView;
            indicatorViewGroup.addView(mImageViews[i]);
        }
        bitmap.recycle();
        mViewPager.setOnPageChangeListener(mPageChangeLinstener);
    }

    private OnPageChangeListener mPageChangeLinstener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            // TODO Auto-generated method stub
            for (int i = 0; i < mImageViews.length; i++) {
                if (i == arg0) {
                    mImageViews[i].setBackgroundResource(R.drawable.icon_dot_selected);
                } else {
                    mImageViews[i].setBackgroundResource(R.drawable.icon_dot_normal);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case ViewPager.SCROLL_STATE_IDLE:

                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:

                    break;
                default:
                    break;
            }
        }
    };


    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            switch (position) {
                case 0:
                    ((ViewPager) container).addView(mViewList.get(position), 0);

                    fl_setting_map_gaode = (FrameLayout) findViewById(R.id.fl_setting_map_gaode);
                    fl_setting_map_baidu = (FrameLayout) findViewById(R.id.fl_setting_map_baidu);
                    fl_setting_map_tuba = (FrameLayout) findViewById(R.id.fl_setting_map_tuba);
                    fl_setting_map_daodaotong =
                            (FrameLayout) findViewById(R.id.fl_setting_map_daodaotong);

                    mIvMapSelectedGaode = (ImageView) findViewById(R.id.iv_map_selected_gaode);
                    mIvMapSelectedBaidu = (ImageView) findViewById(R.id.iv_map_selected_baidu);
                    mIvMapSelectedTuba = (ImageView) findViewById(R.id.iv_map_selected_tuba);
                    mIvMapSelecteDaodaotong =
                            (ImageView) findViewById(R.id.iv_map_selected_daodaotong);

                    // set listener
                    fl_setting_map_gaode.setOnClickListener(mOnClickListener);
                    fl_setting_map_baidu.setOnClickListener(mOnClickListener);
                    fl_setting_map_tuba.setOnClickListener(mOnClickListener);
                    fl_setting_map_daodaotong.setOnClickListener(mOnClickListener);

                    // update UI Status
                    int mapType = UserPerferenceUtil.getMapChoose(mContext);
                    updateMapUIStatus(mapType);

                    break;

                case 1:
                    ((ViewPager) container).addView(mViewList.get(position), 1);

                    break;
            }

            return mViewList.get(position);
        }


        private OnClickListener mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d(TAG, "!--->onClick = " + v.getId());
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.fl_setting_map_gaode:
                        Logger.d(TAG, "!--->---click gaode----");
                        Settings.System.putInt(mContext.getContentResolver(), MAP_INDEX, 1);
                        onMapItemClicked(UserPerferenceUtil.VALUE_MAP_AMAP);
                        break;
                    case R.id.fl_setting_map_baidu:
                        Logger.d(TAG, "!--->---click baidu----");
                        Settings.System.putInt(mContext.getContentResolver(), MAP_INDEX, 0);
                        onMapItemClicked(UserPerferenceUtil.VALUE_MAP_BAIDU);
                        break;
                    case R.id.fl_setting_map_tuba:
                        Logger.d(TAG, "!--->---click tuba----");
                        onMapItemClicked(UserPerferenceUtil.VALUE_MAP_TUBA);
                        break;
                    case R.id.fl_setting_map_daodaotong:
                        Logger.d(TAG, "!--->---click daodaotong----");
                        onMapItemClicked(UserPerferenceUtil.VALUE_MAP_DAODAOTONG);
                        break;
                    default:
                        break;
                }
            }
        };

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }


    };

    private void updateMapUIStatus(int mapType) {
        Logger.d(TAG, "!--->updateMapUIStatus---mapType = " + mapType);
        switch (mapType) {
            case UserPerferenceUtil.VALUE_MAP_AMAP:
                mIvMapSelectedGaode.setVisibility(View.VISIBLE);
                mIvMapSelectedBaidu.setVisibility(View.INVISIBLE);
                mIvMapSelectedTuba.setVisibility(View.INVISIBLE);
                mIvMapSelecteDaodaotong.setVisibility(View.INVISIBLE);
                break;
            case UserPerferenceUtil.VALUE_MAP_BAIDU:
                mIvMapSelectedGaode.setVisibility(View.INVISIBLE);
                mIvMapSelectedBaidu.setVisibility(View.VISIBLE);
                mIvMapSelectedTuba.setVisibility(View.INVISIBLE);
                mIvMapSelecteDaodaotong.setVisibility(View.INVISIBLE);
                break;
            case UserPerferenceUtil.VALUE_MAP_TUBA:
                mIvMapSelectedGaode.setVisibility(View.INVISIBLE);
                mIvMapSelectedBaidu.setVisibility(View.INVISIBLE);
                mIvMapSelectedTuba.setVisibility(View.VISIBLE);
                mIvMapSelecteDaodaotong.setVisibility(View.INVISIBLE);
                break;
            case UserPerferenceUtil.VALUE_MAP_DAODAOTONG:
                mIvMapSelectedGaode.setVisibility(View.INVISIBLE);
                mIvMapSelectedBaidu.setVisibility(View.INVISIBLE);
                mIvMapSelectedTuba.setVisibility(View.INVISIBLE);
                mIvMapSelecteDaodaotong.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void onMapItemClicked(int mapType) {
        Logger.d(TAG, "!--->onMapItemClicked--mapType = " + mapType);
        UserPerferenceUtil.setMapChoose(mContext, mapType);
        updateMapUIStatus(mapType);
        SettingMapViewPagerActivity.this.setResult(
                GUIConfig.ACTIVITY_RESULT_CODE_SETTING_MAP_FINISH, mIntent);
        SettingMapViewPagerActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

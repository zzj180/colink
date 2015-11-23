package com.unisound.unicar.gui.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.GUIConfig;
import com.unisound.unicar.gui.utils.Logger;
import com.unisound.unicar.gui.view.ImageViewWithText;

/**
 * 
 * @author xiaodong
 * @date 20150609
 */
public class AllFunctionsActivity extends Activity {

    private static final String TAG = AllFunctionsActivity.class.getSimpleName();

    private ArrayList<View> mViewList = new ArrayList<View>();
    private LayoutInflater mLayoutInflater;
    private ViewGroup indicatorViewGroup;

    private ImageView mImageView;
    private ImageView[] mImageViews;
    private ViewPager mViewPager;

    private ImageViewWithText btnBluetooth;
    private ImageViewWithText btnNavigation;
    private ImageViewWithText btnMusic;
    private ImageViewWithText btnQuickSetting;
    private ImageViewWithText btnNetRadio;
    private ImageViewWithText btnWeather;
    private ImageViewWithText btnStock;
    private ImageViewWithText btnLocalSearch;

    // page2
    private ImageViewWithText btnTraffic;
    private ImageViewWithText btnLimit;
    private ImageViewWithText btnRadio;
    private ImageViewWithText btnWakeup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_function);
        mLayoutInflater = getLayoutInflater();

        ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
        returnBtn.setOnClickListener(mReturnListerner);

        // add layout
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_all_functions_layout_1, null));
        mViewList.add(mLayoutInflater.inflate(R.layout.pager_all_functions_layout_2, null));

        initViewPager();
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
            mImageView = new ImageView(AllFunctionsActivity.this);
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
        bitmap.recycle();// XD added 20150728
        mViewPager.setOnPageChangeListener(mPageChangeLinstener);
    }

    private OnPageChangeListener mPageChangeLinstener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
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
            /* < XiaoDong 20150721 added begin */
            int childCount = ((ViewPager) container).getChildCount();
            if (mViewList == null) {
                Logger.e(TAG, "!--->PagerAdapter instantiateItem error, mViewList is null.");
                return null;
            }
            if (null != mViewList && mViewList.size() < position) {
                Logger.e(TAG,
                        "!--->PagerAdapter instantiateItem error, return null. ViewList size = "
                                + mViewList.size() + "; position = " + position);
                return null;
            }
            Logger.d(TAG, "!--->PagerAdapter--position:" + position + "; childCount:" + childCount
                    + "; mViewList.size:" + mViewList.size());
            /* XiaoDong 20150721 added End > */
            switch (position) {
                case 0:
                    ((ViewPager) container).addView(mViewList.get(position), 0);
                    btnBluetooth = (ImageViewWithText) findViewById(R.id.btn_function_bluetooth);
                    btnNavigation = (ImageViewWithText) findViewById(R.id.btn_function_navigation);
                    btnMusic = (ImageViewWithText) findViewById(R.id.btn_function_music);
                    btnQuickSetting =
                            (ImageViewWithText) findViewById(R.id.btn_function_quick_setting);
                    btnNetRadio = (ImageViewWithText) findViewById(R.id.btn_function_net_radio);
                    btnWeather = (ImageViewWithText) findViewById(R.id.btn_function_weather);
                    btnStock = (ImageViewWithText) findViewById(R.id.btn_function_stock);
                    btnLocalSearch =
                            (ImageViewWithText) findViewById(R.id.btn_function_local_search);

                    btnBluetooth.setOnClickListener(mOnClickListener);
                    btnNavigation.setOnClickListener(mOnClickListener);
                    btnMusic.setOnClickListener(mOnClickListener);
                    btnQuickSetting.setOnClickListener(mOnClickListener);
                    btnNetRadio.setOnClickListener(mOnClickListener);
                    btnWeather.setOnClickListener(mOnClickListener);
                    btnStock.setOnClickListener(mOnClickListener);
                    btnLocalSearch.setOnClickListener(mOnClickListener);
                    break;

                case 1:
                    /* < XiaoDong 20150721 added Begin */
                    if (childCount == 0) {
                        Logger.w(TAG, "!--->position is 1 but childCount is 0");
                        ((ViewPager) container).addView(mViewList.get(position), 0);
                    }
                    /* XiaoDong 20150721 added End > */

                    ((ViewPager) container).addView(mViewList.get(position), 1);
                    btnTraffic = (ImageViewWithText) findViewById(R.id.btn_function_traffic);
                    btnLimit = (ImageViewWithText) findViewById(R.id.btn_function_limit);
                    btnRadio = (ImageViewWithText) findViewById(R.id.btn_function_radio);
                    btnWakeup = (ImageViewWithText) findViewById(R.id.btn_function_wakeup);

                    btnTraffic.setOnClickListener(mOnClickListener);
                    btnLimit.setOnClickListener(mOnClickListener);
                    btnRadio.setOnClickListener(mOnClickListener);
                    btnWakeup.setOnClickListener(mOnClickListener);
                    break;
            }
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            Logger.d(TAG, "destroyItem-----position = " + position);
            if (mViewList != null && mViewList.size() > 0 && mViewList.size() >= position) {
                Logger.d(TAG, "!--->destroyItem---mViewList size" + mViewList.size());
                ((ViewPager) container).removeView(mViewList.get(position));
            }
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    };

    private OnClickListener mReturnListerner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Logger.d(TAG, "!--->mOnClickListener--v.getId:" + v.getId());
            Intent intent = new Intent(AllFunctionsActivity.this, FunctionHelpActivity.class);
            intent.setAction(GUIConfig.ACTION_FUNCTION_HELP);
            int helpType = 0;
            CharSequence helpTitle = "";
            switch (v.getId()) {
            // page1 line1
                case R.id.btn_function_bluetooth:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_BLUETOOTH;
                    helpTitle = btnBluetooth.getShowText();
                    break;
                case R.id.btn_function_navigation:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_NAVIGATION;
                    helpTitle = btnNavigation.getShowText();
                    break;
                case R.id.btn_function_music:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_MUSIC;
                    helpTitle = btnMusic.getShowText();
                    break;
                case R.id.btn_function_quick_setting:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_SETTING;
                    helpTitle = btnQuickSetting.getShowText();
                    break;
                // page1 line2
                case R.id.btn_function_net_radio:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_NET_RADIO;
                    helpTitle = btnNetRadio.getShowText();
                    break;
                case R.id.btn_function_weather:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_WEATHER;
                    helpTitle = btnWeather.getShowText();
                    break;
                case R.id.btn_function_stock:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_STOCK;
                    helpTitle = btnStock.getShowText();
                    break;
                case R.id.btn_function_local_search:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_LOCAL_SEARCH;
                    helpTitle = btnLocalSearch.getShowText();
                    break;
                // Page2 line1
                case R.id.btn_function_traffic:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_TRAFFIC;
                    helpTitle = btnTraffic.getShowText();
                    break;
                case R.id.btn_function_limit:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_LIMIT;
                    helpTitle = btnLimit.getShowText();
                    break;
                case R.id.btn_function_radio:
                    helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_RADIO;
                    helpTitle = btnRadio.getShowText();
                    break;
                case R.id.btn_function_wakeup:
                	 helpType = GUIConfig.VALUE_FUNCTION_HELP_TYPE_WAKEUP;
                     helpTitle = btnWakeup.getShowText();
                	break;
                default:
                    break;
            }
            intent.putExtra(GUIConfig.KEY_FUNCTION_HELP_TYPE, helpType);
            intent.putExtra(GUIConfig.KEY_FUNCTION_HELP_TITLE, helpTitle);
            Logger.d(TAG, "!--->helpType = " + helpType + "; helpTitle = " + helpTitle);
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}

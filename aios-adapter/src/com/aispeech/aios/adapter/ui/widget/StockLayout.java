package com.aispeech.aios.adapter.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.adapter.StockAdapter;
import com.aispeech.aios.adapter.bean.StockBean;
import com.aispeech.aios.adapter.control.UITimer;
import com.aispeech.aios.adapter.control.UITimerTask;
import com.aispeech.aios.adapter.ui.view.StockChartView;
import com.aispeech.aios.adapter.util.MathUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Spring on 2016/2/15.
 * to do:
 */
public class StockLayout extends LinearLayout {
    private Context mContext;
    private TextView mStockValue, mStockFloatValue, mStockFloatRate, mStockCompany, mStockCode,
            mStockCompanyZoom, mStockCodeZoom, mStockTimeZoom;
    private ImageView mStockStatus;
    private StockChartView mStockChart;
    private ListView mStockDetail;
    private ScaleAnimation animation;
    private RelativeLayout mStockDes, mStockDesZoom, mStockChartLayout;
    //    private LinearLayout mStockAreaLayout;
    boolean zoomStatus = false;
    DateFormat df = new SimpleDateFormat("HH:mm");
    private DecimalFormat decformat = new DecimalFormat("0.00");
    Paint mPaint = new Paint();

    public StockLayout(Context context) {
        this(context, null);
    }

    public StockLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StockLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.stock_layout, this);
        mStockValue = (TextView) this.findViewById(R.id.c_stock_value);
        mStockFloatValue = (TextView) this.findViewById(R.id.c_stock_float_value);
        mStockFloatRate = (TextView) this.findViewById(R.id.c_stock_float_rate);
        mStockCompany = (TextView) this.findViewById(R.id.c_stock_company);
        mStockCode = (TextView) this.findViewById(R.id.c_stock_code);

        mStockStatus = (ImageView) this.findViewById(R.id.c_stock_status_icon);
        mStockChart = (StockChartView) this.findViewById(R.id.c_stock_chart);
        mStockChart.setOnClickListener(stockListener);

        mStockDetail = (ListView) this.findViewById(R.id.c_stock_detail);
        mStockDes = (RelativeLayout) this.findViewById(R.id.c_stock_des);

        mStockChartLayout = (RelativeLayout) this.findViewById(R.id.c_stock_chart_layout);

        mStockDesZoom = (RelativeLayout) this.findViewById(R.id.d_stock_des);
        mStockCompanyZoom = (TextView) this.findViewById(R.id.d_stock_company);
        mStockCodeZoom = (TextView) this.findViewById(R.id.d_stock_code);
        mStockTimeZoom = (TextView) this.findViewById(R.id.d_stock_time);

    }

    /**
     * 显示股票查询UI
     *
     * @param bean 股票实体bean
     */
    public void showStockUI(StockBean bean) {

        if (!TextUtils.isEmpty(bean.mBaseData.name)) {
            mStockCompany.setText(bean.mBaseData.name);
            mStockCompanyZoom.setText(bean.mBaseData.name);
        } else {
            mStockCompany.setText("");
            mStockCompanyZoom.setText("");
        }

        if (!TextUtils.isEmpty(bean.mBaseData.symbol)) {
            mStockCode.setText(bean.mBaseData.symbol);
            mStockCodeZoom.setText(bean.mBaseData.symbol);
        } else {
            mStockCode.setText("");
            mStockCodeZoom.setText("");
        }

        if (!TextUtils.isEmpty(bean.mBaseData.adate)) {
            mStockTimeZoom.setText(bean.mBaseData.adate);
        } else {
            mStockTimeZoom.setText("");
        }

        List<StockAdapter.StockItem> list = new ArrayList<StockAdapter.StockItem>();


        list.add(new StockAdapter.StockItem("yesPri", "昨收", String.valueOf(bean.mBaseData.lastClose)));
        list.add(new StockAdapter.StockItem("openPri", "今开", String.valueOf(bean.mBaseData.open)));
        list.add(new StockAdapter.StockItem("highPri", "最高", String.valueOf(bean.mBaseData.high)));
        list.add(new StockAdapter.StockItem("lowPri", "最低", String.valueOf(bean.mBaseData.low)));
        list.add(new StockAdapter.StockItem("dealNum", "成交量", String.valueOf(bean.mBaseData.avolume)));
        list.add(new StockAdapter.StockItem("dealPri", "成交额", String.valueOf(bean.mBaseData.amount)));

        DecimalFormat df = new DecimalFormat("0.00");

        boolean status = (bean.mBaseData.change > 0.0) ? true : false;

        mStockValue.setText(df.format(bean.mBaseData.current));
        mStockFloatValue.setText(df.format(bean.mBaseData.change));
        mStockFloatRate.setText(df.format(bean.mBaseData.percentage) + "%");

        if (status) {
            mStockValue.setTextColor(mContext.getResources().getColor(R.color.text_stock_red));
            mStockFloatValue.setTextColor(mContext.getResources().getColor(R.color.text_stock_red));
            mStockFloatRate.setTextColor(mContext.getResources().getColor(R.color.text_stock_red));
            mStockStatus.setImageResource(R.drawable.stock_up);
        } else {
            mStockValue.setTextColor(mContext.getResources().getColor(R.color.text_stock_green));
            mStockFloatValue.setTextColor(mContext.getResources().getColor(R.color.text_stock_green));
            mStockFloatRate.setTextColor(mContext.getResources().getColor(R.color.text_stock_green));
            mStockStatus.setImageResource(R.drawable.stock_down);
        }
        initStockChart(bean);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStockChart.getLayoutParams();

        layoutParams.width = MathUtil.getInstance().dip2px(mContext, 193);
        layoutParams.height = MathUtil.getInstance().dip2px(mContext, 142);

        StockAdapter adapter = new StockAdapter(mContext, list, status);
        mStockDetail.setAdapter(adapter);

        mStockDes.setVisibility(View.VISIBLE);
        mStockDetail.setVisibility(View.VISIBLE);
        mStockDesZoom.setVisibility(View.GONE);

        zoomStatus = false;
    }


    private void initStockChart(StockBean bean) {
        mStockChart.setAxisMarginBottom(MathUtil.getInstance().dip2px(mContext, 50));
        mStockChart.setShowEndTitles(false);
        mStockChart.setAxisMarginRight(1);
        if (bean == null || bean.mBaseData == null || bean.mStockList == null) {
            return;
        }

        List<String> ytitle = new ArrayList<String>();
        List<String> endTitle = new ArrayList<String>();

        if (bean.mBaseData.state.equals("suspend")) {
            for (int i = 0; i <= 4; i++) {
                ytitle.add("0.00");
                endTitle.add("0.00%");
            }
        } else {
            if (Math.abs(bean.mBaseData.high - bean.mBaseData.lastClose) > Math.abs(bean.mBaseData.low - bean.mBaseData.lastClose)) {
                float low = bean.mBaseData.lastClose - Math.abs(bean.mBaseData.high - bean.mBaseData.lastClose);
                float delta = Math.abs(bean.mBaseData.high - bean.mBaseData.lastClose) / 2;

                for (int i = 0; i <= 4; i++) {
                    if (i == 2) {
                        ytitle.add(String.valueOf(bean.mBaseData.lastClose));
                        endTitle.add(String.valueOf("0.00%"));
                    } else if (i < 2) {
                        ytitle.add(String.valueOf(low + i * delta));
                        endTitle.add("-" + decformat.format(delta * 100 / bean.mBaseData.lastClose / (i + 1)) + "%");
                    } else {
                        ytitle.add(String.valueOf(low + i * delta));
                        endTitle.add("+" + decformat.format(delta * 100 / bean.mBaseData.lastClose / (5 - i)) + "%");
                    }
                }

            } else {
                float low = bean.mBaseData.lastClose - Math.abs(bean.mBaseData.low - bean.mBaseData.lastClose);
                float delta = Math.abs(bean.mBaseData.low - bean.mBaseData.lastClose) / 2;

                for (int i = 0; i <= 4; i++) {
                    if (i == 2) {
                        ytitle.add(String.valueOf(bean.mBaseData.lastClose));
                        endTitle.add(String.valueOf("0.00%"));
                    } else if (i < 2) {
                        ytitle.add(String.valueOf(low + i * delta));
                        endTitle.add("-" + decformat.format(delta * 100 / bean.mBaseData.lastClose / (i + 1)) + "%");
                    } else {
                        ytitle.add(String.valueOf(low + i * delta));
                        endTitle.add("+" + decformat.format(delta * 100 / bean.mBaseData.lastClose / (5 - i)) + "%");
                    }
                }
            }
        }

        List<String> yStick = new ArrayList<String>();
        float stick = bean.curMaxTransNum / 10000 / 3;

        yStick.add("0");
        for (int i = 1; i < 3; i++) {
            yStick.add(String.valueOf((3 - i) * stick));
        }
        yStick.add(String.valueOf(bean.curMaxTransNum / 10000));

        List<String> xtitle = new ArrayList<String>();

        if (bean.mTimeList != null && bean.mTimeList.size() > 0) {

            if (bean.mTimeList.size() == 1) {
                String time[] = bean.mTimeList.get(0).split("-");
                if (time != null && time.length == 2) {
                    xtitle.add(time[0] + "");
                    xtitle.add(" ");
                    xtitle.add(" ");
                    xtitle.add(" ");
                    xtitle.add(time[1] + "");

                    if (df == null) {
                        df = new SimpleDateFormat("HH:mm");
                    }

                    try {
                        Date startTime = df.parse(time[0]);
                        Date stopTime = df.parse(time[1]);


                        int count = (int) (stopTime.getTime() - startTime.getTime()) / 1000 / 60;
                        mStockChart.setMaxPointNum(count);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            } else if (bean.mTimeList.size() == 2) {
                String time0[] = bean.mTimeList.get(0).split("-");
                String time1[] = bean.mTimeList.get(1).split("-");
                if (time0 != null && time0.length == 2 && time1 != null && time1.length == 2) {
                    xtitle.add(time0[0] + "");
                    xtitle.add(" ");
                    xtitle.add(time0[1] + "/" + time1[0]);
                    xtitle.add(" ");
                    xtitle.add(time1[1] + "");

                    if (df == null) {
                        df = new SimpleDateFormat("HH:mm");
                    }

                    try {
                        Date startTime = df.parse(time0[0]);
                        Date midTime0 = df.parse(time0[1]);
                        Date midTime1 = df.parse(time1[0]);
                        Date stopTime = df.parse(time1[1]);


                        int count = (int) (stopTime.getTime() - midTime1.getTime() + midTime0.getTime() - startTime.getTime()) / 1000 / 60;
                        mStockChart.setMaxPointNum(count);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        } else {
            if (bean.mBaseData.exchange.equalsIgnoreCase("SH") || bean.mBaseData.exchange.equalsIgnoreCase("SZ")) {
                xtitle.add("9:30");
                xtitle.add(" ");
                xtitle.add("11:30/13:00");
                xtitle.add(" ");
                xtitle.add("15:00");
                mStockChart.setMaxPointNum(240);
            } else if (bean.mBaseData.exchange.equalsIgnoreCase("HK")) {
                xtitle.add("9:30");
                xtitle.add(" ");
                xtitle.add("12:00/13:00");
                xtitle.add(" ");
                xtitle.add("16:00");
                mStockChart.setMaxPointNum(330);
            } else if (bean.mBaseData.exchange.equalsIgnoreCase("NASDAQ") || bean.mBaseData.exchange.equalsIgnoreCase("INDEXDJX")
                    || bean.mBaseData.exchange.equalsIgnoreCase("INDEXSP") || bean.mBaseData.exchange.equalsIgnoreCase("INDEXNASDAQ")
                    || bean.mBaseData.exchange.equalsIgnoreCase("NYSE")) {
                xtitle.add("9:30");
                xtitle.add(" ");
                xtitle.add(" ");
                xtitle.add(" ");
                xtitle.add("16:00");
                mStockChart.setMaxPointNum(390);
            }
        }

        mStockChart.setAxisXColor(Color.LTGRAY);
        mStockChart.setAxisYColor(Color.LTGRAY);
        mStockChart.setBorderColor(Color.LTGRAY);
        mStockChart.setAxisYTitles(ytitle);
        mStockChart.setAxisXTitles(xtitle);
        mStockChart.setLongtitudeFontColor(Color.WHITE);
        mStockChart.setLatitudeColor(Color.GRAY);
        mStockChart.setLatitudeFontColor(Color.WHITE);
        mStockChart.setLongitudeColor(Color.GRAY);
        mStockChart.setMaxValue(bean.mBaseData.high);
        mStockChart.setMinValue(bean.mBaseData.low);

        mStockChart.setAxisXendTitles(endTitle);
        mStockChart.setAxisYStickTitles(yStick);

        int fontSize = 10;
        mStockChart.setLongtitudeFontSize(fontSize);
        mStockChart.setLatitudeFontSize(fontSize);


        mPaint.setTextSize(fontSize);
        float fontWidth;
        if (String.valueOf(bean.curMaxTransNum / 10000).length() > String.valueOf(bean.mBaseData.current).length()) {
            fontWidth = mPaint.measureText(String.valueOf(bean.curMaxTransNum / 10000)) + MathUtil.getInstance().dip2px(mContext, 3);
        } else {
            fontWidth = mPaint.measureText(String.valueOf(bean.mBaseData.current)) + MathUtil.getInstance().dip2px(mContext, 3);
        }
        mStockChart.setAxisMarginLeft(fontWidth + 5);

        mStockChart.setDisplayAxisXTitle(true);
        mStockChart.setDisplayAxisYTitle(true);
        mStockChart.setDisplayLatitude(true);
        mStockChart.setDisplayLongitude(true);

        mStockChart.setStockBean(bean);
    }


    private OnClickListener stockListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            UITimer.getInstance().executeUITask(new UITimerTask(), UITimer.DELAY_MIDDLE , !zoomStatus);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mStockChart.getLayoutParams();
            if (!zoomStatus) {
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                mStockChart.setShowEndTitles(true);
                float fontWidth = mPaint.measureText("+10.00%");
                mStockChart.setAxisMarginRight(fontWidth + 5);
                float height = 50 / 142.0f * mStockChartLayout.getMeasuredHeight() + MathUtil.getInstance().dip2px(mContext, 25);
                mStockChart.setAxisMarginBottom(height);

                mStockDes.setVisibility(View.GONE);
                mStockDetail.setVisibility(View.GONE);
                mStockDesZoom.setVisibility(View.VISIBLE);

                zoomStatus = true;

            } else {

                layoutParams.width = MathUtil.getInstance().dip2px(mContext, 193);
                layoutParams.height = MathUtil.getInstance().dip2px(mContext, 142);
                mStockChart.setShowEndTitles(false);
                mStockChart.setAxisMarginRight(1);
                mStockChart.setAxisMarginBottom(MathUtil.getInstance().dip2px(mContext, 50));

                mStockDes.setVisibility(View.VISIBLE);
                mStockDetail.setVisibility(View.VISIBLE);
                mStockDesZoom.setVisibility(View.GONE);
                zoomStatus = false;
            }
        }
    };


}

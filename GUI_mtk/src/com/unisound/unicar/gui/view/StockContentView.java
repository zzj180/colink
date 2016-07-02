package com.unisound.unicar.gui.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.StockInfo;
import com.unisound.unicar.gui.preference.AssistantPreference;
import com.unisound.unicar.gui.utils.ImageDownloader;

public class StockContentView extends FrameLayout implements ISessionView {
    public static final String TAG = "StockContentView";

    private TextView mTextViewName, mTextViewCode;
    private TextView mTextViewCurrentPrice, mTextViewChangeAmount, mTextViewChangeRate,
            mTextViewHighestPrice, mTextViewLowestPrice, mTextViewYesterdayClosingPrice,
            mTextViewTodayOpeningPrice;
    private ImageView /* mImgTrend, */mImgChart;

    private final int mColorUp, mColorDown;
    private ImageDownloader mImageDownloader;

    public StockContentView(Context context) {
        super(context);
        mImageDownloader =
                new ImageDownloader(AssistantPreference.FOLDER_PACKAGE_CACHE
                        + AssistantPreference.FOLDER_IMG, 0);
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.stock_content_view, this, true);
        findViews();
        Resources res = getResources();
        mColorUp = res.getColor(R.color.red);
        mColorDown = res.getColor(R.color.gren);
        setListener();
    }

    private void findViews() {
        mTextViewName = (TextView) findViewById(R.id.textViewStockName);
        mTextViewCode = (TextView) findViewById(R.id.textViewStockCode);
        mTextViewCurrentPrice = (TextView) findViewById(R.id.textViewStockCurrentPrice);
        mTextViewChangeAmount = (TextView) findViewById(R.id.textViewStockChangeAmount);
        mTextViewChangeRate = (TextView) findViewById(R.id.textViewStockChangeRate);
        mTextViewHighestPrice = (TextView) findViewById(R.id.textViewStockHighestPrice);
        mTextViewLowestPrice = (TextView) findViewById(R.id.textViewStockLowestPrice);
        mTextViewYesterdayClosingPrice =
                (TextView) findViewById(R.id.textViewStockYesterdayClosingPrice);
        mTextViewTodayOpeningPrice = (TextView) findViewById(R.id.textViewStockTodayOpeningPrice);
        // mImgTrend = (ImageView) findViewById(R.id.imgViewStockTrend);
        mImgChart = (ImageView) findViewById(R.id.imgViewStockChart);
        // mImgTrend.setVisibility(View.GONE);
    }

    private void setListener() {}

    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public void release() {

    }

    public void updateUI(StockInfo result) {
        mTextViewName.setText(result.getName());
        mTextViewCode.setText("(" + result.getCode() + ")");

        mTextViewCurrentPrice.setText(result.getCurrentPrice());
        // mTextViewChangeAmount.setText(String.valueOf(result.getChangeAmount()));
        // mTextViewChangeRate.setText(result.getChangeRate() + "%");

        mTextViewTodayOpeningPrice.setText(getResources().getString(R.string.stock_pening_price,
                result.getTodayOpeningPrice()));
        mTextViewYesterdayClosingPrice.setText(getResources().getString(
                R.string.stock_closing_price, result.getYesterdayClosingPrice()));
        mTextViewHighestPrice.setText(getResources().getString(R.string.stock_highest_price,
                result.getHighestPrice()));
        mTextViewLowestPrice.setText(getResources().getString(R.string.stock_lowest_price,
                result.getLowestPrice()));
        // mImgTrend.setVisibility(View.VISIBLE);
        if (result.getChangeAmount() > 0) {
            mTextViewCurrentPrice.setTextColor(mColorUp);
            // mImgTrend.setImageResource(R.drawable.ic_stock_trend_up);
            mTextViewChangeAmount.setTextColor(mColorUp);
            mTextViewChangeRate.setTextColor(mColorUp);
            mTextViewChangeAmount.setText("上涨:" + result.getChangeAmount());
            mTextViewChangeRate.setText("涨幅:" + result.getChangeRate() + "%");
        } else {
            mTextViewCurrentPrice.setTextColor(mColorDown);
            // mImgTrend.setImageResource(R.drawable.ic_stock_trend_down);
            mTextViewChangeAmount.setTextColor(mColorDown);
            mTextViewChangeRate.setTextColor(mColorDown);
            mTextViewChangeAmount.setText("下跌:" + result.getChangeAmount());
            mTextViewChangeRate.setText("跌幅:" + result.getChangeRate() + "%");
        }

        mImageDownloader.download(result.getChartImgUrl(), mImgChart, true,
                ImageDownloader.SCALE_BY_IMAGEVIEW_WIDTH);
    }
}

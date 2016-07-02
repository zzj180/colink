/**
 * Copyright (c) 2012-2015 Yunzhisheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MusicResultView.java
 * @ProjectName : UnicarGUI
 * @PakageName : com.unisound.unicar.gui.view
 * @Author : xiaodong
 * @CreateDate : 20150828
 */
package com.unisound.unicar.gui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.Logger;

/**
 * MusicResultView
 * 
 * @author xiaodong
 * @date 20150828
 */
public class MusicResultView extends FrameLayout implements ISessionView {

    public static final String TAG = MusicResultView.class.getSimpleName();

    private TextView mTvMusicTitle;
    private LinearLayout mMusicContentView;

    private TextView mTvMusicName, mTvArtist;

    public MusicResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Logger.d(TAG, "!--->-----MusicResultView--------");
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.music_result_view, this, true);
        findViews();
    }

    public MusicResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicResultView(Context context) {
        this(context, null);
    }

    private void findViews() {
        mTvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
        mMusicContentView = (LinearLayout) findViewById(R.id.ll_music_content);
        mTvMusicName = (TextView) findViewById(R.id.tv_music_name);
        mTvArtist = (TextView) findViewById(R.id.tv_artist);
    }

    public void initView(String musicName, String artist) {
        Logger.d(TAG, "!--->initView-----musicName = " + musicName + "; artist = " + artist);
        mTvMusicName.setText(musicName);
        mTvArtist.setText(artist);

        if (TextUtils.isEmpty(musicName) && TextUtils.isEmpty(artist)) {
            mTvMusicTitle.setText(R.string.music_title_result_unknow);
            mMusicContentView.setVisibility(View.GONE);
        } else {
            mTvMusicTitle.setText(R.string.music_title_result_play);
            mMusicContentView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void release() {

    }

    @Override
    public boolean isTemporary() {
        return true;
    }

}

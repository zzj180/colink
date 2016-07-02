/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : MusicShowSession.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.session
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 */
package com.unisound.unicar.gui.session;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.preference.SessionPreference;
import com.unisound.unicar.gui.utils.Logger;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-3
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-3
 * @Modified: 2013-9-3: 实现基本功能
 */
public class UIHandleShowSession extends BaseSession {

    public static final String TAG = "UIHandleShowSession";


    /**
     * @Author : Dancindream
     * @CreateDate : 2013-9-3
     * @param context
     * @param sessionManagerHandler
     */
    public UIHandleShowSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void putProtocol(JSONObject jsonProtocol) {
        super.putProtocol(jsonProtocol);
        addQuestionViewText(mQuestion);

        if (SessionPreference.DOMAIN_ROUTE.equals(mOriginType)
                && SessionPreference.DOMAIN_CODE_FAVORITE_ROUTE.equals(mOriginCode)) {
          /*  String poiVendor =
                    PrivatePreference.getValue("poi_vendor", UserPreference.MAP_VALUE_AMAP);
            JSONObject resultObject = JsonTool.getJSONObject(mDataObject, "result");
            String toPoi = JsonTool.getJsonValue(resultObject, "toPoi");*/
            // if ("AMAP".equals(poiVendor) || "GAODE".equals(poiVendor)) {
            // mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT,
            // mContext.getString(R.string.gaodemap_or_navigation), toPoi);
            // } else if ("BAIDU".equals(poiVendor)) {
            // mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT,
            // mContext.getString(R.string.baidumap_or_navigation), toPoi);
            // } else {
            // mAnswer = mTTS = DataTool.formatString(FATVORITE_ROUTE_FORMAT,
            // mContext.getString(R.string.map_or_navigation), toPoi);
            // }
            addAnswerViewText(mAnswer);
        }

        /*
         * else { String originType = JsonTool.getJsonValue(jsonProtocol,
         * SessionPreference.KEY_ORIGIN_TYPE, ""); if
         * (originType.equals(SessionPreference.DOMAIN_MUSIC)) { dataObject =
         * JsonTool.getJSONObject(jsonProtocol, SessionPreference.KEY_DATA); }
         * addAnswerViewText(mAnswer); }
         */
    }

    @Override
    public void onTTSEnd() {
        Logger.d(TAG, "onTTSEnd");
        super.onTTSEnd();
        mSessionManagerHandler.sendEmptyMessage(SessionPreference.MESSAGE_SESSION_DONE);

        /*
         * MessageSender messageSender = new MessageSender(mContext); if (dataObject != null) {
         * String title = JsonTool.getJsonValue(dataObject, "song"); String artist =
         * JsonTool.getJsonValue(dataObject, "artist"); String album =
         * JsonTool.getJsonValue(dataObject, "album");
         * 
         * TrackInfo track = new TrackInfo(); track.setTitle(title); track.setArtist(artist);
         * track.setAlbum(album); mAnswer = mContext.getString(R.string.for_find) +
         * JsonTool.getJsonValue(dataObject, "keyword"); Bundle bundle = new Bundle();
         * bundle.putParcelable("track", track); Intent intent = new
         * Intent(CommandPreference.ACTION_MUSIC_DATA); intent.putExtras(bundle);
         * messageSender.sendOrderedMessage(intent, null); } else { mAnswer =
         * mContext.getString(R.string.open_music); Intent intent = new
         * Intent(CommandPreference.SERVICECMD); intent.putExtra(CommandPreference.CMDNAME,
         * CommandPreference.CMDOPEN_MUSIC); messageSender.sendMessage(intent, null); }
         */

    }
}

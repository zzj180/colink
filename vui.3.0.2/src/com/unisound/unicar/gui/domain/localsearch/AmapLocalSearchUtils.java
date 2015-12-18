package com.unisound.unicar.gui.domain.localsearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import com.unisound.unicar.gui.preference.UserPerferenceUtil;
import com.unisound.unicar.gui.utils.Logger;

public class AmapLocalSearchUtils {
    private static final String TAG = "AmapLocalSearchPreference";

    private static final boolean useAmap = true;
    private final int AMAP_INDEX = 1;
    private final String assertFileName = "localSearchConfig.mg";
    private Context mContext;
    private List<String> keyWordList = new ArrayList<String>();
    private Map<String, String> key2Category = new HashMap<String, String>();

    public AmapLocalSearchUtils(Context mContext) {
        this.mContext = mContext;
        initKeyWordList();
        initKey2CategoryMap();
    }

    private void initKeyWordList() {
        keyWordList = getKeyWordList();
    }

    private List<String> getKeyWordList() {
        try {
            InputStream in = mContext.getAssets().open(assertFileName);
            String[] keyWrodStrings = getFileContent(in);
            for (int i = 0; i < keyWrodStrings.length; i++) {
                keyWordList.add(keyWrodStrings[i]);
            }
            Logger.d(TAG, "keyWordList : " + keyWordList.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyWordList;
    }

    private String[] getFileContent(InputStream in) {
        String[] contents;
        try {
            byte[] fileContent = new byte[in.available()];
            in.read(fileContent);
            in.close();

            String content = new String(fileContent);
            contents = content.split("#");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return contents;
    }


    /**
     * 后续补充
     */
    private void initKey2CategoryMap() {
        String amap_publicInstitution = "公共设施";
        String[] toilet = {"公厕", "厕所", "卫生间", "洗手间"};
        for (int i = 0; i < toilet.length; i++) {
            key2Category.put(toilet[i], amap_publicInstitution);
        }
        Logger.d(TAG, "key2Category : " + key2Category.toString());
    }

    public boolean isNeedUseAmap(Context mContext, String keyWord) {
        Logger.d(TAG, "mapChoose : " + UserPerferenceUtil.getMapChoose(mContext)
                + " [keyWordList-keyword-useAmap] : [" + keyWordList.toString() + "-" + keyWord
                + "-" + useAmap + "]");
        if (AMAP_INDEX == UserPerferenceUtil.getMapChoose(mContext) && isContainKeyword(keyWord)
                && useAmap) {
            return true;
        }
        return false;
    }

    public String getCategory(String keyWord) {
        Logger.d(TAG, "category : " + key2Category.get(keyWord));
        return key2Category.get(keyWord);
    }

    private boolean isContainKeyword(String keyWrod) {
        for (String keyTemp : keyWordList) {
            if (keyTemp.equals(keyWrod)) {
                return true;
            }
        }
        return false;
    }
}

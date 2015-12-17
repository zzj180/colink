/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : PoiInfo.java
 * @ProjectName : vui_location
 * @PakageName : cn.yunzhisheng.vui.modes
 * @Author : Dancindream
 * @CreateDate : 2013-8-23
 */
package com.unisound.unicar.gui.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-8-23
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-8-23
 * @Modified: 2013-8-23: 实现基本功能
 */
public class PoiInfo {

    private static final String TAG = "POIInfo";
    private String mId;
    private String mName;
    private String mBranchName;
    private String mTel;
    private String mEmail;
    private String mPostCode;
    private String mTypeDes;
    private LocationInfo mLocationInfo;
    private int mDistance;// 距离
    private float mRating;
    private String mUrl;
    private String[] mRegions;
    private String[] mCategories;
    private boolean has_coupon;// 是否有优惠券
    private boolean has_deal;// 是否有团购
    private boolean has_online_reservation;// 是否在线订购
    private float avg_price;// 人均
    private String seletItem;
    private String callSelectItem;

    public String getCallSelectItem() {
        return callSelectItem;
    }

    public void setCallSelectItem(String callSelectItem) {
        this.callSelectItem = callSelectItem;
    }

    public String getSeletItem() {
        return seletItem;
    }

    public void setSeletItem(String seletItem) {
        this.seletItem = seletItem;
    }

    public float getAvg_price() {
        return avg_price;
    }

    public void setAvg_price(float avg_price) {
        this.avg_price = avg_price;
    }

    public boolean isHas_coupon() {
        return has_coupon;
    }

    public void setHas_coupon(boolean has_coupon) {
        this.has_coupon = has_coupon;
    }

    public boolean isHas_deal() {
        return has_deal;
    }

    public void setHas_deal(boolean has_deal) {
        this.has_deal = has_deal;
    }

    public boolean isHas_online_reservation() {
        return has_online_reservation;
    }

    public void setHas_online_reservation(boolean has_online_reservation) {
        this.has_online_reservation = has_online_reservation;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getBranchName() {
        return mBranchName;
    }

    public void setBranchName(String mBranchName) {
        this.mBranchName = mBranchName;
    }

    public String getAddress() {
        return mLocationInfo == null ? null : mLocationInfo.getAddress();
    }

    public void setAddress(String address) {
        if (mLocationInfo == null) {
            mLocationInfo = new LocationInfo();
        }
        mLocationInfo.setAddress(address);
    }

    public String getCity() {
        return mLocationInfo == null ? null : mLocationInfo.getCity();
    }

    public void setCity(String city) {
        if (mLocationInfo == null) {
            mLocationInfo = new LocationInfo();
        }
        mLocationInfo.setCity(city);
    }

    public double getLatitude() {
        if (mLocationInfo != null) {
            return mLocationInfo.getLatitude();
        }
        return 0;
    }

    public double getLongitude() {
        if (mLocationInfo != null) {
            return mLocationInfo.getLongitude();
        }
        return 0;
    }

    public String getTel() {
        return mTel;
    }

    public void setTel(String mTel) {
        this.mTel = mTel;
    }

    public LocationInfo getLocationInfo() {
        return mLocationInfo;
    }

    public void setLocationInfo(LocationInfo mLocationInfo) {
        this.mLocationInfo = mLocationInfo;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int mDistance) {
        this.mDistance = mDistance;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String[] getRegions() {
        return mRegions;
    }

    public void setRegions(String[] mRegions) {
        this.mRegions = mRegions;
    }

    public String[] getCategories() {
        return mCategories;
    }

    public void setCategories(String[] mCategories) {
        this.mCategories = mCategories;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPostCode() {
        return mPostCode;
    }

    public void setPostCode(String mPostCode) {
        this.mPostCode = mPostCode;
    }

    public String getTypeDes() {
        if (TextUtils.isEmpty(mTypeDes)) {
            if (mCategories != null && mCategories.length > 0) {
                return mCategories.toString();
            }
        }
        return mTypeDes;
    }

    public void setTypeDes(String mTypeDes) {
        this.mTypeDes = mTypeDes;
    }

    public JSONObject parse2JSONObj() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", mId);
            jsonObj.put("name", mName);
            jsonObj.put("branchName", mBranchName);
            jsonObj.put("tel", mTel);
            jsonObj.put("postCode", mPostCode);
            jsonObj.put("typeDes", mTypeDes);
            jsonObj.put("has_coupon", has_coupon);
            jsonObj.put("has_deal", has_deal);
            jsonObj.put("has_online_reservation", has_online_reservation);
            jsonObj.put("avg_price", avg_price);
            if (mLocationInfo != null) {
                jsonObj.put("location", mLocationInfo.parse2JSONObj());
            }

            jsonObj.put("distance", mDistance);
            jsonObj.put("rate", mRating);
            jsonObj.put("url", mUrl);
            if (mRegions != null) {
                JSONArray jsonRegionArr = new JSONArray();
                for (String i : mRegions) {
                    jsonRegionArr.put(i);
                }
                jsonObj.put("region", jsonRegionArr);
            }
            if (mCategories != null) {
                JSONArray jsonCategoryArr = new JSONArray();
                for (String i : mCategories) {
                    jsonCategoryArr.put(i);
                }
                jsonObj.put("category", jsonCategoryArr);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonObj;
    }

    @Override
    public String toString() {
        return parse2JSONObj().toString();
    }

}

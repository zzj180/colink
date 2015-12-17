/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : LocationInfo.java
 * @ProjectName : vui_location
 * @PakageName : cn.yunzhisheng.vui.model
 * @Author : Dancindream
 * @CreateDate : 2013-8-9
 */
package com.unisound.unicar.gui.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-8-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-8-9
 * @Modified: 2013-8-9: 实现基本功能
 */
public class LocationInfo {
    public static final int GPS_ORIGIN = 0;
    public static final int GPS_GOOGLE = 2;
    public static final int GPS_BAIDU = 4;
    public static final int GPS_GAODE = 5;

    private int mType = 4;
    /** 纬度 */
    private double mLatitude = 0.0;
    /** 经度 */
    private double mLongitude = 0.0;

    private String mName;
    // 省份,如“河北省”，直辖市城市为空
    private String mProvince;
    // 城市名称
    private String mCity;
    // 城市编码
    private String mCityCode;
    // 区域
    private String mDistrict;
    // 街道
    private String mStreet;
    // 地址
    private String mAddress;
    // 详细地址
    private String mProvider;
    private long mTime = 0;
    private String mAddressDetail;
    private boolean mHasAltitude = false;
    private double mAltitude = 0.0f;
    private boolean mHasSpeed = false;
    private float mSpeed = 0.0f;
    private boolean mHasBearing = false;
    private float mBearing = 0.0f;
    private boolean mHasAccuracy = false;
    private float mAccuracy = 0.0f;
    
    private String mCondition;

	public String getmCondition() {
		return mCondition;
	}

	public void setmCondition(String mCondition) {
		this.mCondition = mCondition;
	}

	public LocationInfo() {
	}

	public LocationInfo(LocationInfo l) {
		if (l == null) {
			return;
		}
		this.mType = l.mType;
		this.mLatitude = l.mLatitude;
		this.mLongitude = l.mLongitude;
		this.mName = l.mName;
		this.mProvince = l.mProvince;
		this.mCity = l.mCity;
		this.mCityCode = l.mCityCode;
		this.mDistrict = l.mDistrict;
		this.mStreet = l.mStreet;
		this.mAddress = l.mAddress;
		this.mAddressDetail = l.mAddressDetail;
		this.mCondition = l.mCondition;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public String getProvince() {
		return mProvince;
	}

	public void setProvince(String province) {
		mProvince = province;
	}

	public String getCityCode() {
		return mCityCode;
	}

	public void setCityCode(String mCityCode) {
		this.mCityCode = mCityCode;
	}

	public String getDistrict() {
		return mDistrict;
	}

	public void setDistrict(String mDistrict) {
		this.mDistrict = mDistrict;
	}

	public String getStreet() {
		return mStreet;
	}

	public void setStreet(String mStreet) {
		this.mStreet = mStreet;
	}

	public String getAddressDetail() {
		return mAddressDetail;
	}

	public void setAddressDetail(String address) {
		mAddressDetail = address;
	}
	

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String provider) {
        mProvider = provider;
    }

    public long getTime() {
        return mTime;
    }

    public boolean hasAccuracy() {
        return mHasAccuracy;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public float getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(float accuracy) {
        mAccuracy = accuracy;
        mHasAccuracy = true;
    }

    public boolean hasAltitude() {
        return mHasAltitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double altitude) {
        mAltitude = altitude;
        mHasAltitude = true;
    }

    public void removeAltitude() {
        mAltitude = 0.0f;
        mHasAltitude = false;
    }

    public boolean hasSpeed() {
        return mHasSpeed;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
        mHasSpeed = true;
    }

    public void removeSpeed() {
        mSpeed = 0.0f;
        mHasSpeed = false;
    }

    public boolean hasBearing() {
        return mHasBearing;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float bearing) {
        while (bearing < 0.0f) {
            bearing += 360.0f;
        }
        while (bearing >= 360.0f) {
            bearing -= 360.0f;
        }
        mBearing = bearing;
        mHasBearing = true;
    }

    public void removeBearing() {
        mBearing = 0.0f;
        mHasBearing = false;
    }

	public double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}

	public double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}

	public String getCity() {
		return mCity;
	}

	public void setCity(String city) {
		if (city != null && city.lastIndexOf("市") > 0) {
			city = city.substring(0, city.lastIndexOf("市"));
		}
		this.mCity = city;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public void setAddress(String address) {
		mAddress = address;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}

	public JSONObject parse2JSONObj() {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("type", mType);
//			jsonObj.put("provider", getProvider());
//			jsonObj.put("accuracy", getAccuracy());
//			jsonObj.put("altitude", getAltitude());
//			jsonObj.put("time", getTime());
//			jsonObj.put("bearing", getBearing());
//			jsonObj.put("speed", getSpeed());

			jsonObj.put("name", mName);
			jsonObj.put("province", mProvince);
			jsonObj.put("city", mCity);
			jsonObj.put("cityCode", mCityCode);
			jsonObj.put("destrict", mDistrict);
			jsonObj.put("street", mStreet);
			jsonObj.put("address", mAddress);
			jsonObj.put("addressDetail", mAddressDetail);

			jsonObj.put("condition", mCondition);
			jsonObj.put("lat", getLatitude());
			jsonObj.put("lng", getLongitude());
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

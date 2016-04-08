package com.aispeech.aios.adapter.bean;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PoiBean extends BaseBean {

    private static final long serialVersionUID = 1L;

    private String name;
    private String poiId;
    private double latitude;
    private double longitude;
    private String address;
    private long distance;
    private String mTelephone;
    private String displayDistance;//页面用来显示的字段：如： 18.25Km
    private String cityName;//城市名

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDisplayDistance() {
        return displayDistance;
    }

    public void setDisplayDistance(String display) {
        this.displayDistance = display;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getTelephone() {
        return mTelephone;
    }

    public void setTelephone(String telephone) {
        this.mTelephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toJson2() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("name", getName());
        json.put("latitude", latitude);
        json.put("longitude", longitude);
        json.put("address", address);
        json.put("distance", distance);
        json.put("tel", mTelephone);

        return json.toString();
    }

    @Override
    public String toString() {
        return "PoiBean{" +
                "name="+getName()+'\'' +
                "poiId='" + poiId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", distance=" + distance +
                ", mTelephone='" + mTelephone + '\'' +
                '}';
    }
}

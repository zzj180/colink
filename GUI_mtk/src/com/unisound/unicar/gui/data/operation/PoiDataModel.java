package com.unisound.unicar.gui.data.operation;

public class PoiDataModel {
    private double latitude;
    private double lontitude;
    private String city;
    private String poi;
    private String category;
    private int radius;

    public PoiDataModel() {
        super();
    }

    public PoiDataModel(double latitude, double lontitude, String city, String poi,
            String category, int radius) {
        super();
        this.latitude = latitude;
        this.lontitude = lontitude;
        this.city = city;
        this.poi = poi;
        this.category = category;
        this.radius = radius;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLontitude() {
        return lontitude;
    }

    public void setLontitude(double lontitude) {
        this.lontitude = lontitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "PoiDataModel [latitude=" + latitude + ", lontitude=" + lontitude + ", city=" + city
                + ", poi=" + poi + ", category=" + category + ", radius=" + radius + "]";
    }
}

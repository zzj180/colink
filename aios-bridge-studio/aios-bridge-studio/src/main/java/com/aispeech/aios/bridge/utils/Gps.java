package com.aispeech.aios.bridge.utils;

/**
 * @desc gps坐标实体类
 * @auth ZZJ
 * @date 2016-03-20
 */
public class Gps {
	private double lat;
	private double lon;
	public Gps(double lat, double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}
	public double getWgLon() {
		return lon;
	}
	public double getWgLat() {
		// TODO Auto-generated method stub
		return lat;
	}
}

package com.unisound.unicar.gui.utils;


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

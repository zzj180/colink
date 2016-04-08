package com.aispeech.aios.adapter.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

public class AssetsXmlUtil {

	private static StringBuffer buffer = new StringBuffer();

	public static String readXmlFile(Context context, String fname) {
		AssetManager manager = context.getAssets();
		if(manager != null) {
			try {
				InputStream in = manager.open(fname);
				if(in != null) {
					String result = parseXmlFile(in);
					return result;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}


	private static String parseXmlFile(InputStream in) throws Exception{
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(in, "utf-8");
		int eveTag = xpp.getEventType();

		while(eveTag != XmlPullParser.END_DOCUMENT) {
			switch(eveTag) {
				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:
					String name = xpp.getName();
					if(name.equals("configs") && buffer != null) {
						buffer = new StringBuffer();
						buffer.append("{");
					} else if(name.equals("config") && buffer != null) {
						if(xpp.getAttributeCount() == 1) {
							buffer.append("\"" + xpp.getAttributeValue(0) + "\""+":{");
						}
					} else if(name.equals("item") && buffer != null) {
						if(xpp.getAttributeCount() == 2) {
							buffer.append("\"" + xpp.getAttributeValue(0) + "\""+ ":" + "" + xpp.getAttributeValue(1) +"" + ",");
						}
					}

					break;

				case XmlPullParser.END_TAG:
					if(xpp.getDepth() == 2) {
						buffer.deleteCharAt(buffer.length() - 1);
						buffer.append("},");
					}
					break;

				case XmlPullParser.TEXT:
					break;

				default:
					break;

			}

			eveTag = xpp.next();
		}

		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append("}");

		return buffer.toString();
	}

	public static String getBasicAttrs(String jsonstr) {
		try {
			JSONObject object = new JSONObject(jsonstr);
			if(object != null && !object.isNull("basic")) {
				return object.optJSONObject("basic").toString();
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getClusterStatus(String jsonstr) {
		try {
			JSONObject object = new JSONObject(jsonstr);
			if(object != null && !object.isNull("cluster")) {
				JSONObject json = object.optJSONObject("cluster");
				if(json != null) {
					String status = json.optString("cluster");
					return status;
				}

			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return "disable";
	}

	public static int getTtsVolume(String jsonstr) {
		try {
			JSONObject object = new JSONObject(jsonstr);
			if(object != null && !object.isNull("volume")) {
				JSONObject json = object.optJSONObject("volume");
				if(json != null) {
					int volume = json.optInt("volume");
					return volume;
				}

			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return 50;
	}

}
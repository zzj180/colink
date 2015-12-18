package com.unisound.unicar.gui.utils;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import cn.yunzhisheng.vui.assistant.WindowService;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.SupportDomain;
import com.unisound.unicar.gui.preference.SessionPreference;

/**
 * 
 * @author XiaoDong
 * @date 20150722
 */
public class FunctionHelpUtil {
	
	private static final String TAG = FunctionHelpUtil.class.getSimpleName();
	
	/**
	 * get MainPage HelpText List
	 * @param context
	 * @param isOnline
	 * @return
	 */
	public static ArrayList<String> getMainPageHelpTextList(Context context, boolean isOnline){
		ArrayList<String> helpTextList = new ArrayList<String>();
		String[] callArray =  context.getResources().getStringArray(isOnline ? 
				R.array.function_example_call : R.array.function_example_call_offline);
		String[] musicArray =  context.getResources().getStringArray(isOnline ? 
				R.array.function_example_music : R.array.function_example_music_offline);
		String[] routeArray =  context.getResources().getStringArray(isOnline ? 
				R.array.function_example_route : R.array.function_example_route_offline);
		int maxLenth = ArithmeticUtil.getMaxNumber(callArray.length, musicArray.length, routeArray.length);
		for (int i = 0; i < maxLenth; i++) {
			if(callArray.length > i){
				String callHelpText = checkHelpTextByType(context, SessionPreference.DOMAIN_CALL, callArray[i]);
				helpTextList.add(addDoubleQuotationMarks(callHelpText));
			}
			if(musicArray.length > i){
				helpTextList.add(addDoubleQuotationMarks(musicArray[i]));
			}
			if(routeArray.length > i){
				helpTextList.add(addDoubleQuotationMarks(routeArray[i]));
			}
		}
		return helpTextList;
	}
	
	/* < XD 20150807 added for unsupported domain help text Begin */
	private static ArrayList<SupportDomain> mSupprotDomainList = null;
	
	/**
	 * get function help text list by network state 
	 * for Unsupported Domain Help View & Mic Recording Session View
	 * @param mContext
	 * @return ArrayList<SupportDomain>
	 */
	public static ArrayList<SupportDomain> getUnsupportedHelpTextList(Context mContext){
		if(mSupprotDomainList != null){
			mSupprotDomainList.clear();
		}
		mSupprotDomainList = new ArrayList<SupportDomain>();
		
		Network.checkNetworkConnected(mContext);
		boolean hasNetWork = Network.hasNetWorkConnect();

		ArrayList<String> supportList = WindowService.getSupportList(hasNetWork);
		Logger.d(TAG, "!--->getUnsupportedHelpTextList:--hasNetWork="+hasNetWork+";-supportList size = "+supportList.size());

		//boolean hasContactWithName = ContactsUtil.hasContactWithName(mContext); //xd added 20150715
		//Logger.d(TAG, "!--->initFunctionViews:---hasContactWithName = " +hasContactWithName);
		// Call
		if (isSupport(SessionPreference.DOMAIN_CALL, supportList)) {
				if (hasNetWork) {
					addFunctionItem(SessionPreference.DOMAIN_CALL,
							R.array.function_example_call);
				} else {
					addFunctionItem(SessionPreference.DOMAIN_CALL,
							R.array.function_example_call_offline);
				}
		}
		
		// MapRoute
		if (isSupport(SessionPreference.DOMAIN_ROUTE, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_ROUTE,
						R.array.function_example_route);
			} /*else {
				addFunctionItem(SessionPreference.DOMAIN_ROUTE,
						R.array.function_example_route_offline);
			}*/
		}

		// Music
		if (isSupport(SessionPreference.DOMAIN_MUSIC, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_MUSIC,R.array.function_example_music);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_MUSIC,R.array.function_example_music_offline);
			}
		}
		
		// Setting
		if (isSupport(SessionPreference.DOMAIN_SETTING, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_SETTING,R.array.function_example_setting);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_SETTING,R.array.function_example_setting_offline);
			}
		}

		// Broadcast
		if (isSupport(SessionPreference.DOMAIN_BROADCAST, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_BROADCAST,R.array.function_example_broadcast);
			} else {
				addFunctionItem(SessionPreference.DOMAIN_BROADCAST,R.array.function_example_broadcast_offline);
			}
		}

		// Weather
		if (isSupport(SessionPreference.DOMAIN_WEATHER, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_WEATHER,R.array.function_example_weather);
			}
		}

		// stock
		if (isSupport(SessionPreference.DOMAIN_STOCK, supportList)) {
		      addFunctionItem( SessionPreference.DOMAIN_STOCK,R.array.function_example_stock);
		  }
		
		// SMS
/*		if (isSupport(SessionPreference.DOMAIN_SMS, supportList)) {
			addFunctionItem(SessionPreference.DOMAIN_SMS,
					R.array.function_example_sms);
		}*/
		
		// Local
		if (isSupport(SessionPreference.DOMAIN_LOCAL, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_LOCAL,R.array.help_child_content_local_search_online);
			}
		}
		
		// Traffic
		if (isSupport(SessionPreference.DOMAIN_TRAFFIC, supportList)) {
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_TRAFFIC,R.array.help_child_content_traffic_online);
			}
		}
		
		// Limit
		if (isSupport(SessionPreference.DOMAIN_LIMIT, supportList)) {
			Logger.d(TAG,"!--->Support DOMAIN_LIMIT");
			if (hasNetWork) {
				addFunctionItem(SessionPreference.DOMAIN_LIMIT,R.array.help_child_content_limit_online);
			}
		}
		return mSupprotDomainList;
	}
	
    private static void addFunctionItem(String functionTag, int functionRes) {
		if(null == mSupprotDomainList){
			return;
		}
		SupportDomain sd = new SupportDomain();
		sd.type = functionTag;
		sd.resourceId = functionRes;
		Logger.d(TAG, "!--->addFunctionItem----: type = "+sd.type);
		mSupprotDomainList.add(sd);
	}
	
	private static boolean isSupport(String domain, ArrayList<String> list) {
		return isSupport(domain, "", list);
	}

	private static boolean isSupport(String domain, String code, ArrayList<String> list) {
		String sign = domain;
		if (code != null && !code.equals("")) {
			sign += "," + code;
		}

		if (list != null) {
			for (String line : list) {
				if (line != null && line.startsWith(sign)) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;
	}
	/* XD 20150807 added for unsupported domain help text End > */
	
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String addDoubleQuotationMarks(String text){
		if(TextUtils.isEmpty(text)){
			return text;
		} 
		if (text.startsWith("“") && text.endsWith("”")) {
			return text;
		} else {
			text = "“"+text+"”";
			return text;
		}
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String removeDoubleQuotationMarks(String text){
		if(TextUtils.isEmpty(text)){
			return text;
		} 
		if (text.startsWith("“") && text.endsWith("”")) {
			text = text.substring(text.indexOf("“")+1, text.lastIndexOf("”"));
		}
		return text;
	}
	
	/**
	 * 
	 * @param context
	 * @param domainType
	 * @param domainHelpText
	 * @return
	 */
	public static String checkHelpTextByType(Context context, String domainType, String domainHelpText){
		if(SessionPreference.DOMAIN_CALL.equals(domainType)
				|| SessionPreference.DOMAIN_SMS.equals(domainType)){
			String contactName = ContactsUtil.getRandomContactName(context);
			if ("".equals(contactName) || null == contactName) {
				Logger.d(TAG, "no contact find, use default contact name for call & sms");
				//if no contact find, use default domain Help Text for call & sms
				contactName = context.getString(R.string.contact_name_default);
			} 
			Object[] nameFormatParam = new Object[1];
			nameFormatParam[0] = contactName;
		    domainHelpText = String.format(domainHelpText, nameFormatParam);
		} 
		return domainHelpText;
	}

}

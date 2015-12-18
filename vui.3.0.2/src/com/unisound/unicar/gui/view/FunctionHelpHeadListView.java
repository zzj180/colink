package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.adapter.FunctionHelpViewAdapter;
import com.unisound.unicar.gui.model.FunctionHelpTextInfo;
import com.unisound.unicar.gui.model.KnowledgeMode;
import com.unisound.unicar.gui.model.SupportDomain;
import com.unisound.unicar.gui.utils.ArithmeticUtil;
import com.unisound.unicar.gui.utils.FunctionHelpUtil;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Function Help Head & ListView
 * 
 * @author xiaodong
 * @date 20150716
 */
public class FunctionHelpHeadListView extends LinearLayout implements
		ISessionView {

	private static final String TAG = FunctionHelpHeadListView.class
			.getSimpleName();

	private Context mContext;
	protected LayoutInflater mLayoutInflater;

	private ArrayList<SupportDomain> mSupprotDomainList = new ArrayList<SupportDomain>();

	private boolean isShowRandomHelpText = false;

	private ListView mListView;
	private FunctionHelpViewAdapter mFunctionHelpAdapter;

	/**
	 * key: support domain value: help text
	 */
	// Map<String, String> mSupportItemMap = new LinkedHashMap<String,String>();

	// private ArrayList<String> showTexts = new ArrayList<String>();

	public FunctionHelpHeadListView(Context context) {
		super(context);
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutInflater.inflate(R.layout.layout_head_listview, this, true);

		findViews();
	}

	public void findViews() {
		Logger.d(TAG, "!--->---findViews()--------");
		initFunctionViews();

		TextView tvHead = (TextView) findViewById(R.id.tv_header_title);
		tvHead.setText(R.string.function_help_view_title);

		mListView = (ListView) findViewById(R.id.list_view_content);
		mFunctionHelpAdapter = new FunctionHelpViewAdapter(mContext,
				getHelpTextInfoList(isShowRandomHelpText));
		mListView.setAdapter(mFunctionHelpAdapter);
		mListView.bringToFront();
	}

	public void initFunctionViews() {
		Logger.d(TAG, "!--->initFunctionHelpHeadListView---------");
		// removeAllViews();

		if (mSupprotDomainList.size() > 0) {
			mSupprotDomainList.clear();
		}

		// if(mSupportItemMap.size() > 0){
		// mSupportItemMap.clear();
		// }

		// XD 20150807 modify
		mSupprotDomainList = FunctionHelpUtil
				.getUnsupportedHelpTextList(mContext);
	}

	/**
	 * create Show Text
	 * 
	 * @param isShowRandomHelpText
	 *            default： false; show resourceId arr[0] help text if true ,
	 *            must satisfy the needs of: resourceId arr[].size() > 1
	 */
	private ArrayList<FunctionHelpTextInfo> getHelpTextInfoList(
			boolean isShowRandomHelpText) {
		ArrayList<FunctionHelpTextInfo> list = new ArrayList<FunctionHelpTextInfo>();

		Logger.d(TAG, "!--->getHelpTextInfoList()---isShowRandomHelpText--"
				+ isShowRandomHelpText + "supprotList.size()  :"
				+ mSupprotDomainList.size());
		if (mSupprotDomainList.size() > 0) {
			int size = mSupprotDomainList.size();
			// int number = mResource.getInteger(R.integer.show_text_number);
			// int showNo = size > number ? number : size;
			int showNo = size;

			int[] numberArray = ArithmeticUtil.getRandomArray(0, size - 1,
					showNo);

			for (int i = 0; i < numberArray.length; i++) {
				SupportDomain sd = null;
				String s = "";
				if (isShowRandomHelpText) {
					sd = mSupprotDomainList.get(numberArray[i]);
					s = KnowledgeMode.getRandomContentString(getContext(),
							sd.resourceId);
					Logger.d(TAG, "!--->getHelpTextInfoList()--i :" + i
							+ " numberArray[i]:" + numberArray[i] + " s" + s);
				} else {
					sd = mSupprotDomainList.get(i);
					s = mContext.getResources().getStringArray(sd.resourceId)[0];
				}
				// showTexts.add(s);
				s = FunctionHelpUtil.removeDoubleQuotationMarks(s);// xd added
																	// 20150729
				Logger.d(TAG, "!--->getHelpTextInfoList()--type :" + sd.type
						+ "; s =" + s);

				FunctionHelpTextInfo info = new FunctionHelpTextInfo();
				info.setType(sd.type);
				info.setHelpText(s);
				list.add(info);

				// mSupportItemMap.put(sd.type, s);
			}

		}
		Logger.d(TAG, "!--->getHelpTextInfoList---list size = " + list.size());
		return list;
	}

	@Override
	public boolean isTemporary() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		Logger.d(TAG, "!--->release----");
		if (null != mSupprotDomainList && mSupprotDomainList.size() > 0) {
			mSupprotDomainList.clear();
		}
	}

}
